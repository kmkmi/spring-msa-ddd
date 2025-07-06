package com.example.adrecommendation.controller;

import com.example.adrecommendation.domain.AdRecommendation;
import com.example.adrecommendation.dto.AdRecommendationRequest;
import com.example.adrecommendation.service.AdRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class AdRecommendationController {

    private final AdRecommendationService adRecommendationService;

    @GetMapping("/health")
    public String health() {
        return "Ad Recommendation Service is running!";
    }

    @PostMapping("/ads")
    public AdRecommendation getRecommendedAds(@RequestBody AdRecommendationRequest request) {
        return adRecommendationService.getRecommendations(request);
    }

    @GetMapping("/ads/{userId}")
    public AdRecommendation getRecommendedAdsForUser(@PathVariable String userId) {
        AdRecommendationRequest request = AdRecommendationRequest.builder()
            .userId(userId)
            .sessionId("sess_" + System.currentTimeMillis())
            .deviceType("MOBILE")
            .location("Seoul")
            .placement("TOP_BANNER")
            .limit(3)
            .build();
        return adRecommendationService.getRecommendations(request);
    }
} 