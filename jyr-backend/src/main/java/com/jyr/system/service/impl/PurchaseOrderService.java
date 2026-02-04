package com.jyr.system.service.impl;

import com.jyr.system.dto.request.*;
import com.jyr.system.dto.response.PurchaseOrderResponse;
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

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository detailRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public PurchaseOrderResponse findById(Long id) {
        return EntityMapper.toPurchaseOrderResponse(purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id)));
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> findAll(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).map(EntityMapper::toPurchaseOrderResponse);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> findByStatus(PurchaseOrderStatus status, Pageable pageable) {
        return purchaseOrderRepository.findByStatus(status, pageable)
                .map(EntityMapper::toPurchaseOrderResponse);
    }

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request, String username) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.getSupplierId()));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String lastNumber = purchaseOrderRepository.findLastOrderNumber();
        String orderNumber = SequenceGenerator.generateNext(lastNumber, "OC-");

        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .supplier(supplier)
                .user(user)
                .orderDate(LocalDate.now())
                .expectedDate(request.getExpectedDate())
                .status(PurchaseOrderStatus.PENDIENTE)
                .notes(request.getNotes())
                .build();
        order.setActive(true);

        for (PurchaseOrderDetailRequest detailReq : request.getDetails()) {
            Product product = productRepository.findById(detailReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detailReq.getProductId()));

            PurchaseOrderDetail detail = PurchaseOrderDetail.builder()
                    .product(product)
                    .quantity(detailReq.getQuantity())
                    .unitCost(detailReq.getUnitCost())
                    .taxRate(detailReq.getTaxRate() != null ?
                            detailReq.getTaxRate() : new BigDecimal("15.00"))
                    .build();
            detail.setActive(true);
            detail.calculateAmounts();
            order.addDetail(detail);
        }

        order.calculateTotals();
        return EntityMapper.toPurchaseOrderResponse(purchaseOrderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse receivePurchase(Long id, ReceivePurchaseRequest request, String username) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (order.getStatus() == PurchaseOrderStatus.CANCELADA) {
            throw new BusinessException("No se puede recibir una orden cancelada");
        }

        boolean allReceived = true;

        for (ReceiveLineRequest line : request.getLines()) {
            PurchaseOrderDetail detail = detailRepository.findById(line.getDetailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Detalle de orden", line.getDetailId()));

            int newReceived = detail.getReceivedQuantity() + line.getReceivedQuantity();
            if (newReceived > detail.getQuantity()) {
                throw new BusinessException("La cantidad recibida excede lo solicitado para: " +
                        detail.getProduct().getName());
            }

            detail.setReceivedQuantity(newReceived);
            detailRepository.save(detail);

            // Update inventory
            inventoryService.recordMovement(
                    detail.getProduct(),
                    MovementType.ENTRADA,
                    line.getReceivedQuantity(),
                    "COMPRA",
                    order.getId(),
                    "Recepción OC - " + order.getOrderNumber(),
                    user
            );

            if (newReceived < detail.getQuantity()) {
                allReceived = false;
            }
        }

        order.setStatus(allReceived ? PurchaseOrderStatus.RECIBIDA : PurchaseOrderStatus.PARCIAL);
        if (allReceived) {
            order.setReceivedDate(LocalDate.now());
        }

        return EntityMapper.toPurchaseOrderResponse(purchaseOrderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse cancel(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));

        if (order.getStatus() == PurchaseOrderStatus.RECIBIDA) {
            throw new BusinessException("No se puede cancelar una orden ya recibida completamente");
        }

        order.setStatus(PurchaseOrderStatus.CANCELADA);
        return EntityMapper.toPurchaseOrderResponse(purchaseOrderRepository.save(order));
    }
}
