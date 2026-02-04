package com.jyr.system.dto.response;

import com.jyr.system.enums.MovementType;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryMovementResponse {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private MovementType movementType;
    private Integer quantity;
    private Integer previousStock;
    private Integer newStock;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime movementDate;
    private String notes;
    private String userName;
}
