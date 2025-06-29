# Cursor 데이터베이스 연동 가이드

## 현재 프로젝트 MySQL 데이터베이스 연결 정보

### 1. Ad Service Database
- **Host**: localhost
- **Port**: 3307
- **Database**: ad_db
- **Username**: root
- **Password**: root
- **Connection String**: `mysql://root:root@localhost:3307/ad_db`

### 2. Campaign Service Database
- **Host**: localhost
- **Port**: 3308
- **Database**: campaign_db
- **Username**: root
- **Password**: root
- **Connection String**: `mysql://root:root@localhost:3308/campaign_db`

### 3. User Service Database
- **Host**: localhost
- **Port**: 3310
- **Database**: userdb
- **Username**: root
- **Password**: root
- **Connection String**: `mysql://root:root@localhost:3310/userdb`

### 4. Publisher Service Database
- **Host**: localhost
- **Port**: 3309
- **Database**: publisher_db
- **Username**: root
- **Password**: root
- **Connection String**: `mysql://root:root@localhost:3309/publisher_db`

## Cursor에서 데이터베이스 연동 방법

### 1. Database Client 확장 설치
1. **Ctrl+Shift+X** (확장 마켓플레이스)
2. 검색: `Database Client`
3. **Database Client** (by cweijan) 설치

### 2. 데이터베이스 연결 설정
1. **Ctrl+Shift+P** (명령 팔레트)
2. `Database: Add Connection` 입력
3. MySQL 선택
4. 연결 정보 입력:
   ```
   Host: localhost
   Port: 3307 (또는 원하는 DB 포트)
   Username: root
   Password: root
   Database: ad_db (또는 원하는 DB명)
   ```

### 3. SQL 파일 생성 및 실행
1. 프로젝트에 `sql/` 폴더 생성
2. SQL 파일 생성 (예: `ad-service-schema.sql`)
3. 쿼리 작성 후 **Ctrl+Shift+E** (실행)

## 추천 확장 프로그램

### Database Client (cweijan)
- **설치**: `cweijan.vscode-mysql-client2`
- **기능**: 
  - MySQL, PostgreSQL, SQLite 지원
  - 쿼리 실행 및 결과 확인
  - 테이블 구조 확인
  - 데이터 내보내기/가져오기

### SQLTools (Matheus Teixeira)
- **설치**: `mtxr.sqltools`
- **기능**:
  - 다양한 데이터베이스 지원
  - 쿼리 히스토리
  - 스니펫 지원

## 데이터베이스 스키마 확인

### Ad Service 테이블
```sql
USE ad_db;
SHOW TABLES;
DESCRIBE advertisements;
```

### Campaign Service 테이블
```sql
USE campaign_db;
SHOW TABLES;
DESCRIBE campaigns;
```

### User Service 테이블
```sql
USE userdb;
SHOW TABLES;
DESCRIBE users;
```

### Publisher Service 테이블
```sql
USE publisher_db;
SHOW TABLES;
DESCRIBE publishers;
```

## 유용한 SQL 쿼리 예시

### 광고 데이터 조회
```sql
-- Ad Service DB 연결 후
SELECT * FROM advertisements;
SELECT * FROM advertisements WHERE status = 'ACTIVE';
SELECT COUNT(*) as total_ads FROM advertisements;
```

### 캠페인 데이터 조회
```sql
-- Campaign Service DB 연결 후
SELECT * FROM campaigns;
SELECT * FROM campaigns WHERE status = 'ACTIVE';
```

### 사용자 데이터 조회
```sql
-- User Service DB 연결 후
SELECT * FROM users;
SELECT COUNT(*) as total_users FROM users;
```

### 퍼블리셔 데이터 조회
```sql
-- Publisher Service DB 연결 후
SELECT * FROM publishers;
SELECT COUNT(*) as total_publishers FROM publishers;
```

## 문제 해결

### 연결 실패 시 확인사항
1. Docker 컨테이너가 실행 중인지 확인:
   ```bash
   docker-compose ps
   ```

2. 포트가 올바른지 확인:
   - Ad DB: 3307
   - Campaign DB: 3308
   - User DB: 3310
   - Publisher DB: 3309

3. 방화벽 설정 확인 (Windows)

4. 데이터베이스가 초기화되었는지 확인:
   ```bash
   docker-compose logs ad-db
   docker-compose logs campaign-db
   docker-compose logs user-db
   docker-compose logs publisher-db
   ```

### 권한 문제 해결
```sql
-- MySQL에 접속 후
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```

## 자동 연결 설정

### VS Code 설정 파일 (.vscode/settings.json)
```json
{
  "database-client.connections": [
    {
      "name": "Ad Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3307,
      "database": "ad_db",
      "username": "root",
      "password": "root"
    },
    {
      "name": "Campaign Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3308,
      "database": "campaign_db",
      "username": "root",
      "password": "root"
    },
    {
      "name": "User Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3310,
      "database": "userdb",
      "username": "root",
      "password": "root"
    },
    {
      "name": "Publisher Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3309,
      "database": "publisher_db",
      "username": "root",
      "password": "root"
    }
  ]
}
``` 