package com.application.brokagefirm.domain.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final BigDecimal balance;

    public InsufficientBalanceException(BigDecimal balance) {
        super("Insufficient balance. Available usable amount: " + balance);
        this.balance = balance;
    }

}
