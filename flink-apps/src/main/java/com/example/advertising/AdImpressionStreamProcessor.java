package com.example.advertising;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 광고 노출 스트림 실시간 처리 Flink 애플리케이션
 */
public class AdImpressionStreamProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(AdImpressionStreamProcessor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) throws Exception {
        logger.info("🚀 Ad Impression Stream Processor 시작...");
        
        // 환경변수에서 설정 가져오기
        String kafkaBootstrapServers = System.getenv("FLINK_KAFKA_BOOTSTRAP_SERVERS");
        if (kafkaBootstrapServers == null) {
            kafkaBootstrapServers = "localhost:9092"; // 기본값
        }
        
        String kafkaTopic = System.getenv("FLINK_KAFKA_TOPIC_AD_IMPRESSIONS");
        if (kafkaTopic == null) {
            kafkaTopic = "ad-impressions"; // 기본값
        }
        
        String kafkaGroupId = System.getenv("FLINK_KAFKA_IMPRESSION_GROUP_ID");
        if (kafkaGroupId == null) {
            kafkaGroupId = "flink-impression-processor"; // 기본값
        }
        
        logger.info("📊 설정 로드: Kafka={}, Topic={}, GroupId={}", 
                kafkaBootstrapServers, kafkaTopic, kafkaGroupId);
        
        // Flink 실행 환경 설정
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // Kafka 소스 설정 (환경변수에서 받아온 설정 사용)
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaBootstrapServers)
                .setTopics(kafkaTopic)
                .setGroupId(kafkaGroupId)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        
        // 스트림 처리 파이프라인 구성
        DataStream<String> impressionStream = env.fromSource(kafkaSource, 
                org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(), 
                "Ad Impression Stream");
        
        // 1. 노출 이벤트 파싱 및 필터링
        DataStream<AdImpressionEvent> parsedImpressions = impressionStream
                .map(new ImpressionEventParser())
                .filter(event -> event != null);
        
        // 2. 실시간 노출 카운트 (광고별) - 환경변수에서 윈도우 크기 가져오기
        String impressionWindowSeconds = System.getenv("FLINK_IMPRESSION_WINDOW_SECONDS");
        int windowSeconds = impressionWindowSeconds != null ? Integer.parseInt(impressionWindowSeconds) : 10;
        
        DataStream<Tuple2<String, Long>> adImpressionCounts = parsedImpressions
                .map(event -> new Tuple2<>(event.getAdId(), 1L))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(windowSeconds)))
                .sum(1);
        
        // 3. 실시간 노출 카운트 (캠페인별)
        DataStream<Tuple2<String, Long>> campaignImpressionCounts = parsedImpressions
                .map(event -> new Tuple2<>(event.getCampaignId(), 1L))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(windowSeconds)))
                .sum(1);
        
        // 4. 사용자별 노출 패턴 분석 - 환경변수에서 윈도우 크기 가져오기
        String userPatternWindowMinutes = System.getenv("FLINK_USER_PATTERN_WINDOW_MINUTES");
        int userWindowMinutes = userPatternWindowMinutes != null ? Integer.parseInt(userPatternWindowMinutes) : 5;
        
        DataStream<Tuple3<String, String, Long>> userImpressionPatterns = parsedImpressions
                .map(event -> new Tuple3<>(event.getUserId(), event.getAdId(), 1L))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.minutes(userWindowMinutes)))
                .aggregate(new UserImpressionAggregator());
        
        // 5. 실시간 CPM (Cost Per Mille) 계산 - 환경변수에서 윈도우 크기 가져오기
        String cpmWindowMinutes = System.getenv("FLINK_CPM_WINDOW_MINUTES");
        int cpmWindowMin = cpmWindowMinutes != null ? Integer.parseInt(cpmWindowMinutes) : 1;
        
        DataStream<CPMResult> cpmResults = parsedImpressions
                .map(event -> new Tuple2<>(event.getAdId(), new Tuple2<>(1L, event.getBidAmount())))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.minutes(cpmWindowMin)))
                .aggregate(new CPMCalculator());
        
        // 6. 지리적 분포 분석 - 환경변수에서 윈도우 크기 가져오기
        String geographicWindowMinutes = System.getenv("FLINK_GEOGRAPHIC_WINDOW_MINUTES");
        int geoWindowMin = geographicWindowMinutes != null ? Integer.parseInt(geographicWindowMinutes) : 5;
        
        DataStream<Tuple2<String, Long>> geographicDistribution = parsedImpressions
                .map(event -> new Tuple2<>(event.getGeoLocation(), 1L))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.minutes(geoWindowMin)))
                .sum(1);
        
        // 결과 출력
        adImpressionCounts.print("📊 광고별 노출 수");
        campaignImpressionCounts.print("📈 캠페인별 노출 수");
        userImpressionPatterns.print("👤 사용자별 노출 패턴");
        cpmResults.print("💰 실시간 CPM");
        geographicDistribution.print("🌍 지리적 분포");
        
        logger.info("✅ Ad Impression Stream Processor 파이프라인 구성 완료");
        env.execute("Ad Impression Stream Processor");
    }
    
    /**
     * 광고 노출 이벤트 데이터 클래스
     */
    public static class AdImpressionEvent {
        private String impressionId;
        private String adId;
        private String campaignId;
        private String userId;
        private String publisherId;
        private String timestamp;
        private String userAgent;
        private String ipAddress;
        private String geoLocation;
        private Double bidAmount;
        private String deviceType;
        private String browserType;
        private String osType;
        
        // Getters and Setters
        public String getImpressionId() { return impressionId; }
        public void setImpressionId(String impressionId) { this.impressionId = impressionId; }
        
        public String getAdId() { return adId; }
        public void setAdId(String adId) { this.adId = adId; }
        
        public String getCampaignId() { return campaignId; }
        public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getPublisherId() { return publisherId; }
        public void setPublisherId(String publisherId) { this.publisherId = publisherId; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getGeoLocation() { return geoLocation; }
        public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }
        
        public Double getBidAmount() { return bidAmount; }
        public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }
        
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        
        public String getBrowserType() { return browserType; }
        public void setBrowserType(String browserType) { this.browserType = browserType; }
        
        public String getOsType() { return osType; }
        public void setOsType(String osType) { this.osType = osType; }
    }
    
    /**
     * 노출 이벤트 파서
     */
    public static class ImpressionEventParser implements MapFunction<String, AdImpressionEvent> {
        @Override
        public AdImpressionEvent map(String value) throws Exception {
            try {
                return objectMapper.readValue(value, AdImpressionEvent.class);
            } catch (Exception e) {
                logger.warn("노출 이벤트 파싱 실패: {}", value, e);
                return null;
            }
        }
    }
    
    /**
     * 사용자 노출 집계기
     */
    public static class UserImpressionAggregator implements AggregateFunction<
            Tuple3<String, String, Long>, Map<String, Long>, Tuple3<String, String, Long>> {
        
        @Override
        public Map<String, Long> createAccumulator() {
            return new HashMap<>();
        }
        
        @Override
        public Map<String, Long> add(Tuple3<String, String, Long> value, Map<String, Long> accumulator) {
            accumulator.merge(value.f1, value.f2, Long::sum);
            return accumulator;
        }
        
        @Override
        public Tuple3<String, String, Long> getResult(Map<String, Long> accumulator) {
            String userId = accumulator.keySet().iterator().next();
            String adId = accumulator.keySet().iterator().next();
            Long totalImpressions = accumulator.values().stream().mapToLong(Long::longValue).sum();
            return new Tuple3<>(userId, adId, totalImpressions);
        }
        
        @Override
        public Map<String, Long> merge(Map<String, Long> a, Map<String, Long> b) {
            a.putAll(b);
            return a;
        }
    }
    
    /**
     * CPM 계산 결과
     */
    public static class CPMResult {
        private String adId;
        private long impressions;
        private double totalCost;
        private double cpm;
        private String timestamp;
        
        public CPMResult(String adId, long impressions, double totalCost, double cpm) {
            this.adId = adId;
            this.impressions = impressions;
            this.totalCost = totalCost;
            this.cpm = cpm;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        // Getters
        public String getAdId() { return adId; }
        public long getImpressions() { return impressions; }
        public double getTotalCost() { return totalCost; }
        public double getCpm() { return cpm; }
        public String getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("CPMResult{adId='%s', impressions=%d, totalCost=%.2f, cpm=%.2f, timestamp='%s'}", 
                    adId, impressions, totalCost, cpm, timestamp);
        }
    }
    
    /**
     * CPM 계산기
     */
    public static class CPMCalculator implements AggregateFunction<
            Tuple2<String, Tuple2<Long, Double>>, Tuple2<Long, Double>, CPMResult> {
        
        @Override
        public Tuple2<Long, Double> createAccumulator() {
            return new Tuple2<>(0L, 0.0);
        }
        
        @Override
        public Tuple2<Long, Double> add(Tuple2<String, Tuple2<Long, Double>> value, Tuple2<Long, Double> accumulator) {
            accumulator.f0 += value.f1.f0; // impressions
            accumulator.f1 += value.f1.f1; // total cost
            return accumulator;
        }
        
        @Override
        public CPMResult getResult(Tuple2<Long, Double> accumulator) {
            double cpm = accumulator.f0 > 0 ? (accumulator.f1 / accumulator.f0) * 1000.0 : 0.0;
            return new CPMResult("ad-" + System.currentTimeMillis(), accumulator.f0, accumulator.f1, cpm);
        }
        
        @Override
        public Tuple2<Long, Double> merge(Tuple2<Long, Double> a, Tuple2<Long, Double> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }
} 