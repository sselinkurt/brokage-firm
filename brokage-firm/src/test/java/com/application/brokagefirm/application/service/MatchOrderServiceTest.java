package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.exception.InvalidOperationException;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchOrderServiceTest {

    @Mock
    private OrderPersistencePort orderPersistencePort;

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @InjectMocks
    private MatchOrderService matchOrderService;

    private static final Long CUSTOMER_ID = 101L;
    private static final Long ORDER_ID = 600L;
    private static final String ASSET_NAME = "XYZ";
    private static final BigDecimal ORDER_SIZE = BigDecimal.valueOf(10.00);
    private static final BigDecimal ORDER_PRICE = BigDecimal.valueOf(10.00);
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
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, ORDER_SIZE,
                ORDER_PRICE, OrderStatus.PENDING, LocalDateTime.now());

        pendingSellOrder = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.SELL, ORDER_SIZE,
                ORDER_PRICE, OrderStatus.PENDING, LocalDateTime.now());
    }

    @Test
    @DisplayName("BUY Order - Successful Match: Updates TRY Size/Usable and Stock Size/Usable")
    void matchOrder_shouldMatchBuyOrderAndUpdateBothAssetsCorrectly() {
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingBuyOrder));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        matchOrderService.matchOrder(new MatchOrderCommand(ORDER_ID));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status()).isEqualTo(OrderStatus.MATCHED);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(2)).save(assetCaptor.capture());

        List<Asset> savedAssets = assetCaptor.getAllValues();
        Asset capturedStockAsset = savedAssets.get(0);
        Asset capturedTryAsset = savedAssets.get(1);

        assertThat(capturedStockAsset.assetName()).isEqualTo(ASSET_NAME);
        assertThat(capturedStockAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
        assertThat(capturedStockAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(50.00));

        assertThat(capturedTryAsset.assetName()).isEqualTo("TRY");
        assertThat(capturedTryAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(900.00));

        assertThat(capturedTryAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(900.00));
    }

    @Test
    @DisplayName("SELL Order - Successful Match: Updates TRY Size/Usable and Stock Size")
    void matchOrder_shouldMatchSellOrderAndUpdateBothAssetsCorrectly() {
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingSellOrder));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        matchOrderService.matchOrder(new MatchOrderCommand(ORDER_ID));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status()).isEqualTo(OrderStatus.MATCHED);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(2)).save(assetCaptor.capture());

        List<Asset> savedAssets = assetCaptor.getAllValues();
        Asset capturedStockAsset = savedAssets.get(0);
        Asset capturedTryAsset = savedAssets.get(1);

        assertThat(capturedStockAsset.assetName()).isEqualTo(ASSET_NAME);
        assertThat(capturedStockAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
        assertThat(capturedStockAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(40.00));

        assertThat(capturedTryAsset.assetName()).isEqualTo("TRY");
        assertThat(capturedTryAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(1100.00));
        assertThat(capturedTryAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
    }

    @Test
    @DisplayName("Should Throw Exception if Order Status is not PENDING")
    void matchOrder_shouldThrowExceptionIfOrderStatusIsNotPending() {
        Order matchedOrderMock = new Order(
                ORDER_ID, CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, ORDER_SIZE,
                ORDER_PRICE, OrderStatus.MATCHED, LocalDateTime.now());

        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(matchedOrderMock));

        assertThrows(InvalidOperationException.class,
                () -> matchOrderService.matchOrder(new MatchOrderCommand(ORDER_ID)),
                "Only PENDING orders can be matched. Current status: MATCHED");

        verify(assetPersistencePort, never()).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should Throw Exception if Asset is TRY")
    void matchOrder_shouldThrowExceptionIfAssetIsTry() {
        Order tryOrderMock = new Order(
                ORDER_ID, CUSTOMER_ID, "TRY", OrderSide.BUY, ORDER_SIZE,
                ORDER_PRICE, OrderStatus.PENDING, LocalDateTime.now());

        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(tryOrderMock));

        assertThrows(InvalidOperationException.class,
                () -> matchOrderService.matchOrder(new MatchOrderCommand(ORDER_ID)),
                "Invalid asset: TRY");

        verify(assetPersistencePort, never()).save(any(Asset.class));
    }
}