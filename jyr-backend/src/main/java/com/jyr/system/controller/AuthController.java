package com.jyr.system.controller;

import com.jyr.system.dto.request.LoginRequest;
import com.jyr.system.dto.request.RegisterRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.AuthResponse;
import com.jyr.system.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Registro exitoso", authService.register(request)));
    }
}
