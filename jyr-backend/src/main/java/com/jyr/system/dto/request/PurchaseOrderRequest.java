package com.jyr.system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {
    @NotNull
    private Long supplierId;
    private LocalDate expectedDate;
    private String notes;
    @NotEmpty @Valid
    private List<PurchaseOrderDetailRequest> details;
}
