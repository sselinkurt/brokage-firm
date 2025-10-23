package com.application.brokagefirm.infrastructure.persistence.adapter;

import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.model.Order;
import com.application.brokagefirm.infrastructure.persistence.entity.OrderJpaEntity;
import com.application.brokagefirm.infrastructure.persistence.mapper.OrderMapper;
import com.application.brokagefirm.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPersistencePort {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderMapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = orderJpaRepository.save(entity);
        return orderMapper.toDomainModel(savedEntity);
    }
}
