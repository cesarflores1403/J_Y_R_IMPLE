package com.jyr.system.dto.response;

import com.jyr.system.enums.ReturnStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReturnResponse {
    private Long id;
    private String returnNumber;
    private Long documentId;
    private String documentNumber;
    private Long productId;
    private String productName;
    private Long customerId;
    private String customerName;
    private Integer quantity;
    private BigDecimal refundAmount;
    private String reason;
    private ReturnStatus status;
    private LocalDate returnDate;
    private String evidenceUrl;
    private String resolutionNotes;
    private String processedByName;
}
