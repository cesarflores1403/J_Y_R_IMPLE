package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequest {
    @NotNull
    private Long documentId;
    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity;
    @NotBlank
    private String reason;
}
