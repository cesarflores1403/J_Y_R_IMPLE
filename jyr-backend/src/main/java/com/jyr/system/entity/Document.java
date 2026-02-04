package com.jyr.system.entity;

import com.jyr.system.enums.DocumentStatus;
import com.jyr.system.enums.DocumentType;
import com.jyr.system.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_doc_number", columnList = "document_number"),
    @Index(name = "idx_doc_type", columnList = "document_type"),
    @Index(name = "idx_doc_date", columnList = "document_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends BaseEntity {

    @Column(name = "document_number", nullable = false, unique = true, length = 30)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PENDIENTE;

    @Column(name = "document_date", nullable = false)
    private LocalDate documentDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DocumentDetail> details = new ArrayList<>();

    // Helper methods
    public void addDetail(DocumentDetail detail) {
        details.add(detail);
        detail.setDocument(this);
    }

    public void removeDetail(DocumentDetail detail) {
        details.remove(detail);
        detail.setDocument(null);
    }

    public void calculateTotals() {
        this.subtotal = details.stream()
                .map(DocumentDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.taxAmount = details.stream()
                .map(DocumentDetail::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = this.subtotal.add(this.taxAmount).subtract(this.discountAmount);
    }
}
