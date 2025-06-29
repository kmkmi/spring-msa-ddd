-- User Service Database 초기화 스크립트
CREATE DATABASE IF NOT EXISTS userdb;
USE userdb;

-- 권한 설정
GRANT ALL PRIVILEGES ON userdb.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- User Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/user-service-schema.sql; 