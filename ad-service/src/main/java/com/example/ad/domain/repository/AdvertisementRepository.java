package com.example.ad.domain.repository;

import com.example.ad.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    
    List<Advertisement> findByCampaignId(Long campaignId);
    
    List<Advertisement> findByStatus(Advertisement.AdStatus status);
    
    List<Advertisement> findByAdType(Advertisement.AdType adType);
} 