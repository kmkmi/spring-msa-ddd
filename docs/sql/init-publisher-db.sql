-- Publisher Service Database 초기화 스크립트
CREATE DATABASE IF NOT EXISTS publisher_db;
USE publisher_db;

-- 권한 설정
GRANT ALL PRIVILEGES ON publisher_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Publisher Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/publisher-service-schema.sql; 