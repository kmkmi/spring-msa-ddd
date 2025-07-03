package com.example.ad.common.exception;

public class AdvertisementValidationException extends RuntimeException {
    
    public AdvertisementValidationException(String message) {
        super(message);
    }
    
    public AdvertisementValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 