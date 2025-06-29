#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counter
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

echo -e "${BLUE}========================================"
echo "MSA Spring Boot Test Suite"
echo "========================================"
echo -e "${NC}"

# Function to run tests for a service
run_service_tests() {
    local service_name="$1"
    local service_path="$2"
    
    echo -e "${YELLOW}Testing $service_name...${NC}"
    echo "========================================"
    
    cd "$service_path"
    
    if mvn test -Dspring.profiles.active=test; then
        echo -e "${GREEN}✓ $service_name tests passed${NC}"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}✗ $service_name tests failed${NC}"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo ""
    
    cd - > /dev/null
}

# Run tests for each service
run_service_tests "User Service" "user-service"
run_service_tests "Campaign Service" "campaign-service"
run_service_tests "Ad Service" "ad-service"
run_service_tests "Publisher Service" "publisher-service"
run_service_tests "API Gateway" "api-gateway"

# Summary
echo -e "${BLUE}========================================"
echo "TEST SUMMARY"
echo "========================================"
echo -e "${NC}"
echo "Total Services Tested: $TOTAL_TESTS"
echo -e "Passed: ${GREEN}$PASSED_TESTS${NC}"
echo -e "Failed: ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}All service tests passed! 🎉${NC}"
    exit 0
else
    echo -e "${RED}Some service tests failed! ❌${NC}"
    exit 1
fi 