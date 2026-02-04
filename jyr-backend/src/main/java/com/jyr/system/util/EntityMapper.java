package com.jyr.system.util;

import com.jyr.system.dto.response.*;
import com.jyr.system.entity.*;

import java.util.stream.Collectors;

public class EntityMapper {

    private EntityMapper() {}

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }

    public static ProductResponse toProductResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .barcode(p.getBarcode())
                .name(p.getName())
                .description(p.getDescription())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .purchasePrice(p.getPurchasePrice())
                .salePrice(p.getSalePrice())
                .stock(p.getStock())
                .minStock(p.getMinStock())
                .maxStock(p.getMaxStock())
                .imageUrl(p.getImageUrl())
                .taxRate(p.getTaxRate())
                .unit(p.getUnit())
                .brand(p.getBrand())
                .model(p.getModel())
                .lowStock(p.isLowStock())
                .active(p.getActive())
                .build();
    }

    public static CategoryResponse toCategoryResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .productCount(c.getProducts() != null ? c.getProducts().size() : 0)
                .active(c.getActive())
                .build();
    }

    public static CustomerResponse toCustomerResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .identityNumber(c.getIdentityNumber())
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .rtn(c.getRtn())
                .notes(c.getNotes())
                .active(c.getActive())
                .build();
    }

    public static SupplierResponse toSupplierResponse(Supplier s) {
        return SupplierResponse.builder()
                .id(s.getId())
                .companyName(s.getCompanyName())
                .rtn(s.getRtn())
                .contactName(s.getContactName())
                .email(s.getEmail())
                .phone(s.getPhone())
                .address(s.getAddress())
                .notes(s.getNotes())
                .active(s.getActive())
                .build();
    }

    public static DocumentResponse toDocumentResponse(Document d) {
        return DocumentResponse.builder()
                .id(d.getId())
                .documentNumber(d.getDocumentNumber())
                .documentType(d.getDocumentType())
                .status(d.getStatus())
                .documentDate(d.getDocumentDate())
                .dueDate(d.getDueDate())
                .customerId(d.getCustomer().getId())
                .customerName(d.getCustomer().getFullName())
                .customerIdentity(d.getCustomer().getIdentityNumber())
                .userId(d.getUser().getId())
                .userName(d.getUser().getFullName())
                .subtotal(d.getSubtotal())
                .taxAmount(d.getTaxAmount())
                .discountAmount(d.getDiscountAmount())
                .total(d.getTotal())
                .paymentMethod(d.getPaymentMethod())
                .notes(d.getNotes())
                .details(d.getDetails().stream()
                        .map(EntityMapper::toDocumentDetailResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static DocumentDetailResponse toDocumentDetailResponse(DocumentDetail dd) {
        return DocumentDetailResponse.builder()
                .id(dd.getId())
                .productId(dd.getProduct().getId())
                .productCode(dd.getProduct().getCode())
                .productName(dd.getProduct().getName())
                .quantity(dd.getQuantity())
                .unitPrice(dd.getUnitPrice())
                .discountPercent(dd.getDiscountPercent())
                .taxRate(dd.getTaxRate())
                .subtotal(dd.getSubtotal())
                .taxAmount(dd.getTaxAmount())
                .total(dd.getTotal())
                .build();
    }

    public static ReturnResponse toReturnResponse(ProductReturn r) {
        return ReturnResponse.builder()
                .id(r.getId())
                .returnNumber(r.getReturnNumber())
                .documentId(r.getDocument().getId())
                .documentNumber(r.getDocument().getDocumentNumber())
                .productId(r.getProduct().getId())
                .productName(r.getProduct().getName())
                .customerId(r.getCustomer().getId())
                .customerName(r.getCustomer().getFullName())
                .quantity(r.getQuantity())
                .refundAmount(r.getRefundAmount())
                .reason(r.getReason())
                .status(r.getStatus())
                .returnDate(r.getReturnDate())
                .evidenceUrl(r.getEvidenceUrl())
                .resolutionNotes(r.getResolutionNotes())
                .processedByName(r.getProcessedBy() != null ?
                        r.getProcessedBy().getFullName() : null)
                .build();
    }

    public static PurchaseOrderResponse toPurchaseOrderResponse(PurchaseOrder po) {
        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .orderNumber(po.getOrderNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getCompanyName())
                .userId(po.getUser().getId())
                .userName(po.getUser().getFullName())
                .orderDate(po.getOrderDate())
                .expectedDate(po.getExpectedDate())
                .receivedDate(po.getReceivedDate())
                .status(po.getStatus())
                .subtotal(po.getSubtotal())
                .taxAmount(po.getTaxAmount())
                .total(po.getTotal())
                .notes(po.getNotes())
                .details(po.getDetails().stream()
                        .map(EntityMapper::toPurchaseOrderDetailResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public static PurchaseOrderDetailResponse toPurchaseOrderDetailResponse(PurchaseOrderDetail pod) {
        return PurchaseOrderDetailResponse.builder()
                .id(pod.getId())
                .productId(pod.getProduct().getId())
                .productCode(pod.getProduct().getCode())
                .productName(pod.getProduct().getName())
                .quantity(pod.getQuantity())
                .receivedQuantity(pod.getReceivedQuantity())
                .unitCost(pod.getUnitCost())
                .taxRate(pod.getTaxRate())
                .subtotal(pod.getSubtotal())
                .taxAmount(pod.getTaxAmount())
                .total(pod.getTotal())
                .build();
    }

    public static InventoryMovementResponse toMovementResponse(InventoryMovement m) {
        return InventoryMovementResponse.builder()
                .id(m.getId())
                .productId(m.getProduct().getId())
                .productCode(m.getProduct().getCode())
                .productName(m.getProduct().getName())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .previousStock(m.getPreviousStock())
                .newStock(m.getNewStock())
                .referenceType(m.getReferenceType())
                .referenceId(m.getReferenceId())
                .movementDate(m.getMovementDate())
                .notes(m.getNotes())
                .userName(m.getUser().getFullName())
                .build();
    }
}
