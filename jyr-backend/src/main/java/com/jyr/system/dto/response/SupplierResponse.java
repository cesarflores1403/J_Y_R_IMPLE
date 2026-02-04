package com.jyr.system.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierResponse {
    private Long id;
    private String companyName;
    private String rtn;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private String notes;
    private Boolean active;
}
