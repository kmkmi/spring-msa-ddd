package com.example.campaign.domain.event;

import com.example.campaign.domain.Campaign;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CampaignStatusChangedEvent extends ApplicationEvent {
    
    private final Campaign campaign;
    private final Campaign.CampaignStatus oldStatus;
    private final Campaign.CampaignStatus newStatus;
    
    public CampaignStatusChangedEvent(Campaign campaign, Campaign.CampaignStatus oldStatus, Campaign.CampaignStatus newStatus) {
        super(campaign);
        this.campaign = campaign;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
} 