package com.jyr.system.dto.response;

import com.jyr.system.enums.PurchaseOrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrderResponse {
    private Long id;
    private String orderNumber;
    private Long supplierId;
    private String supplierName;
    private Long userId;
    private String userName;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private LocalDate receivedDate;
    private PurchaseOrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String notes;
    private List<PurchaseOrderDetailResponse> details;
}
