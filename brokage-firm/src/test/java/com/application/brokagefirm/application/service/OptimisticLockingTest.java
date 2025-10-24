package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptimisticLockingTest {

    @Mock
    private OrderPersistencePort orderPersistencePort;

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @InjectMocks
    private MatchOrderService matchOrderService;

    private static final Long CUSTOMER_ID = 101L;
    private static final Long ORDER_ID = 700L;
    private static final String ASSET_NAME = "LOCK_TEST";
    private static final BigDecimal ORDER_SIZE = BigDecimal.valueOf(1.00);
    private static final BigDecimal ORDER_PRICE = BigDecimal.valueOf(10.00);

    @Test
    @DisplayName("Should Throw OptimisticLockingFailureException on Concurrent Asset Update")
    void matchOrder_shouldThrowOptimisticLockingFailureException() {
        Asset initialStockAsset = AssetTestFactory.createStockAsset(
                ASSET_NAME, BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00), 0L);
        Asset initialTryAsset = AssetTestFactory.createTryAsset(
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1000.00), 0L);

        Order pendingBuyOrderLockTest = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, ORDER_SIZE,
                ORDER_PRICE, OrderStatus.PENDING, LocalDateTime.now());

        MatchOrderService service1 = new MatchOrderService(orderPersistencePort, assetPersistencePort);
        MatchOrderService service2 = new MatchOrderService(orderPersistencePort, assetPersistencePort);

        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingBuyOrderLockTest));

        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(initialStockAsset));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(initialTryAsset));


        Asset updatedStockAssetVersion1 = new Asset(
                initialStockAsset.id(), initialStockAsset.customerId(), initialStockAsset.assetName(),
                initialStockAsset.size(), initialStockAsset.usableSize(), 1L
        );

        Asset updatedTryAssetVersion1 = new Asset(
                initialTryAsset.id(), initialTryAsset.customerId(), initialTryAsset.assetName(),
                initialTryAsset.size(), initialTryAsset.usableSize(), 1L
        );

        Mockito.doReturn(updatedStockAssetVersion1)
                .doReturn(updatedTryAssetVersion1)
                .doThrow(new OptimisticLockingFailureException("Asset was updated by another transaction"))
                .when(assetPersistencePort).save(any(Asset.class));

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        service1.matchOrder(new MatchOrderCommand(ORDER_ID));

        assertThrows(OptimisticLockingFailureException.class,
                () -> service2.matchOrder(new MatchOrderCommand(ORDER_ID)));

        verify(assetPersistencePort, times(3)).findByCustomerIdAndAssetName(any(), any());

        verify(assetPersistencePort, times(3)).save(any(Asset.class));
        verify(orderPersistencePort, times(1)).save(any(Order.class));
    }
}