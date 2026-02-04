package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveLineRequest {
    @NotNull
    private Long detailId;
    @NotNull @Min(1)
    private Integer receivedQuantity;
}
