package com.jyr.system.controller;

import com.jyr.system.dto.request.PurchaseOrderRequest;
import com.jyr.system.dto.request.ReceivePurchaseRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.PurchaseOrderResponse;
import com.jyr.system.enums.PurchaseOrderStatus;
import com.jyr.system.service.impl.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.findAll(pageable)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> findByStatus(
            @PathVariable PurchaseOrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(purchaseOrderService.findByStatus(status, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> create(
            @Valid @RequestBody PurchaseOrderRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Orden de compra creada",
                        purchaseOrderService.create(request, auth.getName())));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receive(
            @PathVariable Long id,
            @Valid @RequestBody ReceivePurchaseRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Mercancía recibida",
                purchaseOrderService.receivePurchase(id, request, auth.getName())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Orden cancelada", purchaseOrderService.cancel(id)));
    }
}
