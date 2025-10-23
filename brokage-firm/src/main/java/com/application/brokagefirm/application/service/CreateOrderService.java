package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.CreateOrderCommand;
import com.application.brokagefirm.application.port.in.CreateOrderUseCase;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public Order createOrder(CreateOrderCommand command) {
        return command.side() == OrderSide.BUY
                ? createBuyOrder(command)
                : createSellOrder(command);
    }

    private Order createBuyOrder(CreateOrderCommand command) {
        Asset tryAsset = getAssetOrThrow(command.customerId(), "TRY");
        BigDecimal requiredAmount = command.price().multiply(command.size()).setScale(2, RoundingMode.HALF_UP);
        assetPersistencePort.save(tryAsset.decreaseUsableSize(requiredAmount));

        ensureAssetExists(command.customerId(), command.assetName());

        return saveOrder(command, OrderSide.BUY);
    }

    private Order createSellOrder(CreateOrderCommand command) {
        Asset stockAsset = getAssetOrThrow(command.customerId(), command.assetName());
        assetPersistencePort.save(stockAsset.decreaseUsableSize(command.size()));

        Asset tryAsset = getAssetOrThrow(command.customerId(), "TRY");
        BigDecimal amountToAdd = command.price().multiply(command.size()).setScale(2, RoundingMode.HALF_UP);
        assetPersistencePort.save(new Asset(
                tryAsset.id(),
                tryAsset.customerId(),
                tryAsset.assetName(),
                tryAsset.size().add(amountToAdd),
                tryAsset.usableSize()
        ));

        return saveOrder(command, OrderSide.SELL);
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new IllegalStateException(assetName + " varlığı bulunamadı."));
    }

    private void ensureAssetExists(Long customerId, String assetName) {
        assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> assetPersistencePort.save(
                        new Asset(null, customerId, assetName, BigDecimal.ZERO, BigDecimal.ZERO)
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
