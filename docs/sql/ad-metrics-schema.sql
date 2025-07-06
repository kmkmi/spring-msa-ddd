-- 광고 메트릭 데이터베이스 스키마

-- 광고 메트릭 일별 집계 테이블
CREATE TABLE IF NOT EXISTS ad_metrics_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    advertisement_id VARCHAR(100) NOT NULL,
    campaign_id VARCHAR(100) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    cvr DECIMAL(5,4) DEFAULT 0.0000,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    cpa DECIMAL(10,2) DEFAULT 0.00,
    total_revenue DECIMAL(10,2) DEFAULT 0.00,
    roas DECIMAL(5,2) DEFAULT 0.00,
    profit DECIMAL(10,2) DEFAULT 0.00,
    profit_margin DECIMAL(5,2) DEFAULT 0.00,
    unique_users BIGINT DEFAULT 0,
    sessions BIGINT DEFAULT 0,
    avg_session_duration DECIMAL(8,2) DEFAULT 0.00,
    bounce_rate DECIMAL(5,2) DEFAULT 0.00,
    engagement_rate DECIMAL(5,2) DEFAULT 0.00,
    viewability_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (date),
    INDEX idx_advertisement_id (advertisement_id),
    INDEX idx_campaign_id (campaign_id),
    UNIQUE KEY uk_daily_metrics (date, advertisement_id)
);

-- 광고 메트릭 시간별 집계 테이블
CREATE TABLE IF NOT EXISTS ad_metrics_hourly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    hour TINYINT NOT NULL,
    advertisement_id VARCHAR(100) NOT NULL,
    campaign_id VARCHAR(100) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    unique_users BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date_hour (date, hour),
    INDEX idx_advertisement_id (advertisement_id),
    INDEX idx_campaign_id (campaign_id),
    UNIQUE KEY uk_hourly_metrics (date, hour, advertisement_id)
);

-- 캠페인 성과 분석 테이블
CREATE TABLE IF NOT EXISTS campaign_performance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    campaign_id VARCHAR(100) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    cvr DECIMAL(5,4) DEFAULT 0.0000,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    cpa DECIMAL(10,2) DEFAULT 0.00,
    total_revenue DECIMAL(10,2) DEFAULT 0.00,
    roas DECIMAL(5,2) DEFAULT 0.00,
    unique_users BIGINT DEFAULT 0,
    avg_session_duration DECIMAL(8,2) DEFAULT 0.00,
    bounce_rate DECIMAL(5,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (date),
    INDEX idx_campaign_id (campaign_id),
    UNIQUE KEY uk_campaign_daily (date, campaign_id)
);

-- 디바이스별 분석 테이블
CREATE TABLE IF NOT EXISTS device_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    unique_users BIGINT DEFAULT 0,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (date),
    INDEX idx_device_type (device_type),
    UNIQUE KEY uk_device_daily (date, device_type)
);

-- 지역별 분석 테이블
CREATE TABLE IF NOT EXISTS location_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    location VARCHAR(100) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    unique_users BIGINT DEFAULT 0,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (date),
    INDEX idx_location (location),
    UNIQUE KEY uk_location_daily (date, location)
);

-- 크리에이티브별 분석 테이블
CREATE TABLE IF NOT EXISTS creative_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    creative_type VARCHAR(50) NOT NULL,
    impressions BIGINT DEFAULT 0,
    clicks BIGINT DEFAULT 0,
    conversions BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    creative_impressions BIGINT DEFAULT 0,
    creative_ctr DECIMAL(5,4) DEFAULT 0.0000,
    total_spend DECIMAL(10,2) DEFAULT 0.00,
    cpc DECIMAL(10,2) DEFAULT 0.00,
    cpm DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_date (date),
    INDEX idx_creative_type (creative_type),
    UNIQUE KEY uk_creative_daily (date, creative_type)
);

-- 배치 작업 이력 테이블
CREATE TABLE IF NOT EXISTS batch_job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id VARCHAR(100) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    records_processed BIGINT DEFAULT 0,
    records_failed BIGINT DEFAULT 0,
    error_message TEXT,
    execution_time_ms BIGINT DEFAULT 0,
    output_location VARCHAR(500),
    data_source VARCHAR(100),
    data_target VARCHAR(100),
    memory_used_mb BIGINT DEFAULT 0,
    cpu_usage_percent BIGINT DEFAULT 0,
    network_io_mb BIGINT DEFAULT 0,
    disk_io_mb BIGINT DEFAULT 0,
    duplicate_records BIGINT DEFAULT 0,
    null_records BIGINT DEFAULT 0,
    invalid_records BIGINT DEFAULT 0,
    data_quality_score DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_job_id (job_id),
    INDEX idx_job_type (job_type),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    UNIQUE KEY uk_job_id (job_id)
);

-- 이상 탐지 결과 테이블
CREATE TABLE IF NOT EXISTS anomaly_detection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    advertisement_id VARCHAR(100) NOT NULL,
    anomaly_type VARCHAR(50) NOT NULL,
    metric_name VARCHAR(50) NOT NULL,
    current_value DECIMAL(10,4) NOT NULL,
    expected_value DECIMAL(10,4) NOT NULL,
    deviation_percentage DECIMAL(5,2) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    description TEXT,
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'DETECTED',
    
    INDEX idx_advertisement_id (advertisement_id),
    INDEX idx_anomaly_type (anomaly_type),
    INDEX idx_severity (severity),
    INDEX idx_detected_at (detected_at),
    INDEX idx_status (status)
);

-- 실시간 메트릭 캐시 테이블 (Redis 대체용)
CREATE TABLE IF NOT EXISTS realtime_metrics_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cache_key VARCHAR(200) NOT NULL,
    cache_value TEXT NOT NULL,
    cache_type VARCHAR(50) NOT NULL,
    ttl_seconds INT DEFAULT 3600,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_cache_key (cache_key),
    INDEX idx_cache_type (cache_type),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_cache_key (cache_key)
);

-- 뷰 생성: 일별 광고 성과 요약
CREATE OR REPLACE VIEW daily_ad_performance_summary AS
SELECT 
    date,
    COUNT(DISTINCT advertisement_id) as active_ads,
    COUNT(DISTINCT campaign_id) as active_campaigns,
    SUM(impressions) as total_impressions,
    SUM(clicks) as total_clicks,
    SUM(conversions) as total_conversions,
    AVG(ctr) as avg_ctr,
    AVG(cvr) as avg_cvr,
    SUM(total_spend) as total_spend,
    SUM(total_revenue) as total_revenue,
    AVG(roas) as avg_roas,
    SUM(unique_users) as total_unique_users
FROM ad_metrics_daily
GROUP BY date
ORDER BY date DESC;

-- 뷰 생성: 캠페인 성과 랭킹
CREATE OR REPLACE VIEW campaign_performance_ranking AS
SELECT 
    campaign_id,
    date,
    impressions,
    clicks,
    conversions,
    ctr,
    cvr,
    total_spend,
    total_revenue,
    roas,
    RANK() OVER (PARTITION BY date ORDER BY roas DESC) as roas_rank,
    RANK() OVER (PARTITION BY date ORDER BY ctr DESC) as ctr_rank,
    RANK() OVER (PARTITION BY date ORDER BY impressions DESC) as impression_rank
FROM campaign_performance
ORDER BY date DESC, roas DESC; 