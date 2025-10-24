package com.application.brokagefirm.application.port.out;

import com.application.brokagefirm.domain.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderPersistencePort {
    Order save(Order order);

    Optional<Order> findById(Long orderId);

    List<Order> findByCustomerIdAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
