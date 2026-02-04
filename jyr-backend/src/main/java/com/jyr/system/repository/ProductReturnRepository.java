package com.jyr.system.repository;

import com.jyr.system.entity.ProductReturn;
import com.jyr.system.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReturnRepository extends JpaRepository<ProductReturn, Long> {
    Optional<ProductReturn> findByReturnNumber(String returnNumber);
    Page<ProductReturn> findByStatus(ReturnStatus status, Pageable pageable);
    Page<ProductReturn> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM ProductReturn r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReturnStatus status);

    @Query("SELECT MAX(r.returnNumber) FROM ProductReturn r")
    String findLastReturnNumber();
}
