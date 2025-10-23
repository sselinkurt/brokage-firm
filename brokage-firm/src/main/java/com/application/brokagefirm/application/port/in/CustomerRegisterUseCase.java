package com.application.brokagefirm.application.port.in;

import com.application.brokagefirm.domain.model.Customer;

public interface CustomerRegisterUseCase {
    Customer registerCustomer(CustomerRegisterCommand command);
}
