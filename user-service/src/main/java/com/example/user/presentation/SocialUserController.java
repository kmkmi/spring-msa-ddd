package com.example.user.presentation;

import com.example.user.application.service.SocialUserService;
import com.example.user.domain.SocialUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/social-users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Social User Management", description = "소셜 사용자 관리 API")
public class SocialUserController {
    
    private final SocialUserService socialUserService;
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Social User Service is healthy");
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "소셜 사용자 조회", description = "ID로 소셜 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "소셜 사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = SocialUser.SocialUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "소셜 사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocialUser.SocialUserResponse> getSocialUserById(
            @Parameter(description = "소셜 사용자 ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting social user by id: {}", id);
        try {
            SocialUser.SocialUserResponse socialUser = socialUserService.getSocialUserById(id);
            return ResponseEntity.ok(socialUser);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 소셜 사용자 조회", description = "이메일로 소셜 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "소셜 사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = SocialUser.SocialUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "소셜 사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocialUser.SocialUserResponse> getSocialUserByEmail(
            @Parameter(description = "이메일", required = true)
            @PathVariable String email) {
        
        log.info("Getting social user by email: {}", email);
        Optional<SocialUser.SocialUserResponse> socialUserOpt = socialUserService.getSocialUserByEmail(email);
        
        return socialUserOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/provider/{provider}/{providerId}")
    @Operation(summary = "Provider ID로 소셜 사용자 조회", description = "소셜 Provider와 Provider ID로 소셜 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "소셜 사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = SocialUser.SocialUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "소셜 사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocialUser.SocialUserResponse> getSocialUserByProviderAndProviderId(
            @Parameter(description = "소셜 Provider", required = true)
            @PathVariable SocialUser.SocialProvider provider,
            @Parameter(description = "Provider ID", required = true)
            @PathVariable String providerId) {
        
        log.info("Getting social user by provider: {} and providerId: {}", provider, providerId);
        Optional<SocialUser.SocialUserResponse> socialUserOpt = socialUserService.getSocialUserByProviderAndProviderId(provider, providerId);
        
        return socialUserOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "모든 소셜 사용자 조회", description = "모든 소셜 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "소셜 사용자 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = SocialUser.SocialUserResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SocialUser.SocialUserResponse>> getAllSocialUsers() {
        log.info("Getting all social users");
        List<SocialUser.SocialUserResponse> socialUsers = socialUserService.getAllSocialUsers();
        return ResponseEntity.ok(socialUsers);
    }
} 