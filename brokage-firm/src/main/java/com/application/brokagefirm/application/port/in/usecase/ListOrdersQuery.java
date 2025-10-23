package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.ListOrdersQueryCommand;
import com.application.brokagefirm.domain.model.Order;

import java.util.List;

public interface ListOrdersQuery {
    List<Order> listOrders(ListOrdersQueryCommand query);
}
