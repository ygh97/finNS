package com.finns.security.account.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class AuthVO implements GrantedAuthority {
    private String username; // admin
    private String authority; // ROLE_MEMBER

    @Override
    public String getAuthority() {
        return authority;
    }
}