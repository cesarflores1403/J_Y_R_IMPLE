package com.jyr.system.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank @Size(max = 30)
    private String code;
    private String barcode;
    @NotBlank @Size(max = 150)
    private String name;
    private String description;
    @NotNull
    private Long categoryId;
    @NotNull @DecimalMin("0.00")
    private BigDecimal purchasePrice;
    @NotNull @DecimalMin("0.01")
    private BigDecimal salePrice;
    @Min(0)
    private Integer stock = 0;
    @Min(0)
    private Integer minStock = 5;
    private Integer maxStock;
    private BigDecimal taxRate;
    private String unit;
    private String brand;
    private String model;
}
