package com.jyr.system.repository;

import com.jyr.system.entity.Document;
import com.jyr.system.enums.DocumentStatus;
import com.jyr.system.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByDocumentNumber(String documentNumber);

    Page<Document> findByDocumentType(DocumentType type, Pageable pageable);
    Page<Document> findByDocumentTypeAndStatus(DocumentType type, DocumentStatus status, Pageable pageable);
    Page<Document> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.documentType = :type AND " +
           "d.documentDate BETWEEN :startDate AND :endDate")
    List<Document> findByTypeAndDateRange(
            @Param("type") DocumentType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(d.total), 0) FROM Document d WHERE d.documentType = 'FACTURA' " +
           "AND d.status = 'PAGADA' AND d.documentDate = :date")
    BigDecimal sumSalesByDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(d.total), 0) FROM Document d WHERE d.documentType = 'FACTURA' " +
           "AND d.status = 'PAGADA' AND d.documentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumSalesByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.documentType = 'FACTURA' AND d.documentDate = :date")
    Long countInvoicesByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.documentType = 'FACTURA' " +
           "AND d.documentDate BETWEEN :startDate AND :endDate")
    Long countInvoicesByDateRange(@Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT MAX(d.documentNumber) FROM Document d WHERE d.documentType = :type")
    String findLastDocumentNumber(@Param("type") DocumentType type);
}
