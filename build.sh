#!/bin/bash

# 키 파일 복사 (빌드 전에 실행)
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

echo "Building MSA Advertising Platform..."

# Build shared module first (required by other services)
echo "Building Shared Module..."
cd shared
mvn clean install -DskipTests
cd ..

# Build all services
echo "Building Config Service..."
cd config-service
mvn clean package -DskipTests
cd ..

echo "Building API Gateway..."
cd api-gateway
mvn clean package -DskipTests
cd ..

echo "Building Campaign Service..."
cd campaign-service
mvn clean package -DskipTests
cd ..

echo "Building User Service..."
cd user-service
mvn clean package -DskipTests
cd ..

echo "Building Ad Service..."
cd ad-service
mvn clean package -DskipTests
cd ..

echo "Building Publisher Service..."
cd publisher-service
mvn clean package -DskipTests
cd ..

echo "Building JWKS Service..."
cd jwks-service
mvn clean package -DskipTests
cd ..

echo "All services built successfully!"
echo "Run 'docker-compose up -d' to start the services" 