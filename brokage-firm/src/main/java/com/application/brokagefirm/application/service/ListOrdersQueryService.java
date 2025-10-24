package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.usecase.ListOrdersQuery;
import com.application.brokagefirm.application.port.in.command.ListOrdersQueryCommand;
import com.application.brokagefirm.application.port.out.OrderPersistencePort;
import com.application.brokagefirm.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListOrdersQueryService implements ListOrdersQuery {

    private final OrderPersistencePort orderPersistencePort;

    @Override
    public List<Order> listOrders(ListOrdersQueryCommand query) {
        return orderPersistencePort.findByCustomerIdAndDateRange(
                query.customerId(),
                query.startDate(),
                query.endDate()
        );
    }
}
