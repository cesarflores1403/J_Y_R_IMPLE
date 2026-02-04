package com.jyr.system.dto.response;

import com.jyr.system.enums.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DocumentResponse {
    private Long id;
    private String documentNumber;
    private DocumentType documentType;
    private DocumentStatus status;
    private LocalDate documentDate;
    private LocalDate dueDate;
    private Long customerId;
    private String customerName;
    private String customerIdentity;
    private Long userId;
    private String userName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private String notes;
    private List<DocumentDetailResponse> details;
}
