package com.jyr.system.dto.request;

import com.jyr.system.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50)
    private String username;
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    @NotBlank @Size(max = 100)
    private String fullName;
    @Email
    private String email;
    private String phone;
    @NotNull
    private Role role;
}
