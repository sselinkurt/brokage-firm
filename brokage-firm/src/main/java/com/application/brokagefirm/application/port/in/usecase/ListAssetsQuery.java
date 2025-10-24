package com.application.brokagefirm.application.port.in.usecase;

import com.application.brokagefirm.application.port.in.command.ListAssetsQueryCommand;
import com.application.brokagefirm.domain.model.Asset;

import java.util.List;

public interface ListAssetsQuery {
    List<Asset> listAssets(ListAssetsQueryCommand query);
}
