package com.application.brokagefirm.presentation.mapper;

import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.domain.model.Order;
import com.application.brokagefirm.presentation.dto.response.AssetResponse;
import com.application.brokagefirm.presentation.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ResponseMapper {

    @Mappings({
            @Mapping(source = "orderSide", target = "orderSide"),
            @Mapping(source = "status", target = "status")
    })
    OrderResponse toOrderResponse(Order order);

    AssetResponse toAssetResponse(Asset asset);
}
