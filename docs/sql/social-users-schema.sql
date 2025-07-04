-- 소셜 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS social_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    profile_image_url TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_provider_provider_id (provider, provider_id),
    INDEX idx_status (status),
    INDEX idx_role (role)
);

-- 소셜 사용자 테이블에 대한 인덱스 추가
CREATE INDEX IF NOT EXISTS idx_social_users_email_status ON social_users(email, status);
CREATE INDEX IF NOT EXISTS idx_social_users_provider ON social_users(provider);
CREATE INDEX IF NOT EXISTS idx_social_users_last_login ON social_users(last_login_at);

-- 소셜 사용자 테이블에 대한 제약조건 추가
ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_status 
CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'));

ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_role 
CHECK (role IN ('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER'));

ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_provider 
CHECK (provider IN ('GOOGLE', 'NAVER', 'KAKAO', 'FACEBOOK', 'APPLE')); 