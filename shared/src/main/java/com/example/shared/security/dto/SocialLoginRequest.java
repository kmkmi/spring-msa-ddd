package com.example.shared.security.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class SocialLoginRequest {
    @NotBlank
    private String provider; // ex: "google"
    @NotBlank
    private String providerToken; // ex: Google id_token
    @NotBlank
    private String clientId; // ex: Google OAuth2 Client ID
} 