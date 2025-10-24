package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.usecase.AuthUseCase;
import com.application.brokagefirm.application.port.in.usecase.CustomerRegisterUseCase;
import com.application.brokagefirm.presentation.dto.request.CustomerRegisterRequest;
import com.application.brokagefirm.presentation.dto.request.LoginRequest;
import com.application.brokagefirm.presentation.dto.response.AuthResponse;
import com.application.brokagefirm.presentation.dto.response.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final CustomerRegisterUseCase customerRegisterUseCase;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        String token = authUseCase.login(loginRequest.username(), loginRequest.password());

        AuthResponse response = new AuthResponse(token);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseWrapper.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<Void>> createCustomer(@RequestBody @Valid CustomerRegisterRequest request) {
        customerRegisterUseCase.registerCustomer(request.username(), request.password());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("User registered successfully", null));
    }
}
