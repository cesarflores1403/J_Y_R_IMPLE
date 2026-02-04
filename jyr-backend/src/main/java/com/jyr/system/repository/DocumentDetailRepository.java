package com.jyr.system.repository;

import com.jyr.system.entity.DocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DocumentDetailRepository extends JpaRepository<DocumentDetail, Long> {

    List<DocumentDetail> findByDocumentId(Long documentId);

    @Query("SELECT dd.product.name, SUM(dd.quantity), SUM(dd.total) " +
           "FROM DocumentDetail dd JOIN dd.document d " +
           "WHERE d.documentType = 'FACTURA' AND d.status = 'PAGADA' " +
           "AND d.documentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY dd.product.name ORDER BY SUM(dd.quantity) DESC")
    List<Object[]> findTopProducts(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
}
