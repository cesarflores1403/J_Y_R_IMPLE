package com.jyr.system.controller;

import com.jyr.system.dto.request.CustomerRequest;
import com.jyr.system.dto.response.ApiResponse;
import com.jyr.system.dto.response.CustomerResponse;
import com.jyr.system.service.impl.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(customerService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.findById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> search(
            @RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.search(q, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente creado", customerService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado", customerService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado", null));
    }
}
