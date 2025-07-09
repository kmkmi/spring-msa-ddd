package com.example.gateway.filter;

import com.example.gateway.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Profile;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // 공개 경로는 인증 없이 통과
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        
        String token = getJwtFromRequest(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("No JWT token found in request to: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid JWT token in request to: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // 토큰이 유효하면 사용자 정보를 헤더에 추가
        String username = jwtTokenProvider.getUsernameFromToken(token);
        if (StringUtils.hasText(username)) {
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Email", username)
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100; // 높은 우선순위
    }

    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/gateway/routes") ||
               path.startsWith("/api/v1/users/health") ||
               path.startsWith("/api/v1/campaigns/health") ||
               path.startsWith("/api/v1/ads/health") ||
               path.startsWith("/api/v1/publishers/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/v3/api-docs/");
    }
} 