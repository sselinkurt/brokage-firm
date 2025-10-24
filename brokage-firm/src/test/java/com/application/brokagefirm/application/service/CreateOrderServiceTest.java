package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.command.CreateOrderCommand;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.enums.OrderSide;
import com.application.brokagefirm.domain.enums.OrderStatus;
import com.application.brokagefirm.domain.exception.InsufficientBalanceException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderPersistencePort orderPersistencePort;

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @InjectMocks
    private CreateOrderService createOrderService;

    private static final Long CUSTOMER_ID = 101L;
    private static final String ASSET_NAME = "XYZ";
    private Asset tryAsset;
    private Asset stockAsset;
    private Asset expectedUpdatedTryAsset;
    private CreateOrderCommand buyCommand;
    private CreateOrderCommand sellCommand;

    @BeforeEach
    void setUp() {
        tryAsset = AssetTestFactory.createTryAsset(
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(1000.00), 0L);

        stockAsset = AssetTestFactory.createStockAsset(
                ASSET_NAME, BigDecimal.valueOf(50.00), BigDecimal.valueOf(50.00), 0L);

        buyCommand = new CreateOrderCommand(
                CUSTOMER_ID, ASSET_NAME, OrderSide.BUY, BigDecimal.valueOf(10.00), BigDecimal.valueOf(10.00));

        sellCommand = new CreateOrderCommand(
                CUSTOMER_ID, ASSET_NAME, OrderSide.SELL, BigDecimal.valueOf(10.00), BigDecimal.valueOf(10.00));

        expectedUpdatedTryAsset = AssetTestFactory.createTryAsset(
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(900.00), 0L);
    }

    @Test
    @DisplayName("BUY Order - Successful Order Creation and TRY Balance Reservation")
    void createOrder_shouldCreateBuyOrderAndDecreaseTryUsableSize() {
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));
        when(assetPersistencePort.save(any(Asset.class)))
                .thenReturn(expectedUpdatedTryAsset);
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = createOrderService.createOrder(buyCommand);

        assertThat(createdOrder.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(createdOrder.orderSide()).isEqualTo(OrderSide.BUY);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(1)).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(900.00));
        assertThat(capturedAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(capturedAsset.version()).isEqualTo(0L);

        verify(orderPersistencePort, times(1)).save(any(Order.class));
        verify(assetPersistencePort, times(2)).findByCustomerIdAndAssetName(eq(CUSTOMER_ID), anyString());
    }

    @Test
    @DisplayName("BUY Order - Should Throw Exception on Insufficient TRY Balance")
    void createOrder_shouldThrowExceptionOnInsufficientTryBalance() {
        tryAsset = AssetTestFactory.createTryAsset(
                BigDecimal.valueOf(1000.00), BigDecimal.valueOf(50.00), 0L);

        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        assertThrows(InsufficientBalanceException.class,
                () -> createOrderService.createOrder(buyCommand));

        verify(assetPersistencePort, never()).save(any(Asset.class));
        verify(orderPersistencePort, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("BUY Order - Should Create New Asset with Zero Balance if it Does Not Exist")
    void createOrder_shouldCreateNewAssetIfItDoesNotExist() {
        Asset newStockAsset = AssetTestFactory.createNewStockAsset(ASSET_NAME);

        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.empty());
        when(assetPersistencePort.save(any(Asset.class)))
                .thenReturn(expectedUpdatedTryAsset, newStockAsset);
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        createOrderService.createOrder(buyCommand);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(2)).save(assetCaptor.capture());

        Asset capturedNewAsset = assetCaptor.getAllValues().get(1);
        assertThat(capturedNewAsset.assetName()).isEqualTo(ASSET_NAME);
        assertThat(capturedNewAsset.size()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(capturedNewAsset.usableSize()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(capturedNewAsset.version()).isEqualTo(0L);
    }

    @Test
    @DisplayName("SELL Order - Successful Order Creation and Asset Balance Reservation")
    void createOrder_shouldCreateSellOrderAndDecreaseStockUsableSize() {
        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));

        when(assetPersistencePort.save(any(Asset.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderPersistencePort.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        createOrderService.createOrder(sellCommand);

        verify(assetPersistencePort, times(1)).findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderPersistencePort, times(1)).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().status()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderCaptor.getValue().orderSide()).isEqualTo(OrderSide.SELL);

        ArgumentCaptor<Asset> assetCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetPersistencePort, times(1)).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.usableSize()).isEqualByComparingTo(BigDecimal.valueOf(40.00));
        assertThat(capturedAsset.size()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(capturedAsset.version()).isEqualTo(0L);
    }

    @Test
    @DisplayName("SELL Order - Should Throw Exception on Insufficient Stock Balance")
    void createOrder_shouldThrowExceptionOnInsufficientStockBalance() {
        stockAsset = AssetTestFactory.createStockAsset(
                ASSET_NAME, BigDecimal.valueOf(50.00), BigDecimal.valueOf(5.00), 0L);

        when(assetPersistencePort.findByCustomerIdAndAssetName(CUSTOMER_ID, ASSET_NAME))
                .thenReturn(Optional.of(stockAsset));

        assertThrows(InsufficientBalanceException.class,
                () -> createOrderService.createOrder(sellCommand));

        verify(assetPersistencePort, never()).save(any(Asset.class));
        verify(orderPersistencePort, never()).save(any(Order.class));
    }
}