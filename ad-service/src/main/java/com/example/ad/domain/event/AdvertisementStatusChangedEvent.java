package com.example.ad.domain.event;

import com.example.ad.domain.Advertisement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AdvertisementStatusChangedEvent extends ApplicationEvent {
    
    private final Advertisement advertisement;
    private final Advertisement.AdStatus oldStatus;
    private final Advertisement.AdStatus newStatus;
    
    public AdvertisementStatusChangedEvent(Advertisement advertisement, Advertisement.AdStatus oldStatus, Advertisement.AdStatus newStatus) {
        super(advertisement);
        this.advertisement = advertisement;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
} 