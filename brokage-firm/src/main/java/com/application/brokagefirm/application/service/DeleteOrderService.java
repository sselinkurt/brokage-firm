package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.DeleteOrderUseCase;
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
public class DeleteOrderService implements DeleteOrderUseCase {
    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        Order order = orderPersistencePort.findById(command.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled. Current status: " + order.status());
        }

        restoreAssetUsableSize(order);

        Order cancelledOrder = new Order(
                order.id(),
                order.customerId(),
                order.assetName(),
                order.orderSide(),
                order.size(),
                order.price(),
                OrderStatus.CANCELLED,
                order.createDate()
        );
        orderPersistencePort.save(cancelledOrder);
    }

    private void restoreAssetUsableSize(Order order) {
        if (order.orderSide() == OrderSide.BUY) {
            Asset tryAsset = getAssetOrThrow(order.customerId(), "TRY");
            BigDecimal requiredAmount = order.price().multiply(order.size()).setScale(2, RoundingMode.HALF_UP);
            assetPersistencePort.save(tryAsset.increaseUsableSize(requiredAmount));
        } else if (order.orderSide() == OrderSide.SELL) {
            Asset stockAsset = getAssetOrThrow(order.customerId(), order.assetName());
            assetPersistencePort.save(stockAsset.increaseUsableSize(order.size()));
        }
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new IllegalStateException(assetName + " is not found for customer " + customerId));
    }
}
