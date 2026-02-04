package com.jyr.system.dto.request;

import com.jyr.system.enums.ReturnStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnStatusUpdate {
    @NotNull
    private ReturnStatus status;
    private String resolutionNotes;
}
