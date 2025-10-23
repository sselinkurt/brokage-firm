package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.command.CustomerRegisterCommand;
import com.application.brokagefirm.application.port.in.usecase.CustomerRegisterUseCase;
import com.application.brokagefirm.domain.model.Customer;
import com.application.brokagefirm.presentation.dto.CustomerRegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRegisterUseCase customerRegisterUseCase;

    @PostMapping("/register")
    public ResponseEntity<Customer> createCustomer(@RequestBody @Valid CustomerRegisterRequest request) {

        CustomerRegisterCommand command = new CustomerRegisterCommand(
                request.username(),
                request.password(),
                null
        );

        Customer customer = customerRegisterUseCase.registerCustomer(command);

        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }
}
