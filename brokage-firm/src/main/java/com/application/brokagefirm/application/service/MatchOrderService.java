package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.MatchOrderUseCase;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.PriceCalculator;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.exception.InvalidOperationException;
import com.application.brokagefirm.domain.exception.ResourceNotFoundException;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchOrderService implements MatchOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public Order matchOrder(MatchOrderCommand command) {
        Order order = orderPersistencePort.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new InvalidOperationException("Only PENDING orders can be matched. Current status: " + order.status());
        }

        if ("TRY".equals(order.assetName())) {
            throw new InvalidOperationException("Invalid asset: " + order.assetName());
        }

        updateAssets(order);

        Order matchedOrder = new Order(
                order.id(),
                order.customerId(),
                order.assetName(),
                order.orderSide(),
                order.size(),
                order.price(),
                OrderStatus.MATCHED,
                order.createDate()
        );
        Order savedOrder = orderPersistencePort.save(matchedOrder);

        log.info("Order successfully matched. Order ID: {}, Customer ID: {}, Asset: {}",
                savedOrder.id(), savedOrder.customerId(), savedOrder.assetName());

        return savedOrder;
    }

    private void updateAssets(Order order) {
        if (order.orderSide() == OrderSide.BUY) {
            matchBuyOrder(order);
        } else if (order.orderSide() == OrderSide.SELL) {
            matchSellOrder(order);
        }
    }

    private void matchBuyOrder(Order order) {
        Asset stockAsset = getAssetOrThrow(order.customerId(), order.assetName());
        assetPersistencePort.save(stockAsset.increaseSizeAndUsableSize(order.size()));

        Asset tryAsset = getAssetOrThrow(order.customerId(), "TRY");
        BigDecimal requiredAmount = PriceCalculator.calculateTotalAmount(order.price(), order.size());

        BigDecimal newTrySize = tryAsset.size().subtract(requiredAmount);

        Asset updatedTryAsset = new Asset(
                tryAsset.id(),
                tryAsset.customerId(),
                tryAsset.assetName(),
                newTrySize,
                tryAsset.usableSize(),
                tryAsset.version()
        );

        assetPersistencePort.save(updatedTryAsset);
    }

    private void matchSellOrder(Order order) {
        Asset stockAsset = getAssetOrThrow(order.customerId(), order.assetName());
        BigDecimal newStockSize = stockAsset.size().subtract(order.size());

        Asset updatedStockAsset = new Asset(
                stockAsset.id(),
                stockAsset.customerId(),
                stockAsset.assetName(),
                newStockSize,
                stockAsset.usableSize(),
                stockAsset.version()
        );
        assetPersistencePort.save(updatedStockAsset);

        Asset tryAsset = getAssetOrThrow(order.customerId(), "TRY");
        BigDecimal amountToAdd = PriceCalculator.calculateTotalAmount(order.price(), order.size());

        Asset updatedTryAsset = new Asset(
                tryAsset.id(),
                tryAsset.customerId(),
                tryAsset.assetName(),
                tryAsset.size().add(amountToAdd),
                tryAsset.usableSize().add(amountToAdd),
                tryAsset.version()
        );

        assetPersistencePort.save(updatedTryAsset);
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(assetName + " is not found for customer."));
    }
}
