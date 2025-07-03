package com.example.ad.common.exception;

public class AdvertisementNotFoundException extends RuntimeException {
    
    public AdvertisementNotFoundException(String message) {
        super(message);
    }
    
    public AdvertisementNotFoundException(Long advertisementId) {
        super("Advertisement not found with id: " + advertisementId);
    }
} 