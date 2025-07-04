-- User Service Database Schema
USE userdb;

-- Users 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    profile_image_url VARCHAR(500),
    last_login_at TIMESTAMP NULL,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);

-- 샘플 데이터 삽입
INSERT INTO users (
    email, password, role, status
) VALUES
('admin@example.com', 'admin123', 'ADMIN', 'ACTIVE'),
('publisher1@example.com', 'publisher123', 'PUBLISHER', 'ACTIVE'),
('advertiser1@example.com', 'advertiser123', 'ADVERTISER', 'ACTIVE'),
('user1@example.com', 'user123', 'USER', 'ACTIVE');

-- 테이블 구조 확인
DESCRIBE users;

-- 샘플 데이터 조회
SELECT * FROM users; 