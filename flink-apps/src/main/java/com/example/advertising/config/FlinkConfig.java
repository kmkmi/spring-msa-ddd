package com.example.advertising.config;

/**
 * Flink 애플리케이션 설정 클래스
 * 환경변수를 통해 설정을 관리합니다.
 */
public class FlinkConfig {
    
    private Kafka kafka = new Kafka();
    private Processing processing = new Processing();
    private Monitoring monitoring = new Monitoring();
    
    public static class Kafka {
        private String bootstrapServers = System.getenv("FLINK_KAFKA_BOOTSTRAP_SERVERS");
        private String clickTopic = System.getenv("FLINK_KAFKA_TOPIC_AD_CLICKS");
        private String impressionTopic = System.getenv("FLINK_KAFKA_TOPIC_AD_IMPRESSIONS");
        private String clickGroupId = System.getenv("FLINK_KAFKA_CLICK_GROUP_ID");
        private String impressionGroupId = System.getenv("FLINK_KAFKA_IMPRESSION_GROUP_ID");
        
        public Kafka() {
            // 기본값 설정
            if (bootstrapServers == null) bootstrapServers = "localhost:9092";
            if (clickTopic == null) clickTopic = "ad-clicks";
            if (impressionTopic == null) impressionTopic = "ad-impressions";
            if (clickGroupId == null) clickGroupId = "flink-click-processor";
            if (impressionGroupId == null) impressionGroupId = "flink-impression-processor";
        }
        
        // Getters and Setters
        public String getBootstrapServers() { return bootstrapServers; }
        public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }
        
        public String getClickTopic() { return clickTopic; }
        public void setClickTopic(String clickTopic) { this.clickTopic = clickTopic; }
        
        public String getImpressionTopic() { return impressionTopic; }
        public void setImpressionTopic(String impressionTopic) { this.impressionTopic = impressionTopic; }
        
        public String getClickGroupId() { return clickGroupId; }
        public void setClickGroupId(String clickGroupId) { this.clickGroupId = clickGroupId; }
        
        public String getImpressionGroupId() { return impressionGroupId; }
        public void setImpressionGroupId(String impressionGroupId) { this.impressionGroupId = impressionGroupId; }
    }
    
    public static class Processing {
        private int clickWindowSeconds;
        private int impressionWindowSeconds;
        private int userPatternWindowMinutes;
        private int ctrWindowMinutes;
        private int cpmWindowMinutes;
        private int geographicWindowMinutes;
        
        public Processing() {
            // 환경변수에서 값을 가져오거나 기본값 사용
            String clickWindow = System.getenv("FLINK_CLICK_WINDOW_SECONDS");
            clickWindowSeconds = clickWindow != null ? Integer.parseInt(clickWindow) : 10;
            
            String impressionWindow = System.getenv("FLINK_IMPRESSION_WINDOW_SECONDS");
            impressionWindowSeconds = impressionWindow != null ? Integer.parseInt(impressionWindow) : 10;
            
            String userPatternWindow = System.getenv("FLINK_USER_PATTERN_WINDOW_MINUTES");
            userPatternWindowMinutes = userPatternWindow != null ? Integer.parseInt(userPatternWindow) : 5;
            
            String ctrWindow = System.getenv("FLINK_CTR_WINDOW_MINUTES");
            ctrWindowMinutes = ctrWindow != null ? Integer.parseInt(ctrWindow) : 1;
            
            String cpmWindow = System.getenv("FLINK_CPM_WINDOW_MINUTES");
            cpmWindowMinutes = cpmWindow != null ? Integer.parseInt(cpmWindow) : 1;
            
            String geographicWindow = System.getenv("FLINK_GEOGRAPHIC_WINDOW_MINUTES");
            geographicWindowMinutes = geographicWindow != null ? Integer.parseInt(geographicWindow) : 5;
        }
        
        // Getters and Setters
        public int getClickWindowSeconds() { return clickWindowSeconds; }
        public void setClickWindowSeconds(int clickWindowSeconds) { this.clickWindowSeconds = clickWindowSeconds; }
        
        public int getImpressionWindowSeconds() { return impressionWindowSeconds; }
        public void setImpressionWindowSeconds(int impressionWindowSeconds) { this.impressionWindowSeconds = impressionWindowSeconds; }
        
        public int getUserPatternWindowMinutes() { return userPatternWindowMinutes; }
        public void setUserPatternWindowMinutes(int userPatternWindowMinutes) { this.userPatternWindowMinutes = userPatternWindowMinutes; }
        
        public int getCtrWindowMinutes() { return ctrWindowMinutes; }
        public void setCtrWindowMinutes(int ctrWindowMinutes) { this.ctrWindowMinutes = ctrWindowMinutes; }
        
        public int getCpmWindowMinutes() { return cpmWindowMinutes; }
        public void setCpmWindowMinutes(int cpmWindowMinutes) { this.cpmWindowMinutes = cpmWindowMinutes; }
        
        public int getGeographicWindowMinutes() { return geographicWindowMinutes; }
        public void setGeographicWindowMinutes(int geographicWindowMinutes) { this.geographicWindowMinutes = geographicWindowMinutes; }
    }
    
    public static class Monitoring {
        private boolean enableMetrics;
        private String metricsPrefix;
        private int metricsIntervalSeconds;
        
        public Monitoring() {
            // 환경변수에서 값을 가져오거나 기본값 사용
            String enableMetricsStr = System.getenv("FLINK_ENABLE_METRICS");
            enableMetrics = enableMetricsStr != null ? Boolean.parseBoolean(enableMetricsStr) : true;
            
            metricsPrefix = System.getenv("FLINK_METRICS_PREFIX");
            if (metricsPrefix == null) metricsPrefix = "flink.advertising";
            
            String metricsInterval = System.getenv("FLINK_METRICS_INTERVAL_SECONDS");
            metricsIntervalSeconds = metricsInterval != null ? Integer.parseInt(metricsInterval) : 30;
        }
        
        // Getters and Setters
        public boolean isEnableMetrics() { return enableMetrics; }
        public void setEnableMetrics(boolean enableMetrics) { this.enableMetrics = enableMetrics; }
        
        public String getMetricsPrefix() { return metricsPrefix; }
        public void setMetricsPrefix(String metricsPrefix) { this.metricsPrefix = metricsPrefix; }
        
        public int getMetricsIntervalSeconds() { return metricsIntervalSeconds; }
        public void setMetricsIntervalSeconds(int metricsIntervalSeconds) { this.metricsIntervalSeconds = metricsIntervalSeconds; }
    }
    
    // Getters and Setters
    public Kafka getKafka() { return kafka; }
    public void setKafka(Kafka kafka) { this.kafka = kafka; }
    
    public Processing getProcessing() { return processing; }
    public void setProcessing(Processing processing) { this.processing = processing; }
    
    public Monitoring getMonitoring() { return monitoring; }
    public void setMonitoring(Monitoring monitoring) { this.monitoring = monitoring; }
} 