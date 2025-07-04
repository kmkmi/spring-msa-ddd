-- User Service Database 초기화 스크립트
CREATE DATABASE IF NOT EXISTS userdb;
USE userdb;

-- Users 테이블 생성 (user-service-schema.sql과 동일하게 수정, password 컬럼 추가)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);

-- 샘플 데이터 삽입 (password 컬럼 추가)
INSERT INTO users (
    email, password, role, status
) VALUES
('admin@example.com', 'admin123', 'ADMIN', 'ACTIVE'),
('publisher1@example.com', 'publisher123', 'PUBLISHER', 'ACTIVE'),
('advertiser1@example.com', 'advertiser123', 'ADVERTISER', 'ACTIVE'),
('user1@example.com', 'user123', 'USER', 'ACTIVE');

-- 권한 설정
GRANT ALL PRIVILEGES ON userdb.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- User Service 스키마 적용 (테이블 구조가 이미 있으므로, 필요시 DROP TABLE IF EXISTS users; 추가 가능)
-- SOURCE /docker-entrypoint-initdb.d/sql/user-service-schema.sql; 