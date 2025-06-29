# Cursor 내에서 데이터베이스 GUI 설정 가이드

## 1. Database Client 확장 설치

### 확장 프로그램 설치
1. **Ctrl+Shift+X** (확장 마켓플레이스 열기)
2. 검색창에 `Database Client` 입력
3. **Database Client** (by cweijan) 찾기
4. **Install** 버튼 클릭

### 확장 프로그램 정보
- **이름**: Database Client
- **제작자**: cweijan
- **설치 ID**: `cweijan.vscode-mysql-client2`
- **기능**: MySQL, PostgreSQL, SQLite, MongoDB 지원

## 2. 데이터베이스 연결 설정

### 첫 번째 연결 (Ad Service DB)
1. **Ctrl+Shift+P** (명령 팔레트 열기)
2. `Database: Add Connection` 입력 후 선택
3. **MySQL** 선택
4. 연결 정보 입력:
   ```
   Host: localhost
   Port: 3307
   Username: root
   Password: password
   Database: addb
   ```
5. **Test Connection** 클릭하여 연결 확인
6. **Save** 클릭

### 추가 연결들
동일한 방법으로 다음 DB들도 연결:

#### Campaign Service DB
```
Host: localhost
Port: 3308
Username: root
Password: password
Database: campaigndb
```

#### User Service DB
```
Host: localhost
Port: 3310
Username: root
Password: password
Database: userdb
```

#### Publisher Service DB
```
Host: localhost
Port: 3309
Username: root
Password: password
Database: publisherdb
```

## 3. Database Client GUI 사용법

### 사이드바에서 데이터베이스 탐색
1. 좌측 사이드바에서 **Database** 아이콘 클릭
2. 연결된 데이터베이스 목록 확인
3. 데이터베이스 클릭하여 테이블 목록 확인
4. 테이블 클릭하여 데이터 확인

### SQL 쿼리 실행
1. **Ctrl+Shift+P** → `Database: New Query` 선택
2. SQL 쿼리 작성:
   ```sql
   USE addb;
   SELECT * FROM advertisements;
   ```
3. **Ctrl+Shift+E** (실행) 또는 **Run** 버튼 클릭

### 테이블 데이터 편집
1. 테이블 우클릭 → **View Data** 선택
2. 데이터를 직접 편집 가능
3. **Save** 버튼으로 변경사항 저장

## 4. 유용한 SQL 쿼리 예시

### Ad Service 데이터 확인
```sql
-- Ad Service DB 연결 후
USE addb;
SELECT * FROM advertisements;
SELECT COUNT(*) as total_ads FROM advertisements;
SELECT * FROM advertisements WHERE status = 'ACTIVE';
```

### Campaign Service 데이터 확인
```sql
-- Campaign Service DB 연결 후
USE campaigndb;
SELECT * FROM campaigns;
SELECT * FROM campaigns WHERE status = 'ACTIVE';
```

### User Service 데이터 확인
```sql
-- User Service DB 연결 후
USE userdb;
SELECT * FROM users;
SELECT COUNT(*) as total_users FROM users;
SELECT * FROM users WHERE role = 'ADMIN';
```

### Publisher Service 데이터 확인
```sql
-- Publisher Service DB 연결 후
USE publisherdb;
SELECT * FROM publishers;
SELECT * FROM publishers WHERE status = 'ACTIVE';
```

## 5. 고급 기능

### 데이터 내보내기/가져오기
1. 테이블 우클릭 → **Export Data**
2. CSV, JSON, SQL 형식으로 내보내기 가능
3. **Import Data**로 데이터 가져오기 가능

### 스키마 확인
1. 테이블 우클릭 → **View Structure**
2. 컬럼 정보, 인덱스, 제약조건 확인

### 쿼리 히스토리
1. **Ctrl+Shift+P** → `Database: Show Query History`
2. 이전에 실행한 쿼리들 확인

## 6. 문제 해결

### 연결 실패 시
1. Docker 컨테이너 상태 확인:
   ```bash
   docker-compose ps
   ```

2. 포트 확인:
   - Ad DB: 3307
   - Campaign DB: 3308
   - User DB: 3310
   - Publisher DB: 3309

3. 방화벽 설정 확인 (Windows)

### 권한 문제
```sql
-- MySQL에 접속 후
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```

## 7. 단축키 모음

- **새 쿼리**: `Ctrl+Shift+P` → `Database: New Query`
- **쿼리 실행**: `Ctrl+Shift+E`
- **연결 추가**: `Ctrl+Shift+P` → `Database: Add Connection`
- **쿼리 히스토리**: `Ctrl+Shift+P` → `Database: Show Query History`

## 8. 추천 설정

### settings.json에 추가할 설정
```json
{
  "database-client.connections": [
    {
      "name": "Ad Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3307,
      "database": "addb",
      "username": "root",
      "password": "password"
    },
    {
      "name": "Campaign Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3308,
      "database": "campaigndb",
      "username": "root",
      "password": "password"
    },
    {
      "name": "User Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3310,
      "database": "userdb",
      "username": "root",
      "password": "password"
    },
    {
      "name": "Publisher Service DB",
      "driver": "MySQL",
      "server": "localhost",
      "port": 3309,
      "database": "publisherdb",
      "username": "root",
      "password": "password"
    }
  ]
}
``` 