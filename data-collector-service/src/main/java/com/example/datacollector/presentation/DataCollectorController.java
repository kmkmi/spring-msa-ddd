package com.example.datacollector.presentation;

import com.example.datacollector.application.dto.AdEventRequest;
import com.example.datacollector.application.service.DataCollectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Data Collector", description = "광고 데이터 수집 API")
public class DataCollectorController {
    
    private final DataCollectorService dataCollectorService;
    
    @PostMapping("/ad")
    @Operation(summary = "광고 이벤트 수집", description = "광고 노출, 클릭, 전환 이벤트를 수집합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이벤트 수집 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<String> collectAdEvent(
            @Parameter(description = "광고 이벤트 데이터", required = true)
            @Valid @RequestBody AdEventRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Received ad event: {}", request);
        
        // IP 주소 자동 설정
        if (request.getIpAddress() == null) {
            String clientIp = getClientIpAddress(httpRequest);
            request.setIpAddress(clientIp);
        }
        
        // User-Agent 자동 설정
        if (request.getUserAgent() == null) {
            String userAgent = httpRequest.getHeader("User-Agent");
            request.setUserAgent(userAgent);
        }
        
        dataCollectorService.collectAdEvent(request);
        
        return ResponseEntity.ok("Event collected successfully");
    }
    
    @PostMapping("/ad/impression")
    @Operation(summary = "광고 노출 이벤트", description = "광고 노출 이벤트를 수집합니다.")
    public ResponseEntity<String> collectImpression(
            @Valid @RequestBody AdEventRequest request,
            HttpServletRequest httpRequest) {
        
        request.setEventType("IMPRESSION");
        return collectAdEvent(request, httpRequest);
    }
    
    @PostMapping("/ad/click")
    @Operation(summary = "광고 클릭 이벤트", description = "광고 클릭 이벤트를 수집합니다.")
    public ResponseEntity<String> collectClick(
            @Valid @RequestBody AdEventRequest request,
            HttpServletRequest httpRequest) {
        
        request.setEventType("CLICK");
        return collectAdEvent(request, httpRequest);
    }
    
    @PostMapping("/ad/conversion")
    @Operation(summary = "광고 전환 이벤트", description = "광고 전환 이벤트를 수집합니다.")
    public ResponseEntity<String> collectConversion(
            @Valid @RequestBody AdEventRequest request,
            HttpServletRequest httpRequest) {
        
        request.setEventType("CONVERSION");
        return collectAdEvent(request, httpRequest);
    }
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Data Collector Service is healthy");
    }
    
    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 