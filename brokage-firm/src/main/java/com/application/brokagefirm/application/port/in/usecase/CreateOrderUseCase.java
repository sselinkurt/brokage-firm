package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.CreateOrderCommand;
import com.application.brokagefirm.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
}
