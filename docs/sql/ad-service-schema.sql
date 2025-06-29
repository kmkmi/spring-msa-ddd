-- Ad Service Database Schema
USE ad_db;

-- Advertisements 테이블
CREATE TABLE IF NOT EXISTS advertisements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    video_url VARCHAR(500),
    landing_page_url VARCHAR(500),
    ad_type ENUM('BANNER', 'VIDEO', 'NATIVE', 'TEXT', 'RICH_MEDIA') NOT NULL,
    status ENUM('DRAFT', 'ACTIVE', 'PAUSED', 'COMPLETED', 'REJECTED') NOT NULL DEFAULT 'DRAFT',
    bid_amount DECIMAL(10,2) NOT NULL,
    daily_budget DECIMAL(10,2) NOT NULL,
    total_spent DECIMAL(10,2) DEFAULT 0.00,
    impressions INT DEFAULT 0,
    clicks INT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0.0000,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_campaign_id (campaign_id),
    INDEX idx_status (status),
    INDEX idx_ad_type (ad_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
);

-- 샘플 데이터 삽입
INSERT INTO advertisements (
    campaign_id, title, description, image_url, video_url, landing_page_url,
    ad_type, status, bid_amount, daily_budget, start_date, end_date
) VALUES 
(1, '여름 세일 광고', '여름 시즌 특별 할인 광고', 'https://example.com/summer-sale.jpg', 
 'https://example.com/summer-sale.mp4', 'https://example.com/summer-sale', 
 'BANNER', 'ACTIVE', 15.50, 200.00, '2025-06-29 00:00:00', '2025-07-29 23:59:59'),
 
(1, '신제품 출시 광고', '새로운 제품 소개 광고', 'https://example.com/new-product.jpg',
 'https://example.com/new-product.mp4', 'https://example.com/new-product',
 'VIDEO', 'ACTIVE', 25.00, 300.00, '2025-06-29 00:00:00', '2025-07-29 23:59:59'),
 
(2, '휴가 패키지 광고', '여름 휴가 패키지 할인', 'https://example.com/vacation.jpg',
 NULL, 'https://example.com/vacation',
 'NATIVE', 'DRAFT', 12.00, 150.00, '2025-07-01 00:00:00', '2025-08-01 23:59:59');

-- 테이블 구조 확인
DESCRIBE advertisements;

-- 샘플 데이터 조회
SELECT * FROM advertisements; 