package com.example.ad.application.service;

import com.example.ad.domain.Advertisement;
import com.example.ad.infrastructure.repository.AdvertisementRepository;
import com.example.ad.application.dto.AdvertisementRequest;
import com.example.ad.application.dto.AdvertisementResponse;
import com.example.ad.common.exception.AdvertisementNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {
    private final AdvertisementRepository repository;

    @Transactional
    public AdvertisementResponse createAd(AdvertisementRequest request) {
        log.info("Creating advertisement for campaign: {}", request.getCampaignId());
        
        Advertisement advertisement = Advertisement.builder()
                .campaignId(request.getCampaignId())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .videoUrl(request.getVideoUrl())
                .landingPageUrl(request.getLandingPageUrl())
                .adType(request.getAdType() != null ? Advertisement.AdType.valueOf(request.getAdType()) : Advertisement.AdType.BANNER)
                .status(request.getStatus() != null ? Advertisement.AdStatus.valueOf(request.getStatus()) : Advertisement.AdStatus.DRAFT)
                .bidAmount(request.getBidAmount() != null ? java.math.BigDecimal.valueOf(request.getBidAmount()) : null)
                .dailyBudget(request.getDailyBudget() != null ? java.math.BigDecimal.valueOf(request.getDailyBudget()) : null)
                .totalSpent(request.getTotalSpent() != null ? java.math.BigDecimal.valueOf(request.getTotalSpent()) : null)
                .impressions(request.getImpressions())
                .clicks(request.getClicks())
                .ctr(request.getCtr() != null ? java.math.BigDecimal.valueOf(request.getCtr()) : null)
                .startDate(request.getStartDate() != null ? java.time.LocalDateTime.parse(request.getStartDate()) : null)
                .endDate(request.getEndDate() != null ? java.time.LocalDateTime.parse(request.getEndDate()) : null)
                .build();
        
        Advertisement savedAdvertisement = repository.save(advertisement);
        log.info("Advertisement created with id: {}", savedAdvertisement.getId());
        
        return toResponse(savedAdvertisement);
    }

    @Transactional(readOnly = true)
    public AdvertisementResponse getAd(Long id) {
        log.info("Getting advertisement by id: {}", id);
        
        return repository.findById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementResponse> getAdsByCampaign(Long campaignId) {
        log.info("Getting advertisements by campaign id: {}", campaignId);
        
        return repository.findByCampaignId(campaignId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementResponse updateStatus(Long id, String status) {
        log.info("Updating advertisement status for id: {} to: {}", id, status);
        
        Advertisement advertisement = repository.findById(id)
                .orElseThrow(() -> new AdvertisementNotFoundException(id));
        
        if (status != null) {
            advertisement.setStatus(Advertisement.AdStatus.valueOf(status));
        }
        
        Advertisement updatedAdvertisement = repository.save(advertisement);
        return toResponse(updatedAdvertisement);
    }

    @Transactional
    public AdvertisementResponse updateMetrics(Long id, Long impressions, Long clicks, Double ctr) {
        log.info("Updating advertisement metrics for id: {} - impressions: {}, clicks: {}, ctr: {}", 
                id, impressions, clicks, ctr);
        
        Advertisement advertisement = repository.findById(id)
                .orElseThrow(() -> new AdvertisementNotFoundException(id));
        
        advertisement.setImpressions(impressions);
        advertisement.setClicks(clicks);
        advertisement.setCtr(ctr != null ? java.math.BigDecimal.valueOf(ctr) : null);
        
        Advertisement updatedAdvertisement = repository.save(advertisement);
        return toResponse(updatedAdvertisement);
    }

    private AdvertisementResponse toResponse(Advertisement advertisement) {
        return AdvertisementResponse.builder()
                .id(advertisement.getId())
                .campaignId(advertisement.getCampaignId())
                .title(advertisement.getTitle())
                .description(advertisement.getDescription())
                .imageUrl(advertisement.getImageUrl())
                .videoUrl(advertisement.getVideoUrl())
                .landingPageUrl(advertisement.getLandingPageUrl())
                .adType(advertisement.getAdType().name())
                .status(advertisement.getStatus().name())
                .bidAmount(advertisement.getBidAmount() != null ? advertisement.getBidAmount().doubleValue() : null)
                .dailyBudget(advertisement.getDailyBudget() != null ? advertisement.getDailyBudget().doubleValue() : null)
                .totalSpent(advertisement.getTotalSpent() != null ? advertisement.getTotalSpent().doubleValue() : null)
                .impressions(advertisement.getImpressions())
                .clicks(advertisement.getClicks())
                .ctr(advertisement.getCtr() != null ? advertisement.getCtr().doubleValue() : null)
                .startDate(advertisement.getStartDate() != null ? advertisement.getStartDate().toString() : null)
                .endDate(advertisement.getEndDate() != null ? advertisement.getEndDate().toString() : null)
                .build();
    }
} 