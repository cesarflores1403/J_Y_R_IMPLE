package com.jyr.system.service.impl;

import com.jyr.system.dto.request.SupplierRequest;
import com.jyr.system.dto.response.SupplierResponse;
import com.jyr.system.entity.Supplier;
import com.jyr.system.exception.*;
import com.jyr.system.repository.SupplierRepository;
import com.jyr.system.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll() {
        return supplierRepository.findByActiveTrue().stream()
                .map(EntityMapper::toSupplierResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return EntityMapper.toSupplierResponse(supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id)));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        if (request.getRtn() != null && supplierRepository.existsByRtn(request.getRtn())) {
            throw new DuplicateResourceException("Ya existe proveedor con RTN: " + request.getRtn());
        }
        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .rtn(request.getRtn())
                .contactName(request.getContactName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();
        supplier.setActive(true);
        return EntityMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        supplier.setCompanyName(request.getCompanyName());
        supplier.setRtn(request.getRtn());
        supplier.setContactName(request.getContactName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setNotes(request.getNotes());
        return EntityMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
}
