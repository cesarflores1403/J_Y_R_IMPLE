package com.jyr.system.repository;

import com.jyr.system.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdentityNumber(String identityNumber);
    boolean existsByIdentityNumber(String identityNumber);
    List<Customer> findByActiveTrue();

    @Query("SELECT c FROM Customer c WHERE c.active = true AND " +
           "(LOWER(c.fullName) LIKE LOWER(CONCAT('%',:search,'%')) OR " +
           "c.identityNumber LIKE CONCAT('%',:search,'%'))")
    Page<Customer> searchCustomers(@Param("search") String search, Pageable pageable);
}
