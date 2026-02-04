package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustmentRequest {
    @NotNull
    private Long productId;
    @NotNull
    private Integer newStock;
    @NotBlank
    private String reason;
}
