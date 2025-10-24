package com.application.brokagefirm.domain.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    CUSTOMER("ROLE_CUSTOMER");

    private final String springSecurityRoleName;

    Role(String springSecurityRoleName) {
        this.springSecurityRoleName = springSecurityRoleName;
    }
}
