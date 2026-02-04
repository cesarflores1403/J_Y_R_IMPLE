package com.jyr.system.dto.response;

import com.jyr.system.enums.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ==================== PRODUCT ====================
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Long id;
    private String code;
    private String barcode;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Integer stock;
    private Integer minStock;
    private Integer maxStock;
    private String imageUrl;
    private BigDecimal taxRate;
    private String unit;
    private String brand;
    private String model;
    private Boolean lowStock;
    private Boolean active;
}
