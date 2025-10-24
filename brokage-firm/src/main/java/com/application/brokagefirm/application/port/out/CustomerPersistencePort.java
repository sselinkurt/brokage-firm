package com.application.brokagefirm.application.port.out;

import com.application.brokagefirm.domain.model.Customer;

import java.util.Optional;

public interface CustomerPersistencePort {
    Customer save(Customer customer);
    Optional<Customer> findByUsername(String username);
    boolean existsByUsername(String username);
}
