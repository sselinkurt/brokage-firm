package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.presentation.dto.CreateOrderRequest;
import com.application.brokagefirm.application.port.in.CreateOrderCommand;
import com.application.brokagefirm.application.port.in.CreateOrderUseCase;
import com.application.brokagefirm.domain.model.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody @Valid CreateOrderRequest request) {

        CreateOrderCommand command = new CreateOrderCommand(
                request.customerId(),
                request.assetName(),
                request.side(),
                request.size(),
                request.price()
        );

        Order order = createOrderUseCase.createOrder(command);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
