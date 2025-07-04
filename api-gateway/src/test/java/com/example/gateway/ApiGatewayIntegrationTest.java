package com.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
public class ApiGatewayIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testContextLoads() {
        // 실제로 ApplicationContext가 정상적으로 로드되는지 검증
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void testGatewayConfiguration() {
        // GatewaySecurityConfig 빈이 정상적으로 등록되어 있는지 검증
        assertThat(applicationContext.containsBean("springSecurityFilterChain")).isTrue();
    }
} 