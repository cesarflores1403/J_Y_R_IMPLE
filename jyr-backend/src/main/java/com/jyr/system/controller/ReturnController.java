package com.jyr.system.controller;

import com.jyr.system.dto.request.ReturnRequest;
import com.jyr.system.dto.request.ReturnStatusUpdate;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.ReturnResponse;
import com.jyr.system.enums.ReturnStatus;
import com.jyr.system.service.impl.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReturnResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.findAll(pageable)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<ReturnResponse>>> findByStatus(
            @PathVariable ReturnStatus status, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(returnService.findByStatus(status, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> create(
            @Valid @RequestBody ReturnRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Devolución registrada", returnService.create(request, auth.getName())));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReturnResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody ReturnStatusUpdate update, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado",
                returnService.updateStatus(id, update, auth.getName())));
    }

    @PostMapping("/{id}/evidence")
    public ResponseEntity<ApiResponse<ReturnResponse>> uploadEvidence(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok("Evidencia cargada",
                returnService.uploadEvidence(id, file)));
    }
}
