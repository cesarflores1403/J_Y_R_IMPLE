package com.jyr.system.controller;

import com.jyr.system.dto.request.DocumentRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.DocumentResponse;
import com.jyr.system.enums.DocumentStatus;
import com.jyr.system.enums.DocumentType;
import com.jyr.system.service.impl.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.findById(id)));
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<ApiResponse<DocumentResponse>> findByNumber(@PathVariable String number) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.findByNumber(number)));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> findByType(
            @PathVariable DocumentType type, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.findByType(type, pageable)));
    }

    @GetMapping("/type/{type}/status/{status}")
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> findByTypeAndStatus(
            @PathVariable DocumentType type, @PathVariable DocumentStatus status, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.findByTypeAndStatus(type, status, pageable)));
    }

    @GetMapping("/type/{type}/range")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> findByDateRange(
            @PathVariable DocumentType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.ok(documentService.findByDateRange(type, start, end)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DocumentResponse>> create(
            @Valid @RequestBody DocumentRequest request, Authentication auth) {
        String typeName = request.getDocumentType() == DocumentType.FACTURA ? "Factura" : "Cotización";
        DocumentResponse response = documentService.create(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(typeName + " creada: " + response.getDocumentNumber(), response));
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateStatus(
            @PathVariable Long id, @PathVariable DocumentStatus status, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado",
                documentService.updateStatus(id, status, auth.getName())));
    }

    @PostMapping("/{quoteId}/convert")
    public ResponseEntity<ApiResponse<DocumentResponse>> convertToInvoice(
            @PathVariable Long quoteId, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cotización convertida a factura",
                        documentService.convertQuoteToInvoice(quoteId, auth.getName())));
    }
}
