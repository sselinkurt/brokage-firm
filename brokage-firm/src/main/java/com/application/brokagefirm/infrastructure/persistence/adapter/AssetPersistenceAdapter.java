package com.application.brokagefirm.infrastructure.persistence.adapter;

import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.infrastructure.persistence.entity.AssetJpaEntity;
import com.application.brokagefirm.infrastructure.persistence.mapper.AssetMapper;
import com.application.brokagefirm.infrastructure.persistence.repository.AssetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetPersistenceAdapter implements AssetPersistencePort {
    private final AssetJpaRepository assetJpaRepository;
    private final AssetMapper assetMapper;

    @Override
    public Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName) {
        return assetJpaRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(assetMapper::toDomainModel);
    }

    @Override
    public Asset save(Asset asset) {
        AssetJpaEntity entity = assetMapper.toJpaEntity(asset);
        AssetJpaEntity savedEntity = assetJpaRepository.save(entity);
        return assetMapper.toDomainModel(savedEntity);
    }
}
