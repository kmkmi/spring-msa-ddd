package com.example.ad.domain.service;

import com.example.ad.domain.Advertisement;
import com.example.ad.domain.event.AdvertisementCreatedEvent;
import com.example.ad.domain.event.AdvertisementStatusChangedEvent;
import com.example.ad.infrastructure.repository.AdvertisementRepository;
import com.example.ad.common.exception.AdvertisementNotFoundException;
import com.example.ad.common.exception.AdvertisementValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdvertisementDomainService {
    
    private final AdvertisementRepository advertisementRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 광고 생성 시 도메인 로직 처리
     */
    public Advertisement createAdvertisement(Advertisement advertisement) {
        // 비즈니스 규칙 검증
        validateAdvertisementCreation(advertisement);
        
        // 광고 저장
        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(new AdvertisementCreatedEvent(savedAdvertisement));
        
        log.info("Advertisement created with ID: {}", savedAdvertisement.getId());
        return savedAdvertisement;
    }
    
    /**
     * 광고 상태 변경 시 도메인 로직 처리
     */
    public Advertisement updateAdvertisementStatus(Long advertisementId, Advertisement.AdStatus newStatus) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException(advertisementId));
        
        Advertisement.AdStatus oldStatus = advertisement.getStatus();
        
        // 상태 변경 검증
        validateStatusTransition(oldStatus, newStatus);
        
        // 상태 변경
        advertisement.updateStatus(newStatus);
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(new AdvertisementStatusChangedEvent(updatedAdvertisement, oldStatus, newStatus));
        
        log.info("Advertisement status changed from {} to {} for advertisement ID: {}", 
                oldStatus, newStatus, advertisementId);
        return updatedAdvertisement;
    }
    
    /**
     * 광고 메트릭 업데이트 시 도메인 로직 처리
     */
    public Advertisement updateAdvertisementMetrics(Long advertisementId, Long impressions, Long clicks, Double ctr) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException(advertisementId));
        
        // 메트릭 검증
        validateMetricsUpdate(impressions, clicks, ctr);
        
        // 메트릭 업데이트
        advertisement.updateMetrics(impressions, clicks, ctr);
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        
        log.info("Advertisement metrics updated for advertisement ID: {} - Impressions: {}, Clicks: {}, CTR: {}", 
                advertisementId, impressions, clicks, ctr);
        return updatedAdvertisement;
    }
    
    /**
     * 캠페인별 광고 조회 (도메인 로직)
     */
    @Transactional(readOnly = true)
    public List<Advertisement> getAdvertisementsByCampaignId(Long campaignId) {
        return advertisementRepository.findByCampaignId(campaignId);
    }
    
    /**
     * 광고 생성 검증
     */
    private void validateAdvertisementCreation(Advertisement advertisement) {
        if (advertisement.getTitle() == null || advertisement.getTitle().trim().isEmpty()) {
            throw new AdvertisementValidationException("Advertisement title is required");
        }
        
        if (advertisement.getCampaignId() == null) {
            throw new AdvertisementValidationException("Campaign ID is required");
        }
        
        if (advertisement.getAdType() == null) {
            throw new AdvertisementValidationException("Ad type is required");
        }
    }
    
    /**
     * 상태 변경 검증
     */
    private void validateStatusTransition(Advertisement.AdStatus oldStatus, Advertisement.AdStatus newStatus) {
        if (oldStatus == Advertisement.AdStatus.COMPLETED && newStatus != Advertisement.AdStatus.COMPLETED) {
            throw new AdvertisementValidationException("Cannot change status of completed advertisement");
        }
        
        if (oldStatus == Advertisement.AdStatus.REJECTED && newStatus != Advertisement.AdStatus.REJECTED) {
            throw new AdvertisementValidationException("Cannot change status of rejected advertisement");
        }
    }
    
    /**
     * 메트릭 업데이트 검증
     */
    private void validateMetricsUpdate(Long impressions, Long clicks, Double ctr) {
        if (impressions == null || impressions < 0) {
            throw new AdvertisementValidationException("Impressions must be non-negative");
        }
        
        if (clicks == null || clicks < 0) {
            throw new AdvertisementValidationException("Clicks must be non-negative");
        }
        
        if (clicks > impressions) {
            throw new AdvertisementValidationException("Clicks cannot exceed impressions");
        }
        
        if (ctr != null && (ctr < 0 || ctr > 100)) {
            throw new AdvertisementValidationException("CTR must be between 0 and 100");
        }
    }
} 