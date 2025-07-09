#!/bin/bash

echo "🚀 광고 데이터 처리 플랫폼 전체 서비스 빌드 시작..."

# 키 파일 복사 (빌드 전에 실행)
echo "📁 키 파일을 모든 서비스에 복사 중..."
for service in ad-service api-gateway campaign-service publisher-service user-service data-collector-service data-processor-service ad-recommendation-service batch-processing-service
  do
    mkdir -p $service/src/main/resources/keys
    cp -f keys/private_key.pem $service/src/main/resources/keys/ 2>/dev/null
    cp -f keys/public_key.pem $service/src/main/resources/keys/ 2>/dev/null
  done

# jwks-service는 public_key.pem만 복사
echo "📁 jwks-service에 public_key.pem 복사 중..."
mkdir -p jwks-service/src/main/resources/keys
cp -f keys/public_key.pem jwks-service/src/main/resources/keys/ 2>/dev/null

echo "🏗️ MSA 광고 데이터 처리 플랫폼 빌드 중..."

echo "📦 (선택) 전체 멀티모듈 빌드: 최상위 demo 프로젝트에서 아래 명령어로 한 번에 빌드할 수 있습니다."
echo "    mvn clean install -DskipTests"
echo ""
# Build shared module first (required by other services)
echo "📦 Shared Module 빌드 중..."
cd shared
mvn clean install -DskipTests
cd ..

# Build all services
echo "🔧 Config Service 빌드 중..."
cd config-service
mvn clean package -DskipTests
cd ..

echo "🌐 API Gateway 빌드 중..."
cd api-gateway
mvn clean package -DskipTests
cd ..

echo "📊 Campaign Service 빌드 중..."
cd campaign-service
mvn clean package -DskipTests
cd ..

echo "👤 User Service 빌드 중..."
cd user-service
mvn clean package -DskipTests
cd ..

echo "📢 Ad Service 빌드 중..."
cd ad-service
mvn clean package -DskipTests
cd ..

echo "📰 Publisher Service 빌드 중..."
cd publisher-service
mvn clean package -DskipTests
cd ..

echo "🔑 JWKS Service 빌드 중..."
cd jwks-service
mvn clean package -DskipTests
cd ..

echo "📥 Data Collector Service 빌드 중..."
cd data-collector-service
mvn clean package -DskipTests
cd ..

echo "⚙️ Data Processor Service 빌드 중..."
cd data-processor-service
mvn clean package -DskipTests
cd ..

echo "🎯 Ad Recommendation Service 빌드 중..."
cd ad-recommendation-service
mvn clean package -DskipTests
cd ..

echo "📈 Batch Processing Service 빌드 중..."
cd batch-processing-service
mvn clean package -DskipTests
cd ..

echo "⚡ Flink Applications 빌드 중..."
cd flink-apps
mvn clean package -DskipTests
cd ..

echo "🔥 Spark Applications 빌드 중..."
cd spark-apps
mvn clean package -DskipTests
cd ..

echo ""
echo "✅ 모든 서비스 빌드 완료!"
echo ""
echo "📋 빌드된 서비스 목록:"
echo "  🔧 Config Service"
echo "  🌐 API Gateway"
echo "  📊 Campaign Service"
echo "  👤 User Service"
echo "  📢 Ad Service"
echo "  📰 Publisher Service"
echo "  🔑 JWKS Service"
echo "  📥 Data Collector Service"
echo "  ⚙️ Data Processor Service"
echo "  🎯 Ad Recommendation Service"
echo "  📈 Batch Processing Service"
echo "  ⚡ Flink Applications"
echo "  🔥 Spark Applications"
echo ""
echo "🚀 서비스 시작 명령어:"
echo "  docker-compose down -v && docker-compose build --no-cache && docker-compose up -d  "
echo ""
echo "📊 서비스 상태 확인:"
echo "  docker-compose ps"
echo ""
 