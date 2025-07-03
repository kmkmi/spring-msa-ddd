package com.example.ad.application.service;

import com.example.ad.domain.Advertisement;
import com.example.ad.infrastructure.repository.AdvertisementRepository;
import com.example.ad.application.dto.AdvertisementRequest;
import com.example.ad.application.dto.AdvertisementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementRepository repository;

    @Transactional
    public AdvertisementResponse createAd(AdvertisementRequest req) {
        Advertisement ad = Advertisement.builder()
                .campaignId(req.getCampaignId())
                .title(req.getTitle())
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .videoUrl(req.getVideoUrl())
                .landingPageUrl(req.getLandingPageUrl())
                .adType(req.getAdType() != null ? Advertisement.AdType.valueOf(req.getAdType()) : Advertisement.AdType.BANNER)
                .status(req.getStatus() != null ? Advertisement.AdStatus.valueOf(req.getStatus()) : Advertisement.AdStatus.DRAFT)
                .bidAmount(req.getBidAmount() != null ? java.math.BigDecimal.valueOf(req.getBidAmount()) : null)
                .dailyBudget(req.getDailyBudget() != null ? java.math.BigDecimal.valueOf(req.getDailyBudget()) : null)
                .totalSpent(req.getTotalSpent() != null ? java.math.BigDecimal.valueOf(req.getTotalSpent()) : null)
                .impressions(req.getImpressions())
                .clicks(req.getClicks())
                .ctr(req.getCtr() != null ? java.math.BigDecimal.valueOf(req.getCtr()) : null)
                .startDate(req.getStartDate() != null ? java.time.LocalDateTime.parse(req.getStartDate()) : null)
                .endDate(req.getEndDate() != null ? java.time.LocalDateTime.parse(req.getEndDate()) : null)
                .build();
        Advertisement saved = repository.save(ad);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AdvertisementResponse getAd(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AdvertisementResponse> getAdsByCampaign(Long campaignId) {
        return repository.findByCampaignId(campaignId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AdvertisementResponse updateStatus(Long id, String status) {
        Advertisement ad = repository.findById(id).orElseThrow();
        if (status != null) {
            ad.setStatus(Advertisement.AdStatus.valueOf(status));
        }
        return toResponse(repository.save(ad));
    }

    @Transactional
    public AdvertisementResponse updateMetrics(Long id, Long impressions, Long clicks, Double ctr) {
        Advertisement ad = repository.findById(id).orElseThrow();
        ad.setImpressions(impressions);
        ad.setClicks(clicks);
        ad.setCtr(ctr != null ? java.math.BigDecimal.valueOf(ctr) : null);
        return toResponse(repository.save(ad));
    }

    private AdvertisementResponse toResponse(Advertisement ad) {
        return AdvertisementResponse.builder()
                .id(ad.getId())
                .campaignId(ad.getCampaignId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .imageUrl(ad.getImageUrl())
                .videoUrl(ad.getVideoUrl())
                .landingPageUrl(ad.getLandingPageUrl())
                .adType(ad.getAdType().name())
                .status(ad.getStatus().name())
                .bidAmount(ad.getBidAmount() != null ? ad.getBidAmount().doubleValue() : null)
                .dailyBudget(ad.getDailyBudget() != null ? ad.getDailyBudget().doubleValue() : null)
                .totalSpent(ad.getTotalSpent() != null ? ad.getTotalSpent().doubleValue() : null)
                .impressions(ad.getImpressions())
                .clicks(ad.getClicks())
                .ctr(ad.getCtr() != null ? ad.getCtr().doubleValue() : null)
                .startDate(ad.getStartDate() != null ? ad.getStartDate().toString() : null)
                .endDate(ad.getEndDate() != null ? ad.getEndDate().toString() : null)
                .build();
    }
} 