package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.CreateOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.CreateOrderUseCase;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.PriceCalculator;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.exception.ResourceNotFoundException;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public Order createOrder(CreateOrderCommand command) {
        Order order = command.side() == OrderSide.BUY
                ? createBuyOrder(command)
                : createSellOrder(command);

        log.info("Order successfully created. Order ID: {}, Customer ID: {}, Asset: {}",
                order.id(), order.customerId(), order.assetName());

        return order;
    }

    private Order createBuyOrder(CreateOrderCommand command) {
        Asset tryAsset = getAssetOrThrow(command.customerId(), "TRY");
        BigDecimal requiredAmount = PriceCalculator.calculateTotalAmount(command.price(), command.size());
        assetPersistencePort.save(tryAsset.decreaseUsableSize(requiredAmount));

        ensureAssetExists(command.customerId(), command.assetName());

        return saveOrder(command, OrderSide.BUY);
    }

    private Order createSellOrder(CreateOrderCommand command) {
        Asset stockAsset = getAssetOrThrow(command.customerId(), command.assetName());
        assetPersistencePort.save(stockAsset.decreaseUsableSize(command.size()));

        return saveOrder(command, OrderSide.SELL);
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(assetName + " is not found for customer " + customerId));
    }

    private void ensureAssetExists(Long customerId, String assetName) {
        assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> assetPersistencePort.save(
                        new Asset(null, customerId, assetName, BigDecimal.ZERO, BigDecimal.ZERO, 0L)
                ));
    }

    private Order saveOrder(CreateOrderCommand command, OrderSide side) {
        Order newOrder = new Order(
                null,
                command.customerId(),
                command.assetName(),
                side,
                command.size(),
                command.price(),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
        return orderPersistencePort.save(newOrder);
    }
}
