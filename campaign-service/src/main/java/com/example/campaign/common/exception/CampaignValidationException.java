package com.example.campaign.common.exception;

public class CampaignValidationException extends RuntimeException {
    
    public CampaignValidationException(String message) {
        super(message);
    }
    
    public CampaignValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 