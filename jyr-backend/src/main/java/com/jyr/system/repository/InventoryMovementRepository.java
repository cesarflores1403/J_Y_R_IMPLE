package com.jyr.system.repository;

import com.jyr.system.entity.InventoryMovement;
import com.jyr.system.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Page<InventoryMovement> findByProductId(Long productId, Pageable pageable);
    Page<InventoryMovement> findByMovementType(MovementType type, Pageable pageable);
    List<InventoryMovement> findByProductIdAndMovementDateBetween(
            Long productId, LocalDateTime start, LocalDateTime end);
}
