package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.usecase.AuthUseCase;
import com.application.brokagefirm.application.port.out.JwtTokenPort;
import com.application.brokagefirm.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenPort jwtTokenPort;

    @Override
    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return jwtTokenPort.generateToken(
                userDetails.getUsername(),
                userDetails.getRole().getSpringSecurityRoleName(),
                userDetails.getCustomerId()
        );
    }
}
