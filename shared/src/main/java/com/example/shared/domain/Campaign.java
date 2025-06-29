package com.example.shared.domain;

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
@Table(name = "campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Campaign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false)
    private CampaignType campaignType;
    
    @Column(name = "budget_amount", precision = 10, scale = 2)
    private BigDecimal budgetAmount;
    
    @Column(name = "daily_budget", precision = 10, scale = 2)
    private BigDecimal dailyBudget;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "target_audience")
    private String targetAudience;
    
    @Column(name = "target_locations")
    private String targetLocations;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum CampaignStatus {
        DRAFT, ACTIVE, PAUSED, COMPLETED, CANCELLED
    }
    
    public enum CampaignType {
        DISPLAY, VIDEO, NATIVE, SEARCH, SOCIAL
    }
} 