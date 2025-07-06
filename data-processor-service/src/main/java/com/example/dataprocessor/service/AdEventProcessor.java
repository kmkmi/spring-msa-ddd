package com.example.dataprocessor.service;

import com.example.dataprocessor.domain.AdEvent;
import com.example.dataprocessor.domain.AdMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdEventProcessor {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String AD_METRICS_TOPIC = "ad-metrics";
    private static final String AD_METRICS_PREFIX = "ad:metrics:";
    private static final String CAMPAIGN_METRICS_PREFIX = "campaign:metrics:";
    private static final String HOURLY_METRICS_PREFIX = "hourly:metrics:";
    private static final String DAILY_METRICS_PREFIX = "daily:metrics:";
    
    // 메모리 캐시 (성능 최적화)
    
    @KafkaListener(topics = "${kafka.topics.ad-events:ad-events}", groupId = "${spring.kafka.consumer.group-id:ad-processor-group}")
    public void processAdEvent(AdEvent adEvent) {
        log.info("Processing ad event: {}", adEvent.getEventId());
        
        try {
            // 실시간 메트릭 업데이트
            updateRealTimeMetrics(adEvent);
            
            // 시간별 집계
            updateHourlyMetrics(adEvent);
            
            // 일별 집계
            updateDailyMetrics(adEvent);
            
            // 이상 탐지
            detectAnomalies(adEvent);
            
            // 메트릭 이벤트 발행
            publishMetricsEvent(adEvent);
            
            log.info("Ad event processed successfully: {}", adEvent.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process ad event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 실시간 메트릭 업데이트
     */
    private void updateRealTimeMetrics(AdEvent adEvent) {
        String adMetricsKey = AD_METRICS_PREFIX + adEvent.getAdvertisementId();
        String campaignMetricsKey = CAMPAIGN_METRICS_PREFIX + adEvent.getCampaignId();
        
        // 광고별 메트릭 업데이트
        updateMetrics(adMetricsKey, adEvent);
        
        // 캠페인별 메트릭 업데이트
        updateMetrics(campaignMetricsKey, adEvent);
    }
    
    /**
     * 메트릭 업데이트
     */
    private void updateMetrics(String metricsKey, AdEvent adEvent) {
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
        calculateCTR(metricsKey);
        
        // 비용 계산
        calculateCosts(metricsKey, adEvent);
    }
    
    /**
     * 시간별 메트릭 집계
     */
    private void updateHourlyMetrics(AdEvent adEvent) {
        String hourKey = HOURLY_METRICS_PREFIX + adEvent.getAdvertisementId() + ":" + 
                        adEvent.getTimestamp().getHour();
        
        String eventType = adEvent.getEventType();
        switch (eventType) {
            case "IMPRESSION":
                redisTemplate.opsForValue().increment(hourKey + ":impressions");
                break;
            case "CLICK":
                redisTemplate.opsForValue().increment(hourKey + ":clicks");
                break;
            case "CONVERSION":
                redisTemplate.opsForValue().increment(hourKey + ":conversions");
                break;
        }
    }
    
    /**
     * 일별 메트릭 집계
     */
    private void updateDailyMetrics(AdEvent adEvent) {
        String dayKey = DAILY_METRICS_PREFIX + adEvent.getAdvertisementId() + ":" + 
                       adEvent.getTimestamp().toLocalDate();
        
        String eventType = adEvent.getEventType();
        switch (eventType) {
            case "IMPRESSION":
                redisTemplate.opsForValue().increment(dayKey + ":impressions");
                break;
            case "CLICK":
                redisTemplate.opsForValue().increment(dayKey + ":clicks");
                break;
            case "CONVERSION":
                redisTemplate.opsForValue().increment(dayKey + ":conversions");
                break;
        }
    }
    
    /**
     * CTR 계산
     */
    private void calculateCTR(String metricsKey) {
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
    
    /**
     * 비용 계산
     */
    private void calculateCosts(String metricsKey, AdEvent adEvent) {
        try {
            if (adEvent.getBidAmount() != null) {
                Object clicks = redisTemplate.opsForValue().get(metricsKey + ":clicks");
                Object impressions = redisTemplate.opsForValue().get(metricsKey + ":impressions");
                
                if (clicks != null && Long.parseLong(clicks.toString()) > 0) {
                    // CPC (Cost Per Click) 계산
                    double cpc = adEvent.getBidAmount().doubleValue();
                    redisTemplate.opsForValue().set(metricsKey + ":cpc", cpc);
                }
                
                if (impressions != null && Long.parseLong(impressions.toString()) > 0) {
                    // CPM (Cost Per Mille) 계산
                    double cpm = (adEvent.getBidAmount().doubleValue() * 1000) / 
                                Long.parseLong(impressions.toString());
                    redisTemplate.opsForValue().set(metricsKey + ":cpm", cpm);
                }
            }
        } catch (Exception e) {
            log.error("Failed to calculate costs: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 이상 탐지
     */
    private void detectAnomalies(AdEvent adEvent) {
        // CTR 이상 탐지
        detectCTRAnomaly(adEvent);
        
        // 클릭 패턴 이상 탐지
        detectClickPatternAnomaly(adEvent);
    }
    
    /**
     * CTR 이상 탐지
     */
    private void detectCTRAnomaly(AdEvent adEvent) {
        try {
            String metricsKey = AD_METRICS_PREFIX + adEvent.getAdvertisementId();
            Object ctr = redisTemplate.opsForValue().get(metricsKey + ":ctr");
            
            if (ctr != null) {
                double currentCTR = Double.parseDouble(ctr.toString());
                
                // CTR이 10%를 초과하면 이상으로 판단
                if (currentCTR > 0.10) {
                    log.warn("High CTR detected for ad {}: {}%", 
                            adEvent.getAdvertisementId(), currentCTR * 100);
                }
            }
        } catch (Exception e) {
            log.error("Failed to detect CTR anomaly: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 클릭 패턴 이상 탐지
     */
    private void detectClickPatternAnomaly(AdEvent adEvent) {
        try {
            String sessionKey = "session:" + adEvent.getSessionId();
            Object sessionClicks = redisTemplate.opsForValue().get(sessionKey + ":clicks");
            
            if (sessionClicks != null) {
                int clicks = Integer.parseInt(sessionClicks.toString());
                
                // 세션당 클릭이 10회를 초과하면 이상으로 판단
                if (clicks > 10) {
                    log.warn("Suspicious click pattern detected for session: {}", 
                            adEvent.getSessionId());
                }
            }
            
            // 세션 클릭 수 증가
            redisTemplate.opsForValue().increment(sessionKey + ":clicks");
        } catch (Exception e) {
            log.error("Failed to detect click pattern anomaly: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 메트릭 이벤트 발행
     */
    private void publishMetricsEvent(AdEvent adEvent) {
        try {
            AdMetrics metrics = buildAdMetrics(adEvent);
            kafkaTemplate.send(AD_METRICS_TOPIC, adEvent.getAdvertisementId().toString(), metrics);
            log.info("Metrics event published for ad: {}", adEvent.getAdvertisementId());
        } catch (Exception e) {
            log.error("Failed to publish metrics event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * AdMetrics 객체 생성
     */
    private AdMetrics buildAdMetrics(AdEvent adEvent) {
        return AdMetrics.builder()
                .advertisementId(adEvent.getAdvertisementId())
                .campaignId(adEvent.getCampaignId())
                .deviceType(adEvent.getDeviceType())
                .location(adEvent.getLocation())
                .creativeType(adEvent.getCreativeType())
                .publisherId(adEvent.getPublisherId())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
} 