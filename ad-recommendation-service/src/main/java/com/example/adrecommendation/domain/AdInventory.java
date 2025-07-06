package com.example.adrecommendation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdInventory {
    private List<Ad> ads;
    private String placement;
    private String deviceType;
    private String location;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ad {
        private Long advertisementId;
        private String creativeType;
        private String placement;
        private Double ctr;
        private Double cpc;
        private String category;
        private Boolean isActive;
    }
} 