package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.usecase.CustomerRegisterUseCase;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.CustomerPersistencePort;
import com.application.brokagefirm.domain.enums.Role;
import com.application.brokagefirm.domain.exception.UsernameAlreadyExistsException;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerRegisterService implements CustomerRegisterUseCase {

    @Value("${initial-try-balance}")
    private BigDecimal initialTryBalance;

    private final CustomerPersistencePort customerPersistencePort;
    private final AssetPersistencePort assetPersistencePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerCustomer(String username, String password) {
        if (customerPersistencePort.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        Customer customer = new Customer(null,
                username,
                passwordEncoder.encode(password),
                Role.CUSTOMER);

        Customer savedCustomer = customerPersistencePort.save(customer);

        createInitialTryAsset(savedCustomer.id());
    }

    private void createInitialTryAsset(Long customerId) {
        Asset initialAsset = new Asset(
                null,
                customerId,
                "TRY",
                initialTryBalance,
                initialTryBalance,
                0L
        );
        assetPersistencePort.save(initialAsset);
    }
}
