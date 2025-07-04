package com.example.campaign.infrastructure.repository;

import com.example.campaign.domain.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    Page<Campaign> findByPublisherId(Long publisherId, Pageable pageable);
    
    List<Campaign> findByPublisherId(Long publisherId);
    
    List<Campaign> findByStatus(Campaign.CampaignStatus status);
    
    List<Campaign> findByCampaignType(Campaign.CampaignType campaignType);
    
    @Query("SELECT c FROM Campaign c WHERE c.publisherId = :publisherId AND c.status = :status")
    List<Campaign> findByPublisherIdAndStatus(@Param("publisherId") Long publisherId, 
                                             @Param("status") Campaign.CampaignStatus status);
    
    @Query("SELECT c FROM Campaign c WHERE c.startDate <= :now AND c.endDate >= :now AND c.status = 'ACTIVE'")
    List<Campaign> findActiveCampaigns(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Campaign c WHERE c.budgetAmount > 0 AND c.status = 'ACTIVE'")
    List<Campaign> findActiveCampaignsWithBudget();
} 