package com.jyr.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Usuario es requerido")
    private String username;
    @NotBlank(message = "Contraseña es requerida")
    private String password;
}
