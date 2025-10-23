package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.MatchOrderCommand;
import com.application.brokagefirm.domain.model.Order;

public interface MatchOrderUseCase {
    Order matchOrder(MatchOrderCommand command);
}
