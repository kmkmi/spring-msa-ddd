-- Ad Service Database 초기화 스크립트
CREATE DATABASE IF NOT EXISTS ad_db;
USE ad_db;

-- 권한 설정
GRANT ALL PRIVILEGES ON ad_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Ad Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/ad-service-schema.sql; 