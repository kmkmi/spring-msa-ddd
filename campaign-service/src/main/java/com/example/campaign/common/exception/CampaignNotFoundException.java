package com.example.campaign.common.exception;

public class CampaignNotFoundException extends RuntimeException {
    
    public CampaignNotFoundException(String message) {
        super(message);
    }
    
    public CampaignNotFoundException(Long campaignId) {
        super("Campaign not found with id: " + campaignId);
    }
} 