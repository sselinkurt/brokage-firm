package com.application.brokagefirm.application.port.out;

import com.application.brokagefirm.domain.model.Customer;

public interface CustomerPersistencePort {
    Customer save(Customer customer);
}
