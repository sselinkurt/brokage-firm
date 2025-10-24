package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;
import com.application.brokagefirm.application.port.in.usecase.DeleteOrderUseCase;
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
public class DeleteOrderService implements DeleteOrderUseCase {
    private final OrderPersistencePort orderPersistencePort;
    private final AssetPersistencePort assetPersistencePort;

    @Override
    public void deleteOrder(DeleteOrderCommand command) {
        Order order = orderPersistencePort.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + command.orderId()));

        if (order.status() != OrderStatus.PENDING) {
            throw new InvalidOperationException("Only PENDING orders can be cancelled. Current status: " + order.status());
        }

        restoreAssetUsableSize(order);

        Order cancelledOrder = new Order(
                order.id(),
                order.customerId(),
                order.assetName(),
                order.orderSide(),
                order.size(),
                order.price(),
                OrderStatus.CANCELED,
                order.createDate()
        );
        orderPersistencePort.save(cancelledOrder);

        log.info("Order successfully deleted. Order ID: {}, Customer ID: {}, Asset: {}",
                order.id(), order.customerId(), order.assetName());
    }

    private void restoreAssetUsableSize(Order order) {
        if (order.orderSide() == OrderSide.BUY) {
            Asset tryAsset = getAssetOrThrow(order.customerId(), "TRY");
            BigDecimal requiredAmount = PriceCalculator.calculateTotalAmount(order.price(), order.size());
            assetPersistencePort.save(tryAsset.increaseUsableSize(requiredAmount));
        } else if (order.orderSide() == OrderSide.SELL) {
            Asset stockAsset = getAssetOrThrow(order.customerId(), order.assetName());
            assetPersistencePort.save(stockAsset.increaseUsableSize(order.size()));
        }
    }

    private Asset getAssetOrThrow(Long customerId, String assetName) {
        return assetPersistencePort.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(assetName + " is not found for customer " + customerId));
    }
}
