package com.application.brokagefirm.domain.model;

import java.math.BigDecimal;

public record Asset(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {
    public Asset decreaseUsableSize(BigDecimal amount) {
        if (usableSize.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient available balance: " + usableSize);
        }
        return new Asset(id, customerId, assetName, size, usableSize.subtract(amount));
    }

    public Asset increaseUsableSize(BigDecimal amount) {
        return new Asset(id, customerId, assetName, size, usableSize.add(amount));
    }

    public Asset increaseSizeAndUsableSize(BigDecimal amount) {
        return new Asset(id, customerId, assetName, size.add(amount), usableSize.add(amount));
    }

}
