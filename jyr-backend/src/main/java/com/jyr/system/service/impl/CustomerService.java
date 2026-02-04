package com.jyr.system.service.impl;

import com.jyr.system.dto.request.CustomerRequest;
import com.jyr.system.dto.response.CustomerResponse;
import com.jyr.system.entity.Customer;
import com.jyr.system.exception.*;
import com.jyr.system.repository.CustomerRepository;
import com.jyr.system.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findByActiveTrue().stream()
                .map(EntityMapper::toCustomerResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return EntityMapper.toCustomerResponse(customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id)));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> search(String query, Pageable pageable) {
        return customerRepository.searchCustomers(query, pageable).map(EntityMapper::toCustomerResponse);
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new DuplicateResourceException("Ya existe cliente con identidad: " + request.getIdentityNumber());
        }
        Customer customer = Customer.builder()
                .identityNumber(request.getIdentityNumber())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .rtn(request.getRtn())
                .notes(request.getNotes())
                .build();
        customer.setActive(true);
        return EntityMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setRtn(request.getRtn());
        customer.setNotes(request.getNotes());
        return EntityMapper.toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        customer.setActive(false);
        customerRepository.save(customer);
    }
}
