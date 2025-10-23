package com.application.brokagefirm.presentation.dto;

import com.application.brokagefirm.domain.enums.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull(message = "Customer ID cannot be null")
        Long customerId,

        @NotBlank
        String assetName,

        @NotNull
        OrderSide side,

        @NotNull
        @DecimalMin(value = "0.01", message = "Size must be greater than zero")
        BigDecimal size,

        @NotNull
        @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
        BigDecimal price
) {
}
