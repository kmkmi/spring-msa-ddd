# 📖 Swagger UI 사용 가이드

## 개요

이 프로젝트의 각 마이크로서비스는 Swagger UI를 통해 API 문서를 제공합니다. Swagger UI를 통해 API를 탐색하고 실시간으로 테스트할 수 있습니다.

## 🔗 접속 URL

### Swagger UI 인터페이스
- **Campaign Service**: http://localhost:8081/swagger-ui.html
- **Ad Service**: http://localhost:8082/swagger-ui.html  
- **User Service**: http://localhost:8083/swagger-ui.html
- **Publisher Service**: http://localhost:8084/swagger-ui.html

### OpenAPI JSON 문서
- **Campaign Service**: http://localhost:8081/api-docs
- **Ad Service**: http://localhost:8082/api-docs
- **User Service**: http://localhost:8083/api-docs
- **Publisher Service**: http://localhost:8084/api-docs

## 🛠️ 주요 기능

### 1. API 엔드포인트 탐색
- 모든 REST API 엔드포인트를 카테고리별로 확인
- HTTP 메서드별로 색상 구분 (GET: 초록, POST: 파랑, PUT: 주황, DELETE: 빨강)
- 각 엔드포인트의 상세 설명 및 파라미터 정보

### 2. 실시간 API 테스트
- 브라우저에서 직접 API 호출 가능
- 요청 데이터를 JSON 형태로 입력
- 응답 결과 및 HTTP 상태 코드 확인
- 요청/응답 시간 측정

### 3. 스키마 정보
- 요청/응답 데이터의 JSON 스키마 제공
- 필수/선택 필드 구분
- 데이터 타입 및 제약 조건 표시

### 4. 상태 코드 및 에러 응답
- 각 엔드포인트별 가능한 HTTP 상태 코드
- 에러 응답 예시 및 설명
- 성공/실패 케이스별 응답 형태

## 📝 사용 예시

### Campaign Service API 테스트

1. **브라우저에서 Swagger UI 접속**
   ```
   http://localhost:8081/swagger-ui.html
   ```

2. **캠페인 생성 API 테스트**
   - "Campaign" 섹션에서 "POST /campaigns" 클릭
   - "Try it out" 버튼 클릭
   - 요청 데이터 입력:
   ```json
   {
     "name": "테스트 캠페인",
     "description": "Swagger UI 테스트용 캠페인",
     "publisherId": 1,
     "campaignType": "DISPLAY",
     "budgetAmount": 1000000.00,
     "dailyBudget": 100000.00,
     "startDate": "2024-01-01T00:00:00",
     "endDate": "2024-12-31T23:59:59",
     "targetAudience": "20-30대",
     "targetLocations": "서울, 부산"
   }
   ```
   - "Execute" 버튼 클릭
   - 응답 결과 확인

3. **캠페인 조회 API 테스트**
   - "GET /campaigns/{id}" 클릭
   - "Try it out" 버튼 클릭
   - id 파라미터에 "1" 입력
   - "Execute" 버튼 클릭
   - 조회된 캠페인 정보 확인

### Ad Service API 테스트

1. **광고 생성 API 테스트**
   ```
   http://localhost:8082/swagger-ui.html
   ```
   - "Advertisement" 섹션에서 "POST /ads" 클릭
   - 요청 데이터 입력:
   ```json
   {
     "campaignId": 1,
     "title": "테스트 광고",
     "description": "Swagger UI 테스트용 광고",
     "imageUrl": "https://example.com/image.jpg",
     "landingPageUrl": "https://example.com",
     "adType": "DISPLAY",
     "bidAmount": 1000.00,
     "dailyBudget": 50000.00
   }
   ```

### User Service API 테스트

1. **사용자 생성 API 테스트**
   ```
   http://localhost:8083/swagger-ui.html
   ```
   - "User" 섹션에서 "POST /users" 클릭
   - 요청 데이터 입력:
   ```json
   {
     "email": "test@example.com",
     "name": "테스트 사용자",
     "password": "password123"
   }
   ```

## 🔧 설정 정보

### Swagger UI 설정 (application.yml)
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
    display-request-duration: true
  default-produces-media-type: application/json
  default-consumes-media-type: application/json
```

### OpenAPI 설정 클래스
각 서비스에는 `OpenApiConfig` 클래스가 있어 API 문서의 메타데이터를 설정합니다:
- API 제목 및 버전
- 서버 정보
- 연락처 정보
- 라이선스 정보

## 📋 API 태그별 분류

### Campaign Service
- **Campaign**: 캠페인 관리 API
  - POST /campaigns - 캠페인 생성
  - GET /campaigns/{id} - 캠페인 조회
  - GET /campaigns/publisher/{publisherId} - 퍼블리셔별 캠페인 조회
  - GET /campaigns/status/{status} - 상태별 캠페인 조회
  - GET /campaigns/active - 활성 캠페인 조회
  - PUT /campaigns/{id}/status - 캠페인 상태 변경
  - PUT /campaigns/{id}/budget - 캠페인 예산 변경

### Ad Service
- **Advertisement**: 광고 관리 API
  - POST /ads - 광고 생성
  - GET /ads/{id} - 광고 조회
  - GET /ads/campaign/{campaignId} - 캠페인별 광고 조회
  - PUT /ads/{id}/status - 광고 상태 변경
  - PUT /ads/{id}/metrics - 광고 메트릭 업데이트

### User Service
- **User**: 사용자 관리 API
  - POST /users - 사용자 생성
  - GET /users/{id} - 사용자 조회
  - GET /users/email/{email} - 이메일로 사용자 조회
  - GET /users - 전체 사용자 조회
  - PUT /users/{id}/status - 사용자 상태 변경

### Publisher Service
- **Publisher**: 퍼블리셔 관리 API
  - GET /publishers/health - 헬스 체크

## 🚨 주의사항

1. **서비스 실행 확인**: Swagger UI 접속 전 해당 서비스가 정상 실행 중인지 확인
2. **데이터베이스 연결**: API 테스트 전 데이터베이스가 정상 연결되어 있는지 확인
3. **필수 필드**: 요청 시 필수 필드는 반드시 입력해야 함
4. **데이터 형식**: 날짜는 ISO 8601 형식 (YYYY-MM-DDTHH:mm:ss) 사용
5. **에러 처리**: API 호출 실패 시 응답의 에러 메시지 확인

## 🔍 문제 해결

### Swagger UI가 로드되지 않는 경우
1. 서비스가 정상 실행 중인지 확인
2. 포트가 올바른지 확인
3. 방화벽 설정 확인

### API 호출이 실패하는 경우
1. 요청 데이터 형식 확인
2. 필수 필드 누락 여부 확인
3. 데이터베이스 연결 상태 확인
4. 서비스 로그 확인

### CORS 오류가 발생하는 경우
1. 브라우저 개발자 도구에서 CORS 설정 확인
2. API Gateway 설정 확인
3. 서비스 간 통신 설정 확인 