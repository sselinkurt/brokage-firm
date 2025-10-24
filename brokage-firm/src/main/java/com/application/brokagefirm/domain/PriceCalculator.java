package com.application.brokagefirm.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PriceCalculator {

    private PriceCalculator() {
    }

    public static BigDecimal calculateTotalAmount(BigDecimal price, BigDecimal size) {
        return price.multiply(size).setScale(2, RoundingMode.HALF_UP);
    }
}
