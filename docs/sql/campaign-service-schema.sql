-- Campaign Service Database Schema
USE campaign_db;

-- Campaigns 테이블
CREATE TABLE IF NOT EXISTS campaigns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    publisher_id BIGINT NOT NULL,
    budget DECIMAL(12,2) NOT NULL,
    spent_amount DECIMAL(12,2) DEFAULT 0.00,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    status ENUM('DRAFT', 'ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    target_audience JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_publisher_id (publisher_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date)
);

-- 샘플 데이터 삽입
INSERT INTO campaigns (
    name, description, publisher_id, budget, start_date, end_date, status, target_audience
) VALUES 
('여름 세일 캠페인', '여름 시즌 특별 할인 캠페인', 1, 5000.00, 
 '2025-06-29 00:00:00', '2025-07-29 23:59:59', 'ACTIVE',
 '{"age_range": "20-40", "interests": ["shopping", "fashion"], "location": "Korea"}'),
 
('신제품 출시 캠페인', '새로운 제품 소개 캠페인', 1, 8000.00,
 '2025-06-29 00:00:00', '2025-07-29 23:59:59', 'ACTIVE',
 '{"age_range": "25-45", "interests": ["technology", "innovation"], "location": "Korea"}'),
 
('휴가 패키지 캠페인', '여름 휴가 패키지 할인 캠페인', 2, 3000.00,
 '2025-07-01 00:00:00', '2025-08-01 23:59:59', 'DRAFT',
 '{"age_range": "30-50", "interests": ["travel", "vacation"], "location": "Korea"}');

-- 테이블 구조 확인
DESCRIBE campaigns;

-- 샘플 데이터 조회
SELECT * FROM campaigns; 