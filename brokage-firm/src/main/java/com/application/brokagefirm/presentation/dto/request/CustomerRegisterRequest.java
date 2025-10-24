package com.application.brokagefirm.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CustomerRegisterRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
