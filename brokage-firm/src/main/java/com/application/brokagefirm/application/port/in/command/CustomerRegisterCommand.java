package com.application.brokagefirm.application.port.in.command;

import com.application.brokagefirm.domain.enums.Role;

public record CustomerRegisterCommand(
        String username,
        String password, // todo: hash
        Role role
) {
}
