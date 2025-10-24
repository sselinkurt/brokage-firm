package com.application.brokagefirm.application.service;

import com.application.brokagefirm.domain.model.Asset;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AssetTestFactory {

    private static final Long CUSTOMER_ID = 101L;

    public static Asset createTryAsset(BigDecimal size, BigDecimal usableSize, Long version) {
        return new Asset(
                1L,
                CUSTOMER_ID,
                "TRY",
                size.setScale(2, RoundingMode.HALF_UP),
                usableSize.setScale(2, RoundingMode.HALF_UP),
                version
        );
    }

    public static Asset createStockAsset(String assetName, BigDecimal size, BigDecimal usableSize, Long version) {
        return new Asset(
                2L,
                CUSTOMER_ID,
                assetName,
                size.setScale(2, RoundingMode.HALF_UP),
                usableSize.setScale(2, RoundingMode.HALF_UP),
                version
        );
    }

    public static Asset createNewStockAsset(String assetName) {
        return new Asset(
                null,
                CUSTOMER_ID,
                assetName,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0L
        );
    }
}
