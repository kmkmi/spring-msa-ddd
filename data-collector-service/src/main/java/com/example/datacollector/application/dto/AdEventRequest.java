package com.example.datacollector.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdEventRequest {
    
    @NotBlank(message = "광고 ID는 필수입니다")
    private String advertisementId;
    
    @NotBlank(message = "캠페인 ID는 필수입니다")
    private String campaignId;
    
    private String userId;
    
    @NotBlank(message = "이벤트 타입은 필수입니다")
    private String eventType; // IMPRESSION, CLICK, CONVERSION
    
    private String userAgent;
    
    private String ipAddress;
    
    private String referrer;
    
    private BigDecimal bidAmount;
    
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
} 