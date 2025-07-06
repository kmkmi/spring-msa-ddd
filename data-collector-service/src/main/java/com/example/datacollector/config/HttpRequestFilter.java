package com.example.datacollector.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class HttpRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // SSL/TLS 핸드셰이크 데이터 감지
            String method = httpRequest.getMethod();
            if (method == null || method.trim().isEmpty()) {
                log.warn("Invalid HTTP method detected, rejecting request");
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            // 잘못된 문자 시퀀스 감지
            String requestURI = httpRequest.getRequestURI();
            if (requestURI != null && containsInvalidCharacters(requestURI)) {
                log.warn("Invalid characters detected in request URI: {}", requestURI);
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean containsInvalidCharacters(String uri) {
        // SSL/TLS 핸드셰이크에서 나타날 수 있는 패턴들
        String[] invalidPatterns = {
            "0x16", "0x03", "0x01", "0x00", "0x00", "0x00", "0x00", "0x00"
        };
        
        for (String pattern : invalidPatterns) {
            if (uri.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
} 