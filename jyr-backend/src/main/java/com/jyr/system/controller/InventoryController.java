package com.jyr.system.controller;

import com.jyr.system.dto.request.InventoryAdjustmentRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.InventoryMovementResponse;
import com.jyr.system.enums.MovementType;
import com.jyr.system.service.impl.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<ApiResponse<Page<InventoryMovementResponse>>> getByProduct(
            @PathVariable Long productId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventoryService.getMovementsByProduct(productId, pageable)));
    }

    @GetMapping("/movements/type/{type}")
    public ResponseEntity<ApiResponse<Page<InventoryMovementResponse>>> getByType(
            @PathVariable MovementType type, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                inventoryService.getMovementsByType(type, pageable)));
    }

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> adjustStock(
            @Valid @RequestBody InventoryAdjustmentRequest request, Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Stock ajustado",
                inventoryService.adjustStock(request, auth.getName())));
    }
}
