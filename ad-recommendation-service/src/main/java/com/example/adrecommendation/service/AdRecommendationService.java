package com.example.adrecommendation.service;

import com.example.adrecommendation.domain.AdRecommendation;
import com.example.adrecommendation.dto.AdRecommendationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdRecommendationService {

    public AdRecommendation getRecommendations(AdRecommendationRequest request) {
        log.info("Getting recommendations for user: {}", request.getUserId());
        
        // 간단한 추천 로직 구현
        List<AdRecommendation.RecommendedAd> recommendedAds = new ArrayList<>();
        
        // 샘플 추천 광고 생성
        for (int i = 1; i <= 3; i++) {
            AdRecommendation.RecommendedAd ad = AdRecommendation.RecommendedAd.builder()
                .advertisementId((long) i)
                .score(0.8 - (i * 0.1))
                .reason("High CTR for this user segment")
                .creativeType("BANNER")
                .placement(request.getPlacement())
                .build();
            recommendedAds.add(ad);
        }
        
        return AdRecommendation.builder()
            .requestId("req_" + System.currentTimeMillis())
            .userId(request.getUserId())
            .sessionId(request.getSessionId())
            .deviceType(request.getDeviceType())
            .location(request.getLocation())
            .placement(request.getPlacement())
            .recommendedAds(recommendedAds)
            .timestamp(LocalDateTime.now())
            .responseTime(System.currentTimeMillis())
            .build();
    }
} 