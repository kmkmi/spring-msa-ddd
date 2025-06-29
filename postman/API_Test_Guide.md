# Advertising MSA API 테스트 가이드

## 1. Postman 설정

### Collection 및 Environment Import
1. Postman을 실행합니다.
2. **Import** 버튼을 클릭합니다.
3. 다음 파일들을 import합니다:
   - `Advertising_MSA_API.postman_collection.json`
   - `Advertising_MSA_Environment.postman_environment.json`

### Environment 설정
1. 우측 상단의 Environment 드롭다운에서 **"Advertising MSA Environment"**를 선택합니다.
2. **base_url**이 API Gateway URL로 설정됩니다: `http://localhost:8080`
3. 모든 API 요청은 API Gateway를 통해 라우팅됩니다.

## 2. API 테스트 순서

### 2.1 Ad Service 테스트

#### 광고 등록
```
POST http://localhost:8080/api/ads
Content-Type: application/json

{
  "campaignId": 1,
  "title": "새로운 광고 캠페인",
  "description": "매력적인 광고 설명",
  "imageUrl": "https://example.com/image.jpg",
  "videoUrl": "https://example.com/video.mp4",
  "landingPageUrl": "https://example.com/landing",
  "adType": "BANNER",
  "status": "DRAFT",
  "bidAmount": 10.50,
  "dailyBudget": 100.00,
  "totalSpent": 0.00,
  "impressions": 0,
  "clicks": 0,
  "ctr": 0.0,
  "startDate": "2025-06-29T00:00:00",
  "endDate": "2025-07-29T23:59:59"
}
```

#### 광고 조회
```
GET http://localhost:8080/api/ads/1
```

#### 캠페인별 광고 목록 조회
```
GET http://localhost:8080/api/ads/campaign/1
```

#### 광고 상태 변경
```
PUT http://localhost:8080/api/ads/1/status?status=ACTIVE
```

#### 광고 메트릭 업데이트
```
PUT http://localhost:8080/api/ads/1/metrics?impressions=1000&clicks=50&ctr=0.05
```

### 2.2 Campaign Service 테스트

#### 캠페인 등록
```
POST http://localhost:8080/api/campaigns
Content-Type: application/json

{
  "name": "여름 세일 캠페인",
  "description": "여름 시즌 특별 할인 캠페인",
  "publisherId": 1,
  "budget": 5000.00,
  "startDate": "2025-06-29T00:00:00",
  "endDate": "2025-07-29T23:59:59",
  "status": "ACTIVE"
}
```

#### 캠페인 조회
```
GET http://localhost:8080/api/campaigns/1
```

#### 퍼블리셔별 캠페인 목록 조회
```
GET http://localhost:8080/api/campaigns/publisher/1?page=0&size=10
```

#### 상태별 캠페인 목록 조회
```
GET http://localhost:8080/api/campaigns/status/ACTIVE
```

#### 활성 캠페인 목록 조회
```
GET http://localhost:8080/api/campaigns/active
```

#### 캠페인 상태 변경
```
PUT http://localhost:8080/api/campaigns/1/status?status=PAUSED
```

#### 캠페인 예산 변경
```
PUT http://localhost:8080/api/campaigns/1/budget?budgetAmount=6000.00&dailyBudget=200.00
```

#### 캠페인 서비스 헬스 체크
```
GET http://localhost:8080/api/campaigns/health
```

### 2.3 User Service 테스트

#### 사용자 등록
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "홍",
  "lastName": "길동",
  "phone": "010-1234-5678",
  "role": "USER"
}
```

#### 사용자 조회
```
GET http://localhost:8080/api/users/1
```

#### 이메일로 사용자 조회
```
GET http://localhost:8080/api/users/email/test@example.com
```

#### 전체 사용자 목록 조회
```
GET http://localhost:8080/api/users
```

#### 사용자 상태 변경
```
PUT http://localhost:8080/api/users/1/status?status=INACTIVE
```

#### 사용자 서비스 헬스 체크
```
GET http://localhost:8080/api/users/health
```

### 2.4 Publisher Service 테스트

#### 퍼블리셔 서비스 헬스 체크
```
GET http://localhost:8080/api/publishers/health
```

### 2.5 API Gateway 테스트

#### API Gateway 헬스 체크
```
GET http://localhost:8080/actuator/health
```

## 3. API Gateway를 통한 통합 테스트

모든 서비스는 API Gateway (포트: 8080)를 통해 접근합니다:

### Ad Service
- `GET http://localhost:8080/api/ads/1`
- `POST http://localhost:8080/api/ads`
- `GET http://localhost:8080/api/ads/campaign/1`
- `PUT http://localhost:8080/api/ads/1/status?status=ACTIVE`
- `PUT http://localhost:8080/api/ads/1/metrics?impressions=1000&clicks=50&ctr=0.05`

### Campaign Service
- `GET http://localhost:8080/api/campaigns/1`
- `POST http://localhost:8080/api/campaigns`
- `GET http://localhost:8080/api/campaigns/publisher/1`
- `GET http://localhost:8080/api/campaigns/status/ACTIVE`
- `GET http://localhost:8080/api/campaigns/active`
- `PUT http://localhost:8080/api/campaigns/1/status?status=PAUSED`
- `PUT http://localhost:8080/api/campaigns/1/budget?budgetAmount=6000.00&dailyBudget=200.00`
- `GET http://localhost:8080/api/campaigns/health`

### User Service
- `GET http://localhost:8080/api/users/1`
- `POST http://localhost:8080/api/users`
- `GET http://localhost:8080/api/users/email/test@example.com`
- `GET http://localhost:8080/api/users`
- `PUT http://localhost:8080/api/users/1/status?status=INACTIVE`
- `GET http://localhost:8080/api/users/health`

## 4. 예상 응답 예시

### 광고 등록 성공 응답
```json
{
  "id": 1,
  "campaignId": 1,
  "title": "새로운 광고 캠페인",
  "description": "매력적인 광고 설명",
  "imageUrl": "https://example.com/image.jpg",
  "videoUrl": "https://example.com/video.mp4",
  "landingPageUrl": "https://example.com/landing",
  "adType": "BANNER",
  "status": "DRAFT",
  "bidAmount": 10.5,
  "dailyBudget": 100.0,
  "totalSpent": 0.0,
  "impressions": 0,
  "clicks": 0,
  "ctr": 0.0,
  "startDate": "2025-06-29T00:00:00",
  "endDate": "2025-07-29T23:59:59"
}
```

### 캠페인 등록 성공 응답
```json
{
  "id": 1,
  "name": "여름 세일 캠페인",
  "description": "여름 시즌 특별 할인 캠페인",
  "publisherId": 1,
  "budget": 5000.00,
  "startDate": "2025-06-29T00:00:00",
  "endDate": "2025-07-29T23:59:59",
  "status": "ACTIVE"
}
```

### 사용자 등록 성공 응답
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "홍",
  "lastName": "길동",
  "phone": "010-1234-5678",
  "role": "USER",
  "status": "ACTIVE"
}
```

## 5. 상태 코드

- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `400 Bad Request`: 잘못된 요청
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

## 6. Enum 값들

### AdType
- `BANNER`
- `VIDEO`
- `NATIVE`
- `TEXT`
- `RICH_MEDIA`

### AdStatus
- `DRAFT`
- `ACTIVE`
- `PAUSED`
- `COMPLETED`
- `REJECTED`

### CampaignStatus
- `ACTIVE`
- `PAUSED`
- `COMPLETED`
- `DRAFT`

### UserStatus
- `ACTIVE`
- `INACTIVE`
- `SUSPENDED`

## 7. 문제 해결

### 서비스가 응답하지 않는 경우
1. Docker 컨테이너가 실행 중인지 확인:
   ```bash
   docker-compose ps
   ```

2. 서비스 로그 확인:
   ```bash
   docker-compose logs ad-service
   docker-compose logs campaign-service
   docker-compose logs user-service
   docker-compose logs api-gateway
   ```

3. API Gateway 헬스 체크:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

4. 개별 서비스 헬스 체크:
   ```bash
   curl http://localhost:8080/api/campaigns/health
   curl http://localhost:8080/api/users/health
   curl http://localhost:8080/api/publishers/health
   ```

### API Gateway 라우팅 문제
1. API Gateway 설정 확인:
   ```bash
   docker-compose logs api-gateway
   ```

2. 서비스 디스커버리 확인:
   ```bash
   docker-compose logs discovery-service
   ```

### 데이터베이스 연결 문제
1. 데이터베이스 컨테이너 상태 확인:
   ```bash
   docker-compose ps | grep mysql
   ```

2. 데이터베이스 로그 확인:
   ```bash
   docker-compose logs mysql
   ``` 