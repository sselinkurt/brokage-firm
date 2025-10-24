package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.exception.InvalidOperationException;
import com.application.brokagefirm.domain.exception.ResourceNotFoundException;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteOrderServiceTest {

    @Mock
    private OrderPersistencePort orderPersistencePort;

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @InjectMocks
    private DeleteOrderService deleteOrderService;

    private static final Long CUSTOMER_ID = 101L;
    private static final Long ORDER_ID = 500L;
    private static final String ASSET_NAME = "XYZ";
    private Order pendingBuyOrder;
    private Order pendingSellOrder;
    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        tryAsset = AssetTestFactory.createTryAsset(
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(900.00), 0L);

        stockAsset = AssetTestFactory.createStockAsset(
                ASSET_NAME, BigDecimal.valueOf(50.00), BigDecimal.valueOf(40.00), 0L);

        pendingBuyOrder = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(10.00), OrderStatus.PENDING, LocalDateTime.now());

        pendingSellOrder = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.SELL, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(10.00), OrderStatus.PENDING, LocalDateTime.now());
    }

    @Test
    @DisplayName("BUY Order - Successful Cancellation and TRY Usable Balance Restoration")
    void deleteOrder_shouldCancelBuyOrderAndRestoreTryUsableSize() {
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingBuyOrder));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetPersistencePort.save(any(Asset.class)))
                .thenAnswer(i -> i.getArgument(0));

        deleteOrderService.deleteOrder(new DeleteOrderCommand(ORDER_ID));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status()).isEqualTo(OrderStatus.CANCELED);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(1)).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(capturedAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(capturedAsset.version()).isEqualTo(0L);
    }

    @Test
    @DisplayName("SELL Order - Successful Cancellation and Stock Usable Balance Restoration")
    void deleteOrder_shouldCancelSellOrderAndRestoreStockUsableSize() {
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingSellOrder));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));
        when(assetPersistencePort.save(any(Asset.class)))
                .thenAnswer(i -> i.getArgument(0));

        deleteOrderService.deleteOrder(new DeleteOrderCommand(ORDER_ID));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status()).isEqualTo(OrderStatus.CANCELED);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(1)).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(capturedAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    @Test
    @DisplayName("Should Only Allow PENDING Orders to be Canceled")
    void deleteOrder_shouldThrowExceptionIfOrderStatusIsNotPending() {
        Order matchedOrderMock = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(10.00), OrderStatus.MATCHED, LocalDateTime.now());

        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(matchedOrderMock));

        assertThrows(InvalidOperationException.class,
                () -> deleteOrderService.deleteOrder(new DeleteOrderCommand(ORDER_ID)));

        verify(assetPersistencePort, never()).save(any(Asset.class));
        verify(orderPersistencePort, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should Throw ResourceNotFoundException If Order Does Not Exist")
    void deleteOrder_shouldThrowResourceNotFoundExceptionIfOrderDoesNotExist() {
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> deleteOrderService.deleteOrder(new DeleteOrderCommand(ORDER_ID)));

        verify(assetPersistencePort, never()).save(any(Asset.class));
    }
}