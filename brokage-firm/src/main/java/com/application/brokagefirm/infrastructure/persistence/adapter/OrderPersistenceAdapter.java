package com.application.brokagefirm.infrastructure.persistence.adapter;

import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.model.Order;
import com.application.brokagefirm.infrastructure.persistence.entity.OrderJpaEntity;
import com.application.brokagefirm.infrastructure.persistence.mapper.OrderMapper;
import com.application.brokagefirm.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId)
                .map(orderMapper::toDomainModel);
    }

    @Override
    public List<Order> findByCustomerIdAndDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderJpaEntity> entities;

        if (startDate != null && endDate != null) {
            entities = orderJpaRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
        } else {
            entities = orderJpaRepository.findByCustomerId(customerId);
        }

        return entities.stream()
                .map(orderMapper::toDomainModel)
                .collect(Collectors.toList());
    }
}
