package com.application.brokagefirm.application.port.in.command;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ListOrdersQueryCommand(
        @NotNull(message = "Customer ID cannot be null")
        Long customerId,

        LocalDateTime startDate,

        LocalDateTime endDate
) {
}
