package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    @NotBlank @Size(max = 150)
    private String companyName;
    private String rtn;
    private String contactName;
    @Email
    private String email;
    private String phone;
    private String address;
    private String notes;
}
