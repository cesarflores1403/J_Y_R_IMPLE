package com.jyr.system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity {

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "rtn", unique = true, length = 20)
    private String rtn;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
