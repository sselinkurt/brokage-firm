package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.command.CreateOrderCommand;
import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;
import com.application.brokagefirm.application.port.in.command.ListOrdersQueryCommand;
import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.CreateOrderUseCase;
import com.application.brokagefirm.application.port.in.usecase.DeleteOrderUseCase;
import com.application.brokagefirm.application.port.in.usecase.ListOrdersQuery;
import com.application.brokagefirm.application.port.in.usecase.MatchOrderUseCase;
import com.application.brokagefirm.presentation.dto.CreateOrderRequest;
import com.application.brokagefirm.domain.model.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final ListOrdersQuery listOrdersQuery;
    private final MatchOrderUseCase matchOrderUseCase;

    @PostMapping
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

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        DeleteOrderCommand command = new DeleteOrderCommand(orderId);

        deleteOrderUseCase.deleteOrder(command);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Order>> listOrders(@RequestParam @NotNull(message = "Customer ID cannot be null") Long customerId,
                                                  @RequestParam(required = false) LocalDateTime startDate,
                                                  @RequestParam(required = false) LocalDateTime endDate) {
        ListOrdersQueryCommand query = new ListOrdersQueryCommand(
                customerId,
                startDate,
                endDate
        );

        List<Order> orders = listOrdersQuery.listOrders(query);

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/match/{orderId}")
    public ResponseEntity<Order> matchOrder(@PathVariable Long orderId) {
        MatchOrderCommand command = new MatchOrderCommand(orderId);

        Order matchedOrder = matchOrderUseCase.matchOrder(command);

        return ResponseEntity.ok(matchedOrder);
    }
}
