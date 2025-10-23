package com.application.brokagefirm.application.port.out;

import com.application.brokagefirm.domain.model.Order;

public interface OrderPersistencePort {
    Order save(Order order);
}
