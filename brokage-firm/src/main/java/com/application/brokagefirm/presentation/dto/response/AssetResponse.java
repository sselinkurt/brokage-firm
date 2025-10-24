package com.application.brokagefirm.presentation.dto.response;

import java.math.BigDecimal;

public record AssetResponse(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {
}
