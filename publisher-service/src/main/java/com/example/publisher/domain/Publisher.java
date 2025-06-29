package com.example.publisher.domain;

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
@Table(name = "publishers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Publisher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublisherStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "publisher_type", nullable = false)
    private PublisherType publisherType;
    
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "total_spent", precision = 10, scale = 2)
    private BigDecimal totalSpent;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "contact_address")
    private String contactAddress;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PublisherStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_APPROVAL
    }
    
    public enum PublisherType {
        ADVERTISER, PUBLISHER, AGENCY
    }
} 