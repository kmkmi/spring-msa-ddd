# Spring Security API 가이드

이 문서는 광고 도메인 MSA에 적용된 Spring Security 기반 인증/권한 시스템의 사용법을 설명합니다.

## 🔐 인증 시스템 개요

### JWT 기반 인증
- **액세스 토큰**: 24시간 유효
- **리프레시 토큰**: 7일 유효
- **토큰 타입**: Bearer Token

### 사용자 역할 (Roles)
- **ADMIN**: 시스템 관리자 (모든 권한)
- **PUBLISHER**: 퍼블리셔 (캠페인 관리 권한)
- **ADVERTISER**: 광고주 (광고 관리 권한)
- **USER**: 일반 사용자 (읽기 권한)

## 🚀 API 사용 가이드

### 1. 회원가입

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "name": "New User",
    "password": "password123",
    "role": "PUBLISHER"
  }'
```

**응답 예시:**
```json
{
  "id": 5,
  "email": "newuser@example.com",
  "name": "New User",
  "status": "ACTIVE",
  "role": "PUBLISHER",
  "createdAt": "2024-01-15T10:30:00"
}
```

### 2. 로그인

```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "publisher@advertising.com",
    "password": "publisher123"
  }'
```

**응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "email": "publisher@advertising.com",
  "roles": ["ROLE_PUBLISHER"]
}
```

### 3. 토큰 갱신

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### 4. 내 정보 조회

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 📋 권한별 API 접근

### ADMIN 권한
- 모든 API 접근 가능
- 사용자 관리
- 시스템 설정 관리

### PUBLISHER 권한
- 캠페인 생성/수정/삭제
- 캠페인 상태 변경
- 캠페인 조회

### ADVERTISER 권한
- 광고 생성/수정/삭제
- 광고 상태 변경
- 캠페인 조회 (읽기만)

### USER 권한
- 기본 정보 조회
- 제한된 읽기 권한

## 🔧 API 예시

### 캠페인 생성 (PUBLISHER 권한 필요)

```bash
curl -X POST http://localhost:8080/api/campaigns \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Summer Sale Campaign",
    "description": "Summer sale campaign for e-commerce",
    "publisherId": 1,
    "campaignType": "DISPLAY",
    "budgetAmount": 10000.00,
    "dailyBudget": 1000.00,
    "startDate": "2024-06-01T00:00:00",
    "endDate": "2024-08-31T23:59:59",
    "targetAudience": "18-35",
    "targetLocations": "Seoul, Busan"
  }'
```

### 사용자 목록 조회 (ADMIN 권한 필요)

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 캠페인 조회 (모든 인증된 사용자)

```bash
curl -X GET http://localhost:8080/api/campaigns/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 🛡️ 보안 설정

### JWT 설정
```yaml
jwt:
  secret: your-super-secret-jwt-key-for-advertising-msa-2024
  expiration: 86400000  # 24시간
  refresh-expiration: 604800000  # 7일
```

### 공개 경로
- `/api/auth/**` - 인증 관련 API
- `/actuator/health` - 헬스 체크
- `/swagger-ui/**` - API 문서
- `/api-docs/**` - OpenAPI 문서

### 보호된 경로
- `/api/admin/**` - ADMIN 권한 필요
- `/api/publishers/**` - ADMIN, PUBLISHER 권한 필요
- `/api/campaigns/**` - ADMIN, PUBLISHER 권한 필요
- `/api/ads/**` - ADMIN, ADVERTISER 권한 필요

## 🔍 에러 처리

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing JWT token"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Insufficient permissions"
}
```

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Invalid request data"
}
```

## 🧪 테스트 계정

### 기본 테스트 계정
| 이메일 | 비밀번호 | 역할 | 설명 |
|--------|----------|------|------|
| admin@advertising.com | admin123 | ADMIN | 시스템 관리자 |
| publisher@advertising.com | publisher123 | PUBLISHER | 퍼블리셔 |
| advertiser@advertising.com | advertiser123 | ADVERTISER | 광고주 |
| user@advertising.com | user123 | USER | 일반 사용자 |

## 📝 Postman 사용법

### 1. 환경 변수 설정
```
BASE_URL: http://localhost:8080
ACCESS_TOKEN: {{auth_response.accessToken}}
REFRESH_TOKEN: {{auth_response.refreshToken}}
```

### 2. 로그인 후 토큰 저장
```javascript
// Tests 탭에 추가
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("ACCESS_TOKEN", response.accessToken);
    pm.environment.set("REFRESH_TOKEN", response.refreshToken);
}
```

### 3. Authorization 헤더 설정
```
Authorization: Bearer {{ACCESS_TOKEN}}
```

## 🔄 토큰 자동 갱신

### Postman Pre-request Script
```javascript
// 토큰 만료 시 자동 갱신
const accessToken = pm.environment.get("ACCESS_TOKEN");
const refreshToken = pm.environment.get("REFRESH_TOKEN");

if (!accessToken && refreshToken) {
    pm.sendRequest({
        url: pm.environment.get("BASE_URL") + "/api/auth/refresh",
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                refreshToken: refreshToken
            })
        }
    }, function (err, response) {
        if (response.code === 200) {
            const authResponse = response.json();
            pm.environment.set("ACCESS_TOKEN", authResponse.accessToken);
            pm.environment.set("REFRESH_TOKEN", authResponse.refreshToken);
        }
    });
}
```

## 🚨 보안 주의사항

1. **토큰 보안**
   - 토큰을 안전하게 저장
   - HTTPS 사용 필수
   - 토큰 만료 시 자동 갱신

2. **권한 관리**
   - 최소 권한 원칙 적용
   - 정기적인 권한 검토
   - 불필요한 권한 제거

3. **모니터링**
   - 인증 실패 로그 모니터링
   - 비정상적인 접근 패턴 감지
   - 정기적인 보안 감사

## 📚 추가 리소스

- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [JWT 공식 사이트](https://jwt.io/)
- [OAuth 2.0 가이드](https://oauth.net/2/) 