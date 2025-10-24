package com.application.brokagefirm.infrastructure.persistence;

import com.application.brokagefirm.domain.enums.Role;
import com.application.brokagefirm.domain.model.Customer;
import com.application.brokagefirm.infrastructure.persistence.adapter.JwtServiceAdapter;
import com.application.brokagefirm.infrastructure.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceAdapterTest {

    private JwtServiceAdapter jwtServiceAdapter;

    private final String testSecretKey = "eYEmZrURmGTOUXh4f3XbgUIpmP6lZ2puJuexBcEckDg=";

    @BeforeEach
    void setUp() {
        jwtServiceAdapter = new JwtServiceAdapter();
        ReflectionTestUtils.setField(jwtServiceAdapter, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtServiceAdapter, "jwtExpiration", 3600000L);
    }

    @Test
    void shouldGenerateValidToken() {
        String username = "testuser";
        String role = "ROLE_CUSTOMER";
        Long customerId = 123L;

        String token = jwtServiceAdapter.generateToken(username, role, customerId);

        assertThat(token).isNotNull().isNotEmpty();

        String extractedUsername = jwtServiceAdapter.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldValidateCorrectToken() {
        Customer customer = new Customer(1L, "testuser", "password", Role.CUSTOMER);
        UserDetails userDetails = new CustomUserDetails(customer);
        String token = jwtServiceAdapter.generateToken(userDetails.getUsername(), "ROLE_CUSTOMER", 1L);

        boolean isValid = jwtServiceAdapter.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void shouldNotValidateTokenWithWrongUsername() {
        Customer correctCustomer = new Customer(1L, "correctUser", "password", Role.CUSTOMER);
        Customer wrongCustomer = new Customer(2L, "wrongUser", "password", Role.CUSTOMER);
        UserDetails wrongUserDetails = new CustomUserDetails(wrongCustomer);

        String tokenForCorrectUser = jwtServiceAdapter.generateToken(correctCustomer.username(), "ROLE_CUSTOMER", 1L);

        boolean isValid = jwtServiceAdapter.validateToken(tokenForCorrectUser, wrongUserDetails);

        assertFalse(isValid);
    }

    @Test
    void shouldThrowExpiredJwtExceptionForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtServiceAdapter, "jwtExpiration", 1L);
        String expiredToken = jwtServiceAdapter.generateToken("testuser", "ROLE_USER", 1L);

        Thread.sleep(50);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtServiceAdapter.extractUsername(expiredToken);
        });
    }
}

