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
 * 광고 클릭 스트림 실시간 처리 Flink 애플리케이션
 */
public class AdClickStreamProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(AdClickStreamProcessor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static void main(String[] args) throws Exception {
        logger.info("🚀 Ad Click Stream Processor 시작...");
        
        try {
            // 환경변수에서 설정 가져오기
            String kafkaBootstrapServers = System.getenv("FLINK_KAFKA_BOOTSTRAP_SERVERS");
            if (kafkaBootstrapServers == null) {
                kafkaBootstrapServers = "localhost:9092"; // 기본값
            }
            
            String kafkaTopic = System.getenv("FLINK_KAFKA_TOPIC_AD_CLICKS");
            if (kafkaTopic == null) {
                kafkaTopic = "ad-clicks"; // 기본값
            }
            
            String kafkaGroupId = System.getenv("FLINK_KAFKA_CLICK_GROUP_ID");
            if (kafkaGroupId == null) {
                kafkaGroupId = "flink-click-processor"; // 기본값
            }
            
            logger.info("📊 설정 로드: Kafka={}, Topic={}, GroupId={}", 
                    kafkaBootstrapServers, kafkaTopic, kafkaGroupId);
            
            // Flink 실행 환경 설정
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            
            // 체크포인트 설정 추가
            env.enableCheckpointing(60000); // 60초마다 체크포인트
            env.getCheckpointConfig().setCheckpointTimeout(30000);
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(30000);
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
            
            // Kafka 소스 설정 (환경변수에서 받아온 설정 사용)
            KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                    .setBootstrapServers(kafkaBootstrapServers)
                    .setTopics(kafkaTopic)
                    .setGroupId(kafkaGroupId)
                    .setStartingOffsets(OffsetsInitializer.latest())
                    .setValueOnlyDeserializer(new SimpleStringSchema())
                    .build();
            
            // 스트림 처리 파이프라인 구성
            DataStream<String> clickStream = env.fromSource(kafkaSource, 
                    org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(), 
                    "Ad Click Stream");
            
            // 처리 로직 추가
            clickStream
                .map(new MapFunction<String, String>() {
                    @Override
                    public String map(String value) throws Exception {
                        logger.info("📊 처리된 클릭 이벤트: {}", value);
                        return value;
                    }
                })
                .name("Click Event Logger");
            
            // 실행
            logger.info("🚀 Flink 작업 실행 시작...");
            env.execute("Ad Click Stream Processing");
            
        } catch (Exception e) {
            logger.error("❌ Flink 애플리케이션 실행 중 오류 발생", e);
            throw e;
        }
    }
    
    /**
     * 광고 클릭 이벤트 데이터 클래스
     */
    public static class AdClickEvent {
        private String clickId;
        private String adId;
        private String campaignId;
        private String userId;
        private String impressionId;
        private String timestamp;
        private String userAgent;
        private String ipAddress;
        private String referrer;
        
        // Getters and Setters
        public String getClickId() { return clickId; }
        public void setClickId(String clickId) { this.clickId = clickId; }
        
        public String getAdId() { return adId; }
        public void setAdId(String adId) { this.adId = adId; }
        
        public String getCampaignId() { return campaignId; }
        public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getImpressionId() { return impressionId; }
        public void setImpressionId(String impressionId) { this.impressionId = impressionId; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
    }
    
    /**
     * 클릭 이벤트 파서
     */
    public static class ClickEventParser implements MapFunction<String, AdClickEvent> {
        @Override
        public AdClickEvent map(String value) throws Exception {
            try {
                return objectMapper.readValue(value, AdClickEvent.class);
            } catch (Exception e) {
                logger.warn("클릭 이벤트 파싱 실패: {}", value, e);
                return null;
            }
        }
    }
    
    /**
     * 사용자 클릭 집계기
     */
    public static class UserClickAggregator implements AggregateFunction<
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
            Long totalClicks = accumulator.values().stream().mapToLong(Long::longValue).sum();
            return new Tuple3<>(userId, adId, totalClicks);
        }
        
        @Override
        public Map<String, Long> merge(Map<String, Long> a, Map<String, Long> b) {
            a.putAll(b);
            return a;
        }
    }
    
    /**
     * CTR 계산 결과
     */
    public static class CTRResult {
        private String adId;
        private long clicks;
        private long impressions;
        private double ctr;
        private String timestamp;
        
        public CTRResult(String adId, long clicks, long impressions, double ctr) {
            this.adId = adId;
            this.clicks = clicks;
            this.impressions = impressions;
            this.ctr = ctr;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        
        // Getters
        public String getAdId() { return adId; }
        public long getClicks() { return clicks; }
        public long getImpressions() { return impressions; }
        public double getCtr() { return ctr; }
        public String getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("CTRResult{adId='%s', clicks=%d, impressions=%d, ctr=%.4f, timestamp='%s'}", 
                    adId, clicks, impressions, ctr, timestamp);
        }
    }
    
    /**
     * CTR 계산기
     */
    public static class CTRCalculator implements AggregateFunction<
            Tuple2<String, Tuple2<Long, Long>>, Tuple2<Long, Long>, CTRResult> {
        
        @Override
        public Tuple2<Long, Long> createAccumulator() {
            return new Tuple2<>(0L, 0L);
        }
        
        @Override
        public Tuple2<Long, Long> add(Tuple2<String, Tuple2<Long, Long>> value, Tuple2<Long, Long> accumulator) {
            accumulator.f0 += value.f1.f0; // clicks
            accumulator.f1 += value.f1.f1; // impressions
            return accumulator;
        }
        
        @Override
        public CTRResult getResult(Tuple2<Long, Long> accumulator) {
            double ctr = accumulator.f1 > 0 ? (double) accumulator.f0 / accumulator.f1 : 0.0;
            return new CTRResult("ad-" + System.currentTimeMillis(), accumulator.f0, accumulator.f1, ctr);
        }
        
        @Override
        public Tuple2<Long, Long> merge(Tuple2<Long, Long> a, Tuple2<Long, Long> b) {
            return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
        }
    }
} 