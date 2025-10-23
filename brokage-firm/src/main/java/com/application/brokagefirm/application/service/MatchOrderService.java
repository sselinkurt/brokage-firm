package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.MatchOrderUseCase;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchOrderService implements MatchOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public Order matchOrder(MatchOrderCommand command) {
        Order order = orderPersistencePort.findById(command.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be matched. Current status: " + order.status());
        }

        if ("TRY".equals(order.assetName())) {
            throw new IllegalStateException("Invalid asset: " + order.assetName());
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
        return orderPersistencePort.save(matchedOrder);
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
        BigDecimal requiredAmount = order.price().multiply(order.size()).setScale(2, RoundingMode.HALF_UP);

        BigDecimal newTrySize = tryAsset.size().subtract(requiredAmount);

        Asset updatedTryAsset = new Asset(
                tryAsset.id(),
                tryAsset.customerId(),
                tryAsset.assetName(),
                newTrySize,
                tryAsset.usableSize()
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
                stockAsset.usableSize()
        );
        assetPersistencePort.save(updatedStockAsset);

        Asset tryAsset = getAssetOrThrow(order.customerId(), "TRY");
        BigDecimal amountToAdd = order.price().multiply(order.size()).setScale(2, RoundingMode.HALF_UP);

        Asset updatedTryAsset = new Asset(
                tryAsset.id(),
                tryAsset.customerId(),
                tryAsset.assetName(),
                tryAsset.size(),
                tryAsset.usableSize().add(amountToAdd)
        );

        assetPersistencePort.save(updatedTryAsset);
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new IllegalStateException(assetName + " is not found for customer."));
    }
}
