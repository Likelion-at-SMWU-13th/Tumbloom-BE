package com.tumbloom.tumblerin.global.security;

public class SecurityConstants {
    public static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-resources",
            "/webjars/**",
            "/configuration/**",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh"
    };
}
