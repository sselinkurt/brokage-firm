package com.application.brokagefirm.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long customerId,
        String assetName,
        String orderSide,
        BigDecimal size,
        BigDecimal price,
        String status,
        LocalDateTime createDate
) {
}
