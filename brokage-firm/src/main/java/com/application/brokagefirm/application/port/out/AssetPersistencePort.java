package com.application.brokagefirm.application.port.out;

import com.application.brokagefirm.domain.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetPersistencePort {
    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
    Asset save(Asset asset);
    List<Asset> findByCustomerId(Long customerId);
}
