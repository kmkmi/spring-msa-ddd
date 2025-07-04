package com.example.publisher.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/publishers")
@Tag(name = "Publisher", description = "퍼블리셔 관리 API")
public class PublisherController {

    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "서비스 정상 동작")
    })
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Publisher Service is healthy");
    }

    @GetMapping
    @Operation(summary = "모든 퍼블리셔 조회", description = "모든 퍼블리셔 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "퍼블리셔 목록 조회 성공")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<List<String>> getAll() {
        return ResponseEntity.ok(Collections.emptyList());
    }
} 