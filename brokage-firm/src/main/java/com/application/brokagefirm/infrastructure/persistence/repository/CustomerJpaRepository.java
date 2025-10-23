package com.application.brokagefirm.infrastructure.persistence.repository;

import com.application.brokagefirm.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, Long> {
}
