-- User Service Database Schema
USE userdb;

-- Users 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('USER', 'ADMIN', 'PUBLISHER', 'ADVERTISER') NOT NULL DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    profile_image_url VARCHAR(500),
    preferences JSON,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);

-- 샘플 데이터 삽입
INSERT INTO users (
    username, email, first_name, last_name, phone, role, status, preferences
) VALUES 
('admin', 'admin@example.com', '관리자', '시스템', '010-0000-0000', 'ADMIN', 'ACTIVE',
 '{"theme": "dark", "language": "ko", "notifications": true}'),
 
('publisher1', 'publisher1@example.com', '김', '퍼블리셔', '010-1111-1111', 'PUBLISHER', 'ACTIVE',
 '{"ad_categories": ["fashion", "beauty"], "payment_method": "bank_transfer"}'),
 
('advertiser1', 'advertiser1@example.com', '이', '광고주', '010-2222-2222', 'ADVERTISER', 'ACTIVE',
 '{"target_audience": ["young_adults"], "budget_limit": 10000}'),
 
('user1', 'user1@example.com', '박', '사용자', '010-3333-3333', 'USER', 'ACTIVE',
 '{"interests": ["technology", "gaming"], "privacy_settings": {"data_collection": true}}');

-- 테이블 구조 확인
DESCRIBE users;

-- 샘플 데이터 조회
SELECT * FROM users; 