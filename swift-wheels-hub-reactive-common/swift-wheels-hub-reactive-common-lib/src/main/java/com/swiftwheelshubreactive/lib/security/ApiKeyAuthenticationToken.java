package com.swiftwheelshubreactive.lib.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthenticationToken implements Authentication {

    private final String apiKey;
    private String principal;
    private boolean authenticated = false;

    public ApiKeyAuthenticationToken(String apiKey) {
        this.apiKey = apiKey;
    }

    public ApiKeyAuthenticationToken(String apiKey, String principal) {
        this.apiKey = apiKey;
        this.principal = principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal;
    }

}
