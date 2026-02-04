package com.jyr.system.repository;

import com.jyr.system.entity.PurchaseOrder;
import com.jyr.system.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);
    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status = :status")
    Long countByStatus(@Param("status") PurchaseOrderStatus status);

    @Query("SELECT MAX(po.orderNumber) FROM PurchaseOrder po")
    String findLastOrderNumber();
}
