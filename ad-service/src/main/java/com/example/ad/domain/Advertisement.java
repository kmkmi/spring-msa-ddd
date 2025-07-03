package com.example.ad.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Advertisement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "landing_page_url")
    private String landingPageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ad_type", nullable = false)
    private AdType adType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;
    
    @Column(name = "bid_amount", precision = 10, scale = 2)
    private BigDecimal bidAmount;
    
    @Column(name = "daily_budget", precision = 10, scale = 2)
    private BigDecimal dailyBudget;
    
    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent;
    
    @Column(name = "impressions")
    private Long impressions;
    
    @Column(name = "clicks")
    private Long clicks;
    
    @Column(name = "ctr", precision = 5, scale = 4)
    private BigDecimal ctr;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum AdType {
        BANNER, VIDEO, NATIVE, TEXT, RICH_MEDIA
    }
    
    public enum AdStatus {
        DRAFT, ACTIVE, PAUSED, COMPLETED, REJECTED
    }
    
    /**
     * 광고 상태 업데이트
     */
    public void updateStatus(AdStatus newStatus) {
        this.status = newStatus;
    }
    
    /**
     * 광고 메트릭 업데이트
     */
    public void updateMetrics(Long impressions, Long clicks, Double ctr) {
        this.impressions = impressions;
        this.clicks = clicks;
        if (ctr != null) {
            this.ctr = BigDecimal.valueOf(ctr);
        }
    }
} 