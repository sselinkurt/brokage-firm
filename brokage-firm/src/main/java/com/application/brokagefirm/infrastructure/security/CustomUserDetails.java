package com.application.brokagefirm.infrastructure.security;

import com.application.brokagefirm.domain.enums.Role;
import com.application.brokagefirm.domain.model.Customer;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Customer customer) {
        this.id = customer.id();
        this.username = customer.username();
        this.password = customer.password();
        this.role = customer.role();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(this.role.getSpringSecurityRoleName()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    public Long getCustomerId() {
        if (this.role.equals(Role.CUSTOMER)) {
            return this.id;
        }
        return null;
    }
}
