package com.example.shared.security;

public class SecurityConstants {
    
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/auth/signup";
    public static final String SIGN_IN_URL = "/api/auth/signin";
    public static final String REFRESH_TOKEN_URL = "/api/auth/refresh";
    public static final String H2_CONSOLE = "/h2-console/**";
    
    // 권한 상수
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_PUBLISHER = "ROLE_PUBLISHER";
    public static final String ROLE_ADVERTISER = "ROLE_ADVERTISER";
    public static final String ROLE_USER = "ROLE_USER";
    
    // API 경로 패턴
    public static final String[] PUBLIC_PATHS = {
        "/api/auth/**",
        "/actuator/health",
        "/swagger-ui/**",
        "/api-docs/**",
        "/v3/api-docs/**"
    };
    
    public static final String[] ADMIN_PATHS = {
        "/api/admin/**"
    };
    
    public static final String[] PUBLISHER_PATHS = {
        "/api/publishers/**",
        "/api/campaigns/**"
    };
    
    public static final String[] ADVERTISER_PATHS = {
        "/api/ads/**"
    };
} 