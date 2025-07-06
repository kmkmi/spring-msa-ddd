package com.example.datacollector.application.service;

import com.example.datacollector.application.dto.AdEventRequest;
import com.example.datacollector.domain.AdEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataCollectorService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String AD_EVENTS_TOPIC = System.getenv("KAFKA_TOPIC_AD_EVENTS") != null ? 
        System.getenv("KAFKA_TOPIC_AD_EVENTS") : "ad-events";
    private static final String AD_METRICS_PREFIX = "ad:metrics:";
    private static final String CAMPAIGN_METRICS_PREFIX = "campaign:metrics:";
    
    /**
     * 광고 이벤트 수집 및 처리
     */
    public void collectAdEvent(AdEventRequest request) {
        log.info("Collecting ad event: {}", request);
        
        // AdEvent 도메인 객체 생성
        AdEvent adEvent = AdEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(request.getEventType())
                .advertisementId(request.getAdvertisementId())
                .campaignId(request.getCampaignId())
                .userId(request.getUserId())
                .userAgent(request.getUserAgent())
                .ipAddress(request.getIpAddress())
                .referrer(request.getReferrer())
                .bidAmount(request.getBidAmount())
                .timestamp(LocalDateTime.now())
                .deviceType(request.getDeviceType())
                .location(request.getLocation())
                .placement(request.getPlacement())
                .duration(request.getDuration())
                .isViewable(request.getIsViewable())
                .creativeType(request.getCreativeType())
                .publisherId(request.getPublisherId())
                .sessionId(request.getSessionId())
                .requestId(request.getRequestId())
                .browser(request.getBrowser())
                .os(request.getOs())
                .screenResolution(request.getScreenResolution())
                .language(request.getLanguage())
                .timezone(request.getTimezone())
                .adSize(request.getAdSize())
                .adFormat(request.getAdFormat())
                .targetingCriteria(request.getTargetingCriteria())
                .revenue(request.getRevenue())
                .conversionType(request.getConversionType())
                .conversionValue(request.getConversionValue())
                .build();
        
        // Kafka로 이벤트 전송
        sendToKafka(adEvent);
        
        // Redis에 실시간 메트릭 업데이트
        updateRealTimeMetrics(adEvent);
        
        log.info("Ad event processed successfully: {}", adEvent.getEventId());
    }
    
    /**
     * Kafka로 이벤트 전송
     */
    private void sendToKafka(AdEvent adEvent) {
        try {
            kafkaTemplate.send(AD_EVENTS_TOPIC, adEvent.getEventId(), adEvent);
            log.info("Event sent to Kafka topic: {}", AD_EVENTS_TOPIC);
        } catch (Exception e) {
            log.error("Failed to send event to Kafka: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send event to Kafka", e);
        }
    }
    
    /**
     * Redis에 실시간 메트릭 업데이트
     */
    private void updateRealTimeMetrics(AdEvent adEvent) {
        try {
            String adMetricsKey = AD_METRICS_PREFIX + adEvent.getAdvertisementId();
            String campaignMetricsKey = CAMPAIGN_METRICS_PREFIX + adEvent.getCampaignId();
            
            // 광고별 메트릭 업데이트
            updateAdMetrics(adMetricsKey, adEvent);
            
            // 캠페인별 메트릭 업데이트
            updateCampaignMetrics(campaignMetricsKey, adEvent);
            
            log.info("Real-time metrics updated for ad: {}, campaign: {}", 
                    adEvent.getAdvertisementId(), adEvent.getCampaignId());
        } catch (Exception e) {
            log.error("Failed to update real-time metrics: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 광고별 메트릭 업데이트
     */
    private void updateAdMetrics(String metricsKey, AdEvent adEvent) {
        String eventType = adEvent.getEventType();
        
        switch (eventType) {
            case "IMPRESSION":
                redisTemplate.opsForValue().increment(metricsKey + ":impressions");
                break;
            case "CLICK":
                redisTemplate.opsForValue().increment(metricsKey + ":clicks");
                break;
            case "CONVERSION":
                redisTemplate.opsForValue().increment(metricsKey + ":conversions");
                break;
        }
        
        // CTR 계산
        updateCTR(metricsKey);
    }
    
    /**
     * 캠페인별 메트릭 업데이트
     */
    private void updateCampaignMetrics(String metricsKey, AdEvent adEvent) {
        String eventType = adEvent.getEventType();
        
        switch (eventType) {
            case "IMPRESSION":
                redisTemplate.opsForValue().increment(metricsKey + ":impressions");
                break;
            case "CLICK":
                redisTemplate.opsForValue().increment(metricsKey + ":clicks");
                break;
            case "CONVERSION":
                redisTemplate.opsForValue().increment(metricsKey + ":conversions");
                break;
        }
        
        // CTR 계산
        updateCTR(metricsKey);
    }
    
    /**
     * CTR (Click Through Rate) 계산
     */
    private void updateCTR(String metricsKey) {
        try {
            Object impressions = redisTemplate.opsForValue().get(metricsKey + ":impressions");
            Object clicks = redisTemplate.opsForValue().get(metricsKey + ":clicks");
            
            if (impressions != null && clicks != null) {
                long impressionsCount = Long.parseLong(impressions.toString());
                long clicksCount = Long.parseLong(clicks.toString());
                
                if (impressionsCount > 0) {
                    double ctr = (double) clicksCount / impressionsCount;
                    redisTemplate.opsForValue().set(metricsKey + ":ctr", ctr);
                }
            }
        } catch (Exception e) {
            log.error("Failed to calculate CTR: {}", e.getMessage(), e);
        }
    }
} 