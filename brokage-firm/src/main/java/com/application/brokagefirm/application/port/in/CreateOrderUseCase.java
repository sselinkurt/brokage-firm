package com.application.brokagefirm.application.port.in;

import com.application.brokagefirm.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
}
