package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.command.CreateOrderCommand;
import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;
import com.application.brokagefirm.application.port.in.command.ListOrdersQueryCommand;
import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.CreateOrderUseCase;
import com.application.brokagefirm.application.port.in.usecase.DeleteOrderUseCase;
import com.application.brokagefirm.application.port.in.usecase.ListOrdersQuery;
import com.application.brokagefirm.application.port.in.usecase.MatchOrderUseCase;
import com.application.brokagefirm.domain.model.Order;
import com.application.brokagefirm.presentation.dto.request.CreateOrderRequest;
import com.application.brokagefirm.presentation.dto.response.OrderResponse;
import com.application.brokagefirm.presentation.dto.response.ResponseWrapper;
import com.application.brokagefirm.presentation.mapper.ResponseMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ResponseMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #request.customerId == authentication.principal.customerId)")
    public ResponseEntity<ResponseWrapper<OrderResponse>> createOrder(@RequestBody @Valid CreateOrderRequest request) {

        CreateOrderCommand command = new CreateOrderCommand(
                request.customerId(),
                request.assetName(),
                request.side(),
                request.size(),
                request.price()
        );

        Order order = createOrderUseCase.createOrder(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success(null, mapper.toOrderResponse(order)));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == authentication.principal.customerId)")
    public ResponseEntity<ResponseWrapper<Void>> deleteOrder(@PathVariable Long orderId,
                                                             @RequestParam @NotNull Long customerId) {
        DeleteOrderCommand command = new DeleteOrderCommand(orderId);

        deleteOrderUseCase.deleteOrder(command);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.success(null, null));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == authentication.principal.customerId)")
    public ResponseEntity<ResponseWrapper<List<OrderResponse>>> listOrders(@RequestParam @NotNull(message = "Customer ID cannot be null") Long customerId,
                                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        ListOrdersQueryCommand query = new ListOrdersQueryCommand(
                customerId,
                startDate,
                endDate
        );

        List<Order> orders = listOrdersQuery.listOrders(query);
        List<OrderResponse> orderResponse = orders.stream()
                .map(mapper::toOrderResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success(null, orderResponse));
    }

    @PostMapping("/match/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<OrderResponse>> matchOrder(@PathVariable Long orderId) {
        MatchOrderCommand command = new MatchOrderCommand(orderId);

        Order matchedOrder = matchOrderUseCase.matchOrder(command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success(null, mapper.toOrderResponse(matchedOrder)));
    }
}
