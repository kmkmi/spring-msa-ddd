#!/bin/bash

echo "Building MSA Advertising Platform..."

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

echo "All services built successfully!"
echo "Run 'docker-compose up -d' to start the services" 