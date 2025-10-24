package com.application.brokagefirm.infrastructure.persistence.mapper;

import com.application.brokagefirm.domain.model.Order;
import com.application.brokagefirm.infrastructure.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderJpaEntity toJpaEntity(Order domain);

    Order toDomainModel(OrderJpaEntity entity);
}
