package com.application.brokagefirm.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenPort {
    String generateToken(String username, String role, Long customerId);
    String extractUsername(String token);
    boolean validateToken(String token, UserDetails userDetails);
}
