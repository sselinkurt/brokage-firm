package com.application.brokagefirm.application.port.in.command;

import jakarta.validation.constraints.NotNull;

public record MatchOrderCommand(
        @NotNull(message = "Order ID cannot be null")
        Long orderId
) {
}
