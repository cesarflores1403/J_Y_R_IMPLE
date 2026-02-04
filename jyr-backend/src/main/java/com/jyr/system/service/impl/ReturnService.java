package com.jyr.system.service.impl;

import com.jyr.system.dto.request.ReturnRequest;
import com.jyr.system.dto.request.ReturnStatusUpdate;
import com.jyr.system.dto.response.ReturnResponse;
import com.jyr.system.entity.*;
import com.jyr.system.enums.*;
import com.jyr.system.exception.*;
import com.jyr.system.repository.*;
import com.jyr.system.util.EntityMapper;
import com.jyr.system.util.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReturnService {

    private final ProductReturnRepository returnRepository;
    private final DocumentRepository documentRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public ReturnResponse findById(Long id) {
        return EntityMapper.toReturnResponse(returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", id)));
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponse> findByStatus(ReturnStatus status, Pageable pageable) {
        return returnRepository.findByStatus(status, pageable).map(EntityMapper::toReturnResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReturnResponse> findAll(Pageable pageable) {
        return returnRepository.findAll(pageable).map(EntityMapper::toReturnResponse);
    }

    @Transactional
    public ReturnResponse create(ReturnRequest request, String username) {
        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento", request.getDocumentId()));

        if (document.getDocumentType() != DocumentType.FACTURA) {
            throw new BusinessException("Solo se pueden devolver productos de facturas");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.getProductId()));

        // Verify the product was in the invoice
        boolean productInInvoice = document.getDetails().stream()
                .anyMatch(d -> d.getProduct().getId().equals(request.getProductId()) &&
                               d.getQuantity() >= request.getQuantity());
        if (!productInInvoice) {
            throw new BusinessException("El producto no está en la factura o la cantidad excede lo facturado");
        }

        String lastNumber = returnRepository.findLastReturnNumber();
        String returnNumber = SequenceGenerator.generateNext(lastNumber, "DEV-");

        // Calculate refund based on unit price in invoice
        BigDecimal unitPrice = document.getDetails().stream()
                .filter(d -> d.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .map(DocumentDetail::getUnitPrice)
                .orElse(BigDecimal.ZERO);
        BigDecimal refundAmount = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        ProductReturn productReturn = ProductReturn.builder()
                .returnNumber(returnNumber)
                .document(document)
                .product(product)
                .customer(document.getCustomer())
                .quantity(request.getQuantity())
                .refundAmount(refundAmount)
                .reason(request.getReason())
                .status(ReturnStatus.SOLICITADA)
                .returnDate(LocalDate.now())
                .build();
        productReturn.setActive(true);

        return EntityMapper.toReturnResponse(returnRepository.save(productReturn));
    }

    @Transactional
    public ReturnResponse updateStatus(Long id, ReturnStatusUpdate update, String username) {
        ProductReturn productReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", id));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        productReturn.setStatus(update.getStatus());
        productReturn.setResolutionNotes(update.getResolutionNotes());
        productReturn.setProcessedBy(user);

        // If approved/completed, return stock
        if (update.getStatus() == ReturnStatus.COMPLETADA) {
            inventoryService.recordMovement(
                    productReturn.getProduct(),
                    MovementType.DEVOLUCION,
                    productReturn.getQuantity(),
                    "DEVOLUCION",
                    productReturn.getId(),
                    "Devolución - " + productReturn.getReturnNumber(),
                    user
            );
        }

        return EntityMapper.toReturnResponse(returnRepository.save(productReturn));
    }

    @Transactional
    public ReturnResponse uploadEvidence(Long id, MultipartFile file) throws IOException {
        ProductReturn productReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución", id));

        Path uploadPath = Paths.get(uploadDir, "returns");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + extension;

        Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        productReturn.setEvidenceUrl("/uploads/images/returns/" + filename);
        return EntityMapper.toReturnResponse(returnRepository.save(productReturn));
    }
}
