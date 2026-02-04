package com.jyr.system.service.impl;

import com.jyr.system.dto.request.DocumentDetailRequest;
import com.jyr.system.dto.request.DocumentRequest;
import com.jyr.system.dto.response.DocumentResponse;
import com.jyr.system.entity.*;
import com.jyr.system.enums.*;
import com.jyr.system.exception.*;
import com.jyr.system.repository.*;
import com.jyr.system.util.EntityMapper;
import com.jyr.system.util.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public DocumentResponse findById(Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", id));
        return EntityMapper.toDocumentResponse(doc);
    }

    @Transactional(readOnly = true)
    public DocumentResponse findByNumber(String number) {
        Document doc = documentRepository.findByDocumentNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado: " + number));
        return EntityMapper.toDocumentResponse(doc);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> findByType(DocumentType type, Pageable pageable) {
        return documentRepository.findByDocumentType(type, pageable)
                .map(EntityMapper::toDocumentResponse);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> findByTypeAndStatus(DocumentType type, DocumentStatus status, Pageable pageable) {
        return documentRepository.findByDocumentTypeAndStatus(type, status, pageable)
                .map(EntityMapper::toDocumentResponse);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findByDateRange(DocumentType type, LocalDate start, LocalDate end) {
        return documentRepository.findByTypeAndDateRange(type, start, end).stream()
                .map(EntityMapper::toDocumentResponse).toList();
    }

    @Transactional
    public DocumentResponse create(DocumentRequest request, String username) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getCustomerId()));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        // Generate document number
        String prefix = request.getDocumentType() == DocumentType.FACTURA ? "FAC-" : "COT-";
        String lastNumber = documentRepository.findLastDocumentNumber(request.getDocumentType());
        String docNumber = SequenceGenerator.generateNext(lastNumber, prefix);

        Document document = Document.builder()
                .documentNumber(docNumber)
                .documentType(request.getDocumentType())
                .status(DocumentStatus.PENDIENTE)
                .documentDate(LocalDate.now())
                .dueDate(request.getDueDate())
                .customer(customer)
                .user(user)
                .discountAmount(request.getDiscountAmount() != null ?
                        request.getDiscountAmount() : BigDecimal.ZERO)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .build();
        document.setActive(true);

        // Process details
        for (DocumentDetailRequest detailReq : request.getDetails()) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detailReq.getProductId()));

            // Validate stock for invoices
            if (request.getDocumentType() == DocumentType.FACTURA) {
                if (product.getStock() < detailReq.getQuantity()) {
                    throw new InsufficientStockException(
                            product.getName(), detailReq.getQuantity(), product.getStock());
                }
            }

            DocumentDetail detail = DocumentDetail.builder()
                    .product(product)
                    .quantity(detailReq.getQuantity())
                    .unitPrice(detailReq.getUnitPrice() != null ?
                            detailReq.getUnitPrice() : product.getSalePrice())
                    .discountPercent(detailReq.getDiscountPercent() != null ?
                            detailReq.getDiscountPercent() : BigDecimal.ZERO)
                    .taxRate(detailReq.getTaxRate() != null ?
                            detailReq.getTaxRate() : product.getTaxRate())
                    .build();
            detail.setActive(true);
            detail.calculateAmounts();
            document.addDetail(detail);
        }

        document.calculateTotals();
        Document saved = documentRepository.save(document);

        // Deduct stock for invoices
        if (request.getDocumentType() == DocumentType.FACTURA) {
            for (DocumentDetail detail : saved.getDetails()) {
                inventoryService.recordMovement(
                        detail.getProduct(),
                        MovementType.SALIDA,
                        detail.getQuantity(),
                        "FACTURA",
                        saved.getId(),
                        "Venta - " + saved.getDocumentNumber(),
                        user
                );
            }
        }

        return EntityMapper.toDocumentResponse(saved);
    }

    @Transactional
    public DocumentResponse updateStatus(Long id, DocumentStatus newStatus, String username) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento", id));

        if (document.getStatus() == DocumentStatus.ANULADA) {
            throw new BusinessException("No se puede modificar un documento anulado");
        }

        // If canceling an invoice, restore stock
        if (newStatus == DocumentStatus.ANULADA &&
            document.getDocumentType() == DocumentType.FACTURA) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            for (DocumentDetail detail : document.getDetails()) {
                inventoryService.recordMovement(
                        detail.getProduct(),
                        MovementType.ENTRADA,
                        detail.getQuantity(),
                        "ANULACION",
                        document.getId(),
                        "Anulación factura - " + document.getDocumentNumber(),
                        user
                );
            }
        }

        document.setStatus(newStatus);
        return EntityMapper.toDocumentResponse(documentRepository.save(document));
    }

    @Transactional
    public DocumentResponse convertQuoteToInvoice(Long quoteId, String username) {
        Document quote = documentRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización", quoteId));

        if (quote.getDocumentType() != DocumentType.COTIZACION) {
            throw new BusinessException("El documento no es una cotización");
        }

        // Create new invoice request from quote data
        DocumentRequest invoiceRequest = new DocumentRequest();
        invoiceRequest.setDocumentType(DocumentType.FACTURA);
        invoiceRequest.setCustomerId(quote.getCustomer().getId());
        invoiceRequest.setDiscountAmount(quote.getDiscountAmount());
        invoiceRequest.setPaymentMethod(quote.getPaymentMethod());
        invoiceRequest.setNotes("Generada desde cotización " + quote.getDocumentNumber());

        List<DocumentDetailRequest> detailRequests = quote.getDetails().stream().map(d -> {
            DocumentDetailRequest dr = new DocumentDetailRequest();
            dr.setProductId(d.getProduct().getId());
            dr.setQuantity(d.getQuantity());
            dr.setUnitPrice(d.getUnitPrice());
            dr.setDiscountPercent(d.getDiscountPercent());
            dr.setTaxRate(d.getTaxRate());
            return dr;
        }).toList();

        invoiceRequest.setDetails(detailRequests);

        return create(invoiceRequest, username);
    }
}
