package com.application.brokagefirm.infrastructure.persistence.repository;

import com.application.brokagefirm.infrastructure.persistence.entity.AssetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetJpaRepository extends JpaRepository<AssetJpaEntity, Long> {
    Optional<AssetJpaEntity> findByCustomerIdAndAssetName(Long customerId, String assetName);
}
