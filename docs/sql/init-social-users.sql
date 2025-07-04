-- 소셜 사용자 테이블 초기화
-- 이 파일은 소셜 사용자 테이블을 생성하고 초기 데이터를 삽입합니다.

-- 소셜 사용자 테이블 생성 (이미 존재하는 경우 무시)
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_social_users_email ON social_users(email);
CREATE INDEX IF NOT EXISTS idx_social_users_provider_provider_id ON social_users(provider, provider_id);
CREATE INDEX IF NOT EXISTS idx_social_users_status ON social_users(status);
CREATE INDEX IF NOT EXISTS idx_social_users_role ON social_users(role);
CREATE INDEX IF NOT EXISTS idx_social_users_email_status ON social_users(email, status);
CREATE INDEX IF NOT EXISTS idx_social_users_provider ON social_users(provider);
CREATE INDEX IF NOT EXISTS idx_social_users_last_login ON social_users(last_login_at);

-- 제약조건 추가
ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_status 
CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'));

ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_role 
CHECK (role IN ('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER'));

ALTER TABLE social_users 
ADD CONSTRAINT chk_social_users_provider 
CHECK (provider IN ('GOOGLE', 'NAVER', 'KAKAO', 'FACEBOOK', 'APPLE'));

-- 테스트용 소셜 사용자 데이터 삽입 (선택사항)
-- INSERT INTO social_users (email, name, provider, provider_id, profile_image_url, status, role) VALUES
-- ('test.social@example.com', 'Test Social User', 'GOOGLE', 'google_123456789', 'https://example.com/profile.jpg', 'ACTIVE', 'USER'); 