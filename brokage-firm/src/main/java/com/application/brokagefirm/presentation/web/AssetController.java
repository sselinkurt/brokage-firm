package com.application.brokagefirm.presentation.web;

import com.application.brokagefirm.application.port.in.usecase.ListAssetsQuery;
import com.application.brokagefirm.application.port.in.command.ListAssetsQueryCommand;
import com.application.brokagefirm.domain.model.Asset;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Asset>> listAssets(@PathVariable @NotNull(message = "Customer ID cannot be null") Long customerId) {
        ListAssetsQueryCommand query = new ListAssetsQueryCommand(customerId);

        List<Asset> assets = listAssetsQuery.listAssets(query);

        return ResponseEntity.ok(assets);
    }
}
