#!/bin/bash

set -e  # Exit immediately if any command fails

# 키 파일 복사 (main과 test 리소스 모두)
echo "Copying keys to all services..."
for service in ad-service api-gateway campaign-service publisher-service user-service batch-processing-service data-collector-service data-processor-service ad-recommendation-service
  do
    # main 리소스에 키 복사
    mkdir -p $service/src/main/resources/keys
    cp -f keys/private_key.pem $service/src/main/resources/keys/ 2>/dev/null
    cp -f keys/public_key.pem $service/src/main/resources/keys/ 2>/dev/null
    
    # test 리소스에 키 복사
    mkdir -p $service/src/test/resources/keys
    cp -f keys/private_key.pem $service/src/test/resources/keys/ 2>/dev/null
    cp -f keys/public_key.pem $service/src/test/resources/keys/ 2>/dev/null
  done
# jwks-service는 public_key.pem만 복사
echo "Copying public_key.pem to jwks-service..."
mkdir -p jwks-service/src/main/resources/keys
cp -f keys/public_key.pem jwks-service/src/main/resources/keys/ 2>/dev/null
mkdir -p jwks-service/src/test/resources/keys
cp -f keys/public_key.pem jwks-service/src/test/resources/keys/ 2>/dev/null

echo "Running tests for all services (with test profile)..."
for service in ad-service api-gateway campaign-service publisher-service user-service batch-processing-service data-collector-service data-processor-service ad-recommendation-service jwks-service
  do
    echo "Testing $service..."
    cd $service
    if ! mvn clean test -Dspring.profiles.active=test; then
      echo "❌ Tests failed in $service. Stopping execution."
      exit 1
    fi
    cd ..
    echo "✅ $service tests passed."
  done

echo "All tests completed successfully!" 