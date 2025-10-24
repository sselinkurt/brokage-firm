package com.application.brokagefirm.infrastructure.persistence.adapter;

import com.application.brokagefirm.application.port.out.CustomerPersistencePort;
import com.application.brokagefirm.domain.model.Customer;
import com.application.brokagefirm.infrastructure.persistence.entity.CustomerJpaEntity;
import com.application.brokagefirm.infrastructure.persistence.mapper.CustomerMapper;
import com.application.brokagefirm.infrastructure.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerPersistencePort {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = customerMapper.toJpaEntity(customer);
        CustomerJpaEntity savedEntity = customerJpaRepository.save(entity);
        return customerMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        Optional<CustomerJpaEntity> entityOptional = customerJpaRepository.findByUsername(username);
        return entityOptional.map(customerMapper::toDomainModel);
    }

    @Override
    public boolean existsByUsername(String username) {
        return customerJpaRepository.existsByUsername(username);
    }

}
