package com.jyr.system.repository;

import com.jyr.system.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    Optional<Product> findByBarcode(String barcode);
    boolean existsByCode(String code);
    boolean existsByBarcode(String barcode);

    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock <= p.minStock")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           "LOWER(p.code) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%',:search,'%')))")
    Page<Product> searchProducts(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    Long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.stock <= p.minStock")
    Long countLowStockProducts();
}
