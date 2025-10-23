package com.application.brokagefirm.application.port.in;

import com.application.brokagefirm.domain.enums.OrderSide;

import java.math.BigDecimal;

public record CreateOrderCommand(
        Long customerId,
        String assetName,
        OrderSide side,
        BigDecimal size,
        BigDecimal price
) {
}
