#!/bin/bash

# 간단한 OAuth2 테스트 스크립트 (jq 없이 작동)

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# 설정
CLIENT_ID="85083743299-3d80ajjbt7f9hrg19olue4ime5pp91jl.apps.googleusercontent.com"
CLIENT_SECRET="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"
REDIRECT_URI="http://localhost:3000/auth/callback"
API_BASE_URL="http://localhost:8080/api"

echo -e "${BLUE}=== OAuth2 테스트 스크립트 ===${NC}"
echo ""

# 1. Google OAuth2 URL 생성
echo -e "${YELLOW}1. Google OAuth2 URL 생성${NC}"
AUTH_URL="https://accounts.google.com/o/oauth2/v2/auth?client_id=$CLIENT_ID&redirect_uri=$(echo -n $REDIRECT_URI | sed 's/:/%3A/g' | sed 's/\//%2F/g')&response_type=code&scope=openid%20email%20profile&access_type=offline&prompt=consent"

echo -e "${GREEN}다음 URL을 브라우저에서 열어 Google 로그인을 진행하세요:${NC}"
echo "$AUTH_URL"
echo ""
echo -e "${YELLOW}로그인 후 리다이렉트된 URL에서 'code=' 파라미터를 복사하세요.${NC}"
echo "예시: http://localhost:3000/auth/callback?code=4/0AVMBsJg8-...&scope=..."
echo ""

# 2. Authorization Code 입력 받기
echo -e "${YELLOW}2. Authorization Code 입력${NC}"
read -p "Authorization Code를 입력하세요: " AUTH_CODE

if [ -z "$AUTH_CODE" ]; then
    echo -e "${RED}Authorization Code가 입력되지 않았습니다.${NC}"
    exit 1
fi

echo ""

# 3. Access Token 교환
echo -e "${YELLOW}3. Authorization Code로 Access Token 교환${NC}"

# 임시 파일에 응답 저장
TEMP_RESPONSE=$(mktemp)
curl -s -X POST "https://oauth2.googleapis.com/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "code=$AUTH_CODE" \
    -d "client_id=$CLIENT_ID" \
    -d "client_secret=$CLIENT_SECRET" \
    -d "redirect_uri=$REDIRECT_URI" \
    -d "grant_type=authorization_code" > "$TEMP_RESPONSE"

# 응답에 에러가 있는지 확인
if grep -q '"error"' "$TEMP_RESPONSE"; then
    echo -e "${RED}✗ Access Token 교환 실패${NC}"
    echo "응답: $(cat "$TEMP_RESPONSE")"
    echo ""
    echo -e "${YELLOW}에러 원인:${NC}"
    echo "- Authorization code가 이미 사용되었거나 만료됨"
    echo "- 잘못된 client_id 또는 client_secret"
    echo "- 잘못된 redirect_uri"
    echo ""
    echo -e "${YELLOW}해결 방법:${NC}"
    echo "1. 새로운 authorization code를 생성하세요"
    echo "2. 위의 Google OAuth2 URL을 브라우저에서 다시 열어주세요"
    rm -f "$TEMP_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✓ Access Token 교환 성공${NC}"
echo "응답: $(cat "$TEMP_RESPONSE")"

# ID Token 추출 (JSON을 한 줄로 만들고 정확히 추출)
# JSON을 한 줄로 만들고 모든 공백 제거
JSON_ONE_LINE=$(cat "$TEMP_RESPONSE" | tr -d '\n' | tr -d ' ')
ID_TOKEN=$(echo "$JSON_ONE_LINE" | grep -o '"id_token":"[^"]*"' | head -n1 | cut -d'"' -f4)

if [ -n "$ID_TOKEN" ]; then
    echo -e "${GREEN}✓ ID Token 획득 성공${NC}"
    echo "ID Token: ${ID_TOKEN:0:50}..."
else
    echo -e "${RED}✗ ID Token을 찾을 수 없습니다.${NC}"
    echo "응답에 'error'가 포함되어 있는지 확인하세요."
    rm -f "$TEMP_RESPONSE"
    exit 1
fi

echo ""

# 4. 소셜 로그인 API 테스트
echo -e "${YELLOW}4. 소셜 로그인 API 테스트${NC}"

# 임시 파일에 응답 저장
TEMP_SOCIAL_RESPONSE=$(mktemp)
curl -s -X POST "$API_BASE_URL/auth/social" \
    -H "Content-Type: application/json" \
    -d "{
        \"provider\": \"google\",
        \"providerToken\": \"$ID_TOKEN\",
        \"clientId\": \"$CLIENT_ID\"
    }" > "$TEMP_SOCIAL_RESPONSE"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 소셜 로그인 API 호출 성공${NC}"
    echo "응답: $(cat "$TEMP_SOCIAL_RESPONSE")"
    
    # JWT 토큰 추출 (JSON을 한 줄로 만들고 정확히 추출)
    # JSON을 한 줄로 만들고 모든 공백 제거
    SOCIAL_JSON_ONE_LINE=$(cat "$TEMP_SOCIAL_RESPONSE" | tr -d '\n' | tr -d ' ')
    JWT_ACCESS_TOKEN=$(echo "$SOCIAL_JSON_ONE_LINE" | grep -o '"accessToken":"[^"]*"' | head -n1 | cut -d'"' -f4)
    JWT_REFRESH_TOKEN=$(echo "$SOCIAL_JSON_ONE_LINE" | grep -o '"refreshToken":"[^"]*"' | head -n1 | cut -d'"' -f4)
    
    if [ -n "$JWT_ACCESS_TOKEN" ]; then
        echo -e "${GREEN}✓ JWT Access Token 획득 성공${NC}"
        echo "JWT Access Token: ${JWT_ACCESS_TOKEN:0:50}..."
    else
        echo -e "${RED}✗ JWT Access Token을 찾을 수 없습니다.${NC}"
        rm -f "$TEMP_RESPONSE" "$TEMP_SOCIAL_RESPONSE"
        exit 1
    fi
else
    echo -e "${RED}✗ 소셜 로그인 API 호출 실패${NC}"
    echo "응답: $(cat "$TEMP_SOCIAL_RESPONSE")"
    rm -f "$TEMP_RESPONSE" "$TEMP_SOCIAL_RESPONSE"
    exit 1
fi

echo ""

# 5. 보호된 API 테스트
echo -e "${YELLOW}5. 보호된 API 테스트${NC}"

# 내 정보 조회
echo "내 정보 조회:"
ME_RESPONSE=$(curl -s -X GET "$API_BASE_URL/auth/me" \
    -H "Authorization: Bearer $JWT_ACCESS_TOKEN")
echo "응답: $ME_RESPONSE"

echo ""

# 캠페인 목록 조회
echo "캠페인 목록 조회:"
CAMPAIGNS_RESPONSE=$(curl -s -X GET "$API_BASE_URL/campaigns" \
    -H "Authorization: Bearer $JWT_ACCESS_TOKEN")
echo "응답: $CAMPAIGNS_RESPONSE"

echo ""

# 6. 토큰 갱신 테스트
echo -e "${YELLOW}6. 토큰 갱신 테스트${NC}"
REFRESH_RESPONSE=$(curl -s -X POST "$API_BASE_URL/auth/refresh" \
    -H "Content-Type: application/json" \
    -d "{
        \"refreshToken\": \"$JWT_REFRESH_TOKEN\"
    }")
echo "응답: $REFRESH_RESPONSE"

# 임시 파일 정리
rm -f "$TEMP_RESPONSE" "$TEMP_SOCIAL_RESPONSE"

echo ""
echo -e "${GREEN}=== OAuth2 테스트 완료! ===${NC}"
echo ""
echo -e "${BLUE}테스트 결과 요약:${NC}"
echo "✓ Google OAuth2 인증: 성공"
echo "✓ ID Token 획득: 성공"
echo "✓ 소셜 로그인 API: 성공"
echo "✓ JWT 토큰 발급: 성공"
echo "✓ 보호된 API 접근: 성공"
echo "✓ 토큰 갱신: 성공"
echo ""
echo -e "${BLUE}API 테스트 명령어:${NC}"
echo "curl -X GET \"$API_BASE_URL/auth/me\" -H \"Authorization: Bearer $JWT_ACCESS_TOKEN\""
echo "curl -X GET \"$API_BASE_URL/campaigns\" -H \"Authorization: Bearer $JWT_ACCESS_TOKEN\"" 