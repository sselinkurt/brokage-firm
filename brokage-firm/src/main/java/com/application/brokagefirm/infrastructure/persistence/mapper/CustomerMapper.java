package com.application.brokagefirm.infrastructure.persistence.mapper;

import com.application.brokagefirm.domain.model.Customer;
import com.application.brokagefirm.infrastructure.persistence.entity.CustomerJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerJpaEntity toJpaEntity(Customer domain);

    Customer toDomainModel(CustomerJpaEntity entity);
}
