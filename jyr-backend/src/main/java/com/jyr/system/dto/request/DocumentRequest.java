package com.jyr.system.dto.request;

import com.jyr.system.enums.DocumentType;
import com.jyr.system.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    @NotNull
    private DocumentType documentType;
    @NotNull
    private Long customerId;
    private LocalDate dueDate;
    private BigDecimal discountAmount;
    private PaymentMethod paymentMethod;
    private String notes;
    @NotEmpty @Valid
    private List<DocumentDetailRequest> details;
}
