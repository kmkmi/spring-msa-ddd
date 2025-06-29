-- MSA 데이터베이스 초기화 스크립트

-- Campaign Service 데이터베이스
CREATE DATABASE IF NOT EXISTS campaign_db;
USE campaign_db;

-- User Service 데이터베이스
CREATE DATABASE IF NOT EXISTS userdb;
USE userdb;

-- Ad Service 데이터베이스
CREATE DATABASE IF NOT EXISTS ad_db;
USE ad_db;

-- Publisher Service 데이터베이스
CREATE DATABASE IF NOT EXISTS publisher_db;
USE publisher_db;

-- 권한 설정 (root 사용자 사용)
GRANT ALL PRIVILEGES ON campaign_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON userdb.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON ad_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON publisher_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

-- Campaign Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/campaign-service-schema.sql;

-- User Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/user-service-schema.sql;

-- Ad Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/ad-service-schema.sql;

-- Publisher Service 스키마 적용
SOURCE /docker-entrypoint-initdb.d/sql/publisher-service-schema.sql; 