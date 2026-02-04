package com.jyr.system.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceivePurchaseRequest {
    @NotEmpty @Valid
    private List<ReceiveLineRequest> lines;
}
