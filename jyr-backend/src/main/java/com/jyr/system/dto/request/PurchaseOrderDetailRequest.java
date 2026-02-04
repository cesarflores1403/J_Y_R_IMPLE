package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDetailRequest {
    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity;
    @NotNull @DecimalMin("0.01")
    private BigDecimal unitCost;
    private BigDecimal taxRate;
}
