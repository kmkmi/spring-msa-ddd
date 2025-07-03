package com.example.ad.domain.event;

import com.example.ad.domain.Advertisement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AdvertisementCreatedEvent extends ApplicationEvent {
    
    private final Advertisement advertisement;
    
    public AdvertisementCreatedEvent(Advertisement advertisement) {
        super(advertisement);
        this.advertisement = advertisement;
    }
} 