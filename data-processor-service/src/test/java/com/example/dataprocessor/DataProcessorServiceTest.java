package com.example.dataprocessor;

import com.example.dataprocessor.domain.AdEvent;
import com.example.dataprocessor.domain.AdMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DataProcessorServiceTest {


    @BeforeEach
    void setUp() {
        // 테스트 전 초기화 작업
    }

    @Test
    void testAdEventBuilder() {
        // AdEvent 빌더 테스트
        AdEvent adEvent = AdEvent.builder()
                .eventId("event123")
                .eventType("IMPRESSION")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .timestamp(LocalDateTime.now())
                .deviceType("MOBILE")
                .location("Seoul")
                .build();
        
        assertNotNull(adEvent);
        assertEquals("event123", adEvent.getEventId());
        assertEquals("IMPRESSION", adEvent.getEventType());
        assertEquals("1", adEvent.getAdvertisementId());
        assertEquals("1", adEvent.getCampaignId());
        assertEquals("user123", adEvent.getUserId());
        assertEquals("MOBILE", adEvent.getDeviceType());
        assertEquals("Seoul", adEvent.getLocation());
    }

    @Test
    void testAdEventWithClick() {
        // 클릭 이벤트로 AdEvent 생성 테스트
        AdEvent adEvent = AdEvent.builder()
                .eventId("event456")
                .eventType("CLICK")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .timestamp(LocalDateTime.now())
                .deviceType("DESKTOP")
                .location("Busan")
                .bidAmount(new BigDecimal("1.50"))
                .build();
        
        assertNotNull(adEvent);
        assertEquals("event456", adEvent.getEventId());
        assertEquals("CLICK", adEvent.getEventType());
        assertEquals("1", adEvent.getAdvertisementId());
        assertEquals("1", adEvent.getCampaignId());
        assertEquals("user123", adEvent.getUserId());
        assertEquals("DESKTOP", adEvent.getDeviceType());
        assertEquals("Busan", adEvent.getLocation());
        assertEquals(new BigDecimal("1.50"), adEvent.getBidAmount());
    }

    @Test
    void testAdEventWithConversion() {
        // 전환 이벤트로 AdEvent 생성 테스트
        AdEvent adEvent = AdEvent.builder()
                .eventId("event789")
                .eventType("CONVERSION")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .timestamp(LocalDateTime.now())
                .deviceType("MOBILE")
                .location("Incheon")
                .conversionValue("100.0")
                .build();
        
        assertNotNull(adEvent);
        assertEquals("event789", adEvent.getEventId());
        assertEquals("CONVERSION", adEvent.getEventType());
        assertEquals("1", adEvent.getAdvertisementId());
        assertEquals("1", adEvent.getCampaignId());
        assertEquals("user123", adEvent.getUserId());
        assertEquals("MOBILE", adEvent.getDeviceType());
        assertEquals("Incheon", adEvent.getLocation());
        assertEquals("100.0", adEvent.getConversionValue());
    }

    @Test
    void testAdMetricsBuilder() {
        // AdMetrics 빌더 테스트
        AdMetrics metrics = AdMetrics.builder()
                .metricsId("metrics123")
                .advertisementId("1")
                .campaignId("1")
                .timestamp(LocalDateTime.now())
                .impressions(1000L)
                .clicks(50L)
                .conversions(5L)
                .ctr(new BigDecimal("5.0"))
                .cvr(new BigDecimal("10.0"))
                .totalSpend(new BigDecimal("75.0"))
                .totalRevenue(new BigDecimal("500.0"))
                .deviceType("MOBILE")
                .location("Seoul")
                .build();
        
        assertNotNull(metrics);
        assertEquals("1", metrics.getAdvertisementId());
        assertEquals("1", metrics.getCampaignId());
        assertEquals(1000L, metrics.getImpressions());
        assertEquals(50L, metrics.getClicks());
        assertEquals(5L, metrics.getConversions());
        assertEquals(new BigDecimal("5.0"), metrics.getCtr());
        assertEquals(new BigDecimal("10.0"), metrics.getCvr());
    }

    @Test
    void testAdMetricsWithZeroValues() {
        // 0값으로 AdMetrics 생성 테스트
        AdMetrics metrics = AdMetrics.builder()
                .advertisementId("1")
                .campaignId("1")
                .impressions(0L)
                .clicks(0L)
                .conversions(0L)
                .ctr(BigDecimal.ZERO)
                .cvr(BigDecimal.ZERO)
                .build();
        
        assertNotNull(metrics);
        assertEquals(0L, metrics.getImpressions());
        assertEquals(0L, metrics.getClicks());
        assertEquals(0L, metrics.getConversions());
        assertEquals(BigDecimal.ZERO, metrics.getCtr());
        assertEquals(BigDecimal.ZERO, metrics.getCvr());
    }

    @Test
    void testAdMetricsWithHighValues() {
        // 높은 값으로 AdMetrics 생성 테스트
        AdMetrics metrics = AdMetrics.builder()
                .advertisementId("1")
                .campaignId("1")
                .impressions(1000000L)
                .clicks(50000L)
                .conversions(5000L)
                .ctr(new BigDecimal("5.0"))
                .cvr(new BigDecimal("10.0"))
                .totalSpend(new BigDecimal("75000.0"))
                .totalRevenue(new BigDecimal("500000.0"))
                .roas(new BigDecimal("6.67"))
                .build();
        
        assertNotNull(metrics);
        assertEquals(1000000L, metrics.getImpressions());
        assertEquals(50000L, metrics.getClicks());
        assertEquals(5000L, metrics.getConversions());
        assertEquals(new BigDecimal("6.67"), metrics.getRoas());
    }
} 