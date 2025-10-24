package com.application.brokagefirm.infrastructure.security;

import com.application.brokagefirm.application.port.out.CustomerPersistencePort;
import com.application.brokagefirm.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerPersistencePort customerPersistencePort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerPersistencePort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new CustomUserDetails(customer);
    }
}
