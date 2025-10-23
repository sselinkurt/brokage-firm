package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.DeleteOrderCommand;

public interface DeleteOrderUseCase {
    void deleteOrder(DeleteOrderCommand command);
}
