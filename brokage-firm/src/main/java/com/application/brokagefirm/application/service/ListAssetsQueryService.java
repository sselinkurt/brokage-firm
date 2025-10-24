package com.application.brokagefirm.application.service;

import com.application.brokagefirm.application.port.in.usecase.ListAssetsQuery;
import com.application.brokagefirm.application.port.in.command.ListAssetsQueryCommand;
import com.application.brokagefirm.application.port.out.AssetPersistencePort;
import com.application.brokagefirm.domain.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAssetsQueryService implements ListAssetsQuery {

    private final AssetPersistencePort assetPersistencePort;

    @Override
    public List<Asset> listAssets(ListAssetsQueryCommand query) {
        return assetPersistencePort.findByCustomerId(query.customerId());
    }
}
