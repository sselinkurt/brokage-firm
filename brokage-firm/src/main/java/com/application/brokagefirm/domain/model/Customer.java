package com.application.brokagefirm.domain.model;

import com.application.brokagefirm.domain.enums.Role;

public record Customer(
        Long id,
        String username,
        String password,
        Role role
) {
}
