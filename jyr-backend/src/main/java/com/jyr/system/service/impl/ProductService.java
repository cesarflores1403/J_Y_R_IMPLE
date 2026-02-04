package com.jyr.system.service.impl;

import com.jyr.system.dto.request.ProductRequest;
import com.jyr.system.dto.response.ProductResponse;
import com.jyr.system.entity.Category;
import com.jyr.system.entity.Product;
import com.jyr.system.exception.*;
import com.jyr.system.repository.CategoryRepository;
import com.jyr.system.repository.ProductRepository;
import com.jyr.system.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findByActiveTrue().stream()
                .map(EntityMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        return EntityMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse findByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + code));
        return EntityMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable)
                .map(EntityMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(EntityMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStock() {
        return productRepository.findLowStockProducts().stream()
                .map(EntityMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe un producto con el código: " + request.getCode());
        }
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException("Ya existe un producto con el código de barras: " + request.getBarcode());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.getCategoryId()));

        Product product = Product.builder()
                .code(request.getCode())
                .barcode(request.getBarcode())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSalePrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .minStock(request.getMinStock() != null ? request.getMinStock() : 5)
                .maxStock(request.getMaxStock())
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : new BigDecimal("15.00"))
                .unit(request.getUnit() != null ? request.getUnit() : "UNIDAD")
                .brand(request.getBrand())
                .model(request.getModel())
                .build();
        product.setActive(true);

        return EntityMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        if (!product.getCode().equals(request.getCode()) &&
            productRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Ya existe un producto con el código: " + request.getCode());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.getCategoryId()));

        product.setCode(request.getCode());
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setMinStock(request.getMinStock());
        product.setMaxStock(request.getMaxStock());
        if (request.getTaxRate() != null) product.setTaxRate(request.getTaxRate());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());

        return EntityMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponse uploadImage(Long id, MultipartFile file) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        product.setImageUrl("/uploads/images/" + filename);
        return EntityMapper.toProductResponse(productRepository.save(product));
    }
}
