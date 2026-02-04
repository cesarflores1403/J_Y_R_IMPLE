package com.jyr.system.service.impl;

import com.jyr.system.dto.request.InventoryAdjustmentRequest;
import com.jyr.system.dto.response.InventoryMovementResponse;
import com.jyr.system.entity.*;
import com.jyr.system.enums.MovementType;
import com.jyr.system.exception.ResourceNotFoundException;
import com.jyr.system.repository.*;
import com.jyr.system.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final UserRepository userRepository;

    @Transactional
    public void recordMovement(Product product, MovementType type, int quantity,
                                String referenceType, Long referenceId, String notes, User user) {
        int previousStock = product.getStock();
        int newStock;

        switch (type) {
            case ENTRADA, DEVOLUCION -> newStock = previousStock + quantity;
            case SALIDA -> newStock = previousStock - quantity;
            case AJUSTE -> newStock = quantity; // quantity = new absolute stock
            default -> throw new IllegalArgumentException("Tipo de movimiento no válido");
        };

        product.setStock(newStock);
        productRepository.save(product);

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .movementType(type)
                .quantity(type == MovementType.AJUSTE ? Math.abs(newStock - previousStock) : quantity)
                .previousStock(previousStock)
                .newStock(newStock)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .movementDate(LocalDateTime.now())
                .notes(notes)
                .user(user)
                .build();
        movement.setActive(true);

        movementRepository.save(movement);
    }

    @Transactional
    public InventoryMovementResponse adjustStock(InventoryAdjustmentRequest request, String username) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", request.getProductId()));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));

        int previousStock = product.getStock();
        product.setStock(request.getNewStock());
        productRepository.save(product);

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .movementType(MovementType.AJUSTE)
                .quantity(Math.abs(request.getNewStock() - previousStock))
                .previousStock(previousStock)
                .newStock(request.getNewStock())
                .referenceType("AJUSTE_MANUAL")
                .movementDate(LocalDateTime.now())
                .notes(request.getReason())
                .user(user)
                .build();
        movement.setActive(true);

        return EntityMapper.toMovementResponse(movementRepository.save(movement));
    }

    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> getMovementsByProduct(Long productId, Pageable pageable) {
        return movementRepository.findByProductId(productId, pageable)
                .map(EntityMapper::toMovementResponse);
    }

    @Transactional(readOnly = true)
    public Page<InventoryMovementResponse> getMovementsByType(MovementType type, Pageable pageable) {
        return movementRepository.findByMovementType(type, pageable)
                .map(EntityMapper::toMovementResponse);
    }
}
