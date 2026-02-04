package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank @Size(max = 20)
    private String identityNumber;
    @NotBlank @Size(max = 150)
    private String fullName;
    @Email
    private String email;
    private String phone;
    private String address;
    private String rtn;
    private String notes;
}
