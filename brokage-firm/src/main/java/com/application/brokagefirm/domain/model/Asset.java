package com.application.brokagefirm.domain.model;

import com.application.brokagefirm.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;

public record Asset(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize,
        Long version
) {
    public Asset decreaseUsableSize(BigDecimal amount) {
        if (usableSize.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(usableSize);
        }
        return new Asset(id, customerId, assetName, size, usableSize.subtract(amount), version);
    }

    public Asset increaseUsableSize(BigDecimal amount) {
        return new Asset(id, customerId, assetName, size, usableSize.add(amount), version);
    }

    public Asset increaseSizeAndUsableSize(BigDecimal amount) {
        return new Asset(id, customerId, assetName, size.add(amount), usableSize.add(amount), version);
    }

}
