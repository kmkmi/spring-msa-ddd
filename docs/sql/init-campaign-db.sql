-- Campaign Service Database 초기화 스크립트
CREATE DATABASE IF NOT EXISTS campaign_db;
USE campaign_db;

-- 권한 설정
GRANT ALL PRIVILEGES ON campaign_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Campaign Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/campaign-service-schema.sql; 