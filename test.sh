#!/bin/bash

# 키 파일 복사 (빌드와 동일)
echo "Copying keys to all services..."
for service in ad-service api-gateway campaign-service publisher-service user-service
  do
    mkdir -p $service/src/main/resources/keys
    cp -f keys/private_key.pem $service/src/main/resources/keys/ 2>/dev/null
    cp -f keys/public_key.pem $service/src/main/resources/keys/ 2>/dev/null
  done
# jwks-service는 public_key.pem만 복사
echo "Copying public_key.pem to jwks-service..."
mkdir -p jwks-service/src/main/resources/keys
cp -f keys/public_key.pem jwks-service/src/main/resources/keys/ 2>/dev/null

echo "Running tests for all services (with test profile)..."
for service in ad-service api-gateway campaign-service publisher-service user-service
  do
    echo "Testing $service..."
    cd $service
    mvn clean test -Dspring.profiles.active=test
    cd ..
  done

echo "All tests completed!" 