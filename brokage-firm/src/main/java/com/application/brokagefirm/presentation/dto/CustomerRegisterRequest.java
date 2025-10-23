package com.application.brokagefirm.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRegisterRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
