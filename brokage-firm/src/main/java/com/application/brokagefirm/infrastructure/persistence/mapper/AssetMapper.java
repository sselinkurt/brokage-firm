package com.application.brokagefirm.infrastructure.persistence.mapper;

import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.infrastructure.persistence.entity.AssetJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetJpaEntity toJpaEntity(Asset domain);

    Asset toDomainModel(AssetJpaEntity entity);
}
