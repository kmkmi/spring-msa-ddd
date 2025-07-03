package com.example.campaign.domain.event;

import com.example.campaign.domain.Campaign;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CampaignCreatedEvent extends ApplicationEvent {
    
    private final Campaign campaign;
    
    public CampaignCreatedEvent(Campaign campaign) {
        super(campaign);
        this.campaign = campaign;
    }
} 