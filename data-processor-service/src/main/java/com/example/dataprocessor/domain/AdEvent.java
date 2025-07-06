package com.example.dataprocessor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEvent {
    private String eventId;
    private String eventType; // IMPRESSION, CLICK, CONVERSION
    private String advertisementId;
    private String campaignId;
    private String userId;
    private String userAgent;
    private String ipAddress;
    private String referrer;
    private BigDecimal bidAmount;
    private LocalDateTime timestamp;
    private String deviceType; // MOBILE, DESKTOP, TABLET
    private String location; // 국가/지역 정보
    private String placement; // 광고 위치
    private Integer duration; // 노출 시간 (초)
    private Boolean isViewable; // 뷰어블 여부
    private String creativeType; // 이미지, 비디오, HTML 등
    private String publisherId;
    private String sessionId;
    private String requestId;
    
    // 추가 필드들
    private String browser;
    private String os;
    private String screenResolution;
    private String language;
    private String timezone;
    private String adSize;
    private String adFormat;
    private String targetingCriteria;
    private BigDecimal revenue;
    private String conversionType;
    private String conversionValue;
    
    // 메트릭 계산을 위한 필드들
    private BigDecimal ctr; // Click Through Rate
    private BigDecimal cpc; // Cost Per Click
    private BigDecimal cpm; // Cost Per Mille
    private BigDecimal roas; // Return on Ad Spend
} 