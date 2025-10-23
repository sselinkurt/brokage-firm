package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.CustomerRegisterCommand;
import com.application.brokagefirm.domain.model.Customer;

public interface CustomerRegisterUseCase {
    Customer registerCustomer(CustomerRegisterCommand command);
}
