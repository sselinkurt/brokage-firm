package com.application.brokagefirm.domain.model;

import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(
        Long id,
        Long customerId,
        String assetName,
        OrderSide orderSide,
        BigDecimal size,
        BigDecimal price,
        OrderStatus status,
        LocalDateTime createDate
) {
}
