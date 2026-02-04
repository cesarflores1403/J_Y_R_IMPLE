package com.jyr.system.controller;

import com.jyr.system.dto.request.ProductRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.ProductResponse;
import com.jyr.system.service.impl.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(productService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.findById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ProductResponse>> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(productService.findByCode(code)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(productService.search(q, pageable)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(productService.findByCategory(categoryId)));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findLowStock() {
        return ResponseEntity.ok(ApiResponse.ok(productService.findLowStock()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto creado", productService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", productService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<ProductResponse>> uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok("Imagen cargada", productService.uploadImage(id, file)));
    }
}
