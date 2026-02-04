package com.jyr.system.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerResponse {
    private Long id;
    private String identityNumber;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String rtn;
    private String notes;
    private Boolean active;
}
