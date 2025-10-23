package com.application.brokagefirm.infrastructure.persistence.repository;

import com.application.brokagefirm.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
}
