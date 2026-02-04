package com.jyr.system.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrderDetailResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private Integer receivedQuantity;
    private BigDecimal unitCost;
    private BigDecimal taxRate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
}
