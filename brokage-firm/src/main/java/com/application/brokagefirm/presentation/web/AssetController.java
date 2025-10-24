package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.command.ListAssetsQueryCommand;
import com.application.brokagefirm.application.port.in.usecase.ListAssetsQuery;
import com.application.brokagefirm.domain.model.Asset;
import com.application.brokagefirm.presentation.dto.response.AssetResponse;
import com.application.brokagefirm.presentation.dto.response.ResponseWrapper;
import com.application.brokagefirm.presentation.mapper.ResponseMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final ListAssetsQuery listAssetsQuery;
    private final ResponseMapper mapper;

    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == authentication.principal.customerId)")
    public ResponseEntity<ResponseWrapper<List<AssetResponse>>> listAssets(@PathVariable @NotNull(message = "Customer ID cannot be null") Long customerId) {
        ListAssetsQueryCommand query = new ListAssetsQueryCommand(customerId);

        List<Asset> assets = listAssetsQuery.listAssets(query);
        List<AssetResponse> assetResponses = assets.stream().map(mapper::toAssetResponse).toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success(null, assetResponses));
    }
}
