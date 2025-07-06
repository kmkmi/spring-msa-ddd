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
public class AdMetrics {
    
    private String metricsId;
    private String advertisementId;
    private String campaignId;
    private LocalDateTime timestamp;
    
    // 기본 메트릭
    private Long impressions;
    private Long clicks;
    private Long conversions;
    
    // 비율 메트릭
    private BigDecimal ctr; // Click Through Rate
    private BigDecimal cvr; // Conversion Rate
    private BigDecimal viewabilityRate;
    
    // 비용 메트릭
    private BigDecimal totalSpend;
    private BigDecimal cpc; // Cost Per Click
    private BigDecimal cpm; // Cost Per Mille
    private BigDecimal cpa; // Cost Per Acquisition
    
    // 수익 메트릭
    private BigDecimal totalRevenue;
    private BigDecimal roas; // Return on Ad Spend
    private BigDecimal profit;
    private BigDecimal profitMargin;
    
    // 시간별 메트릭
    private String timeGranularity; // HOURLY, DAILY, WEEKLY, MONTHLY
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    
    // 추가 메트릭
    private Long uniqueUsers;
    private Long sessions;
    private BigDecimal avgSessionDuration;
    private BigDecimal bounceRate;
    private BigDecimal engagementRate;
    
    // 디바이스별 메트릭
    private Long mobileImpressions;
    private Long desktopImpressions;
    private Long tabletImpressions;
    private String deviceType;
    private String location;
    private String publisherId;
    private LocalDateTime lastUpdated;
    
    // 지역별 메트릭
    private String topLocation;
    private Long locationImpressions;
    
    // 크리에이티브별 메트릭
    private String creativeType;
    private Long creativeImpressions;
    private BigDecimal creativeCtr;
} 