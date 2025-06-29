-- Publisher Service Database Schema
USE publisher_db;

-- Publishers 테이블
CREATE TABLE IF NOT EXISTS publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    website_url VARCHAR(500),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    address TEXT,
    business_type ENUM('NEWSPAPER', 'MAGAZINE', 'WEBSITE', 'MOBILE_APP', 'SOCIAL_MEDIA', 'OTHER') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING') NOT NULL DEFAULT 'PENDING',
    monthly_traffic BIGINT DEFAULT 0,
    revenue_share DECIMAL(5,2) DEFAULT 0.00,
    payment_info JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_business_type (business_type),
    INDEX idx_status (status),
    INDEX idx_monthly_traffic (monthly_traffic)
);

-- 샘플 데이터 삽입
INSERT INTO publishers (
    name, description, website_url, contact_email, contact_phone, 
    business_type, status, monthly_traffic, revenue_share, payment_info
) VALUES 
('테크뉴스', '최신 기술 뉴스와 리뷰를 제공하는 웹사이트', 'https://technews.com',
 'contact@technews.com', '02-1234-5678', 'WEBSITE', 'ACTIVE', 500000, 70.00,
 '{"bank_account": "123-456-789", "account_holder": "테크뉴스"}'),
 
('패션매거진', '패션과 뷰티 트렌드를 다루는 온라인 매거진', 'https://fashionmag.com',
 'info@fashionmag.com', '02-2345-6789', 'MAGAZINE', 'ACTIVE', 300000, 65.00,
 '{"bank_account": "234-567-890", "account_holder": "패션매거진"}'),
 
('모바일게임', '인기 모바일 게임 앱', 'https://mobilegame.com',
 'support@mobilegame.com', '02-3456-7890', 'MOBILE_APP', 'ACTIVE', 1000000, 80.00,
 '{"bank_account": "345-678-901", "account_holder": "모바일게임"}'),
 
('소셜미디어', '소셜 미디어 플랫폼', 'https://socialmedia.com',
 'hello@socialmedia.com', '02-4567-8901', 'SOCIAL_MEDIA', 'PENDING', 2000000, 75.00,
 '{"bank_account": "456-789-012", "account_holder": "소셜미디어"}');

-- 테이블 구조 확인
DESCRIBE publishers;

-- 샘플 데이터 조회
SELECT * FROM publishers; 