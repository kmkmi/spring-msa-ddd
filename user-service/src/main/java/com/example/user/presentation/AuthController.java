package com.example.user.presentation;

import com.example.shared.security.JwtTokenProvider;
import com.example.shared.security.dto.AuthRequest;
import com.example.shared.security.dto.AuthResponse;
import com.example.shared.security.dto.RefreshTokenRequest;
import com.example.user.application.dto.CreateUserRequest;
import com.example.user.application.service.UserService;
import com.example.user.application.service.SocialUserService;
import com.example.user.application.service.CommonUserService;
import com.example.user.domain.User;
import com.example.user.domain.SocialUser;
import com.example.user.domain.UserBase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.example.shared.security.dto.SocialLoginRequest;
import com.example.shared.security.provider.GoogleTokenVerifier;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final UserService userService;
    private final SocialUserService socialUserService;
    private final CommonUserService commonUserService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<User.UserResponse> signup(@Valid @RequestBody CreateUserRequest request) {
        log.info("Signup request for email: {}", request.getEmail());
        User.UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody AuthRequest request) {
        log.info("Signin request for email: {}", request.getEmail());
        userService.authenticateUser(request.getEmail(), request.getPassword());
        String username = request.getEmail();
        List<String> roles = userService.getRolesByEmail(username);
        String accessToken = jwtTokenProvider.generateToken(username, roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username, roles);
        AuthResponse authResponse = AuthResponse.of(
                accessToken, 
                refreshToken, 
                username, 
                roles, 
                86400000L // 24시간
        );
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request");
        
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(request.getRefreshToken());
        
        List<String> roles = userService.getRolesByEmail(username);
        
        String newAccessToken = jwtTokenProvider.generateToken(username, roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, roles);
        
        AuthResponse authResponse = AuthResponse.of(
                newAccessToken, 
                newRefreshToken, 
                username, 
                roles, 
                86400000L // 24시간
        );
        
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 인증된 사용자의 정보를 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserBase> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Authorities 디버깅 정보 추가
        log.info("=== Authentication Debug Info ===");
        log.info("Principal: {}", authentication.getPrincipal());
        log.info("Name: {}", authentication.getName());
        log.info("Authorities: {}", authentication.getAuthorities());
        log.info("Credentials: {}", authentication.getCredentials());
        log.info("Details: {}", authentication.getDetails());
        log.info("================================");
        
        String email = authentication.getName();
        
        // CommonUserService를 사용하여 통합 조회
        Optional<UserBase> userOpt = commonUserService.findUserByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/social")
    @Operation(summary = "소셜 로그인", description = "구글 등 외부 Provider 토큰으로 로그인")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> socialLogin(@RequestBody SocialLoginRequest request) {
        if (!"google".equalsIgnoreCase(request.getProvider())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // 동적으로 client_id를 받아서 GoogleTokenVerifier 생성
        GoogleTokenVerifier verifier = new GoogleTokenVerifier(request.getClientId());
        var payload = verifier.verify(request.getProviderToken());
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = (String) payload.getEmail();
        String name = (String) payload.get("name");
        String providerId = (String) payload.get("sub");
        String profileImageUrl = (String) payload.get("picture");
        
        // CommonUserService를 사용하여 통합 조회
        List<String> roles = commonUserService.getRolesByEmail(email);
        boolean isExistingUser = !roles.isEmpty();
        
        // 기존 사용자가 없는 경우에만 소셜 사용자 생성
        if (!isExistingUser) {
            // 새로운 소셜 사용자 - 자동 회원가입
            socialUserService.createSocialUser(
                email, name, SocialUser.SocialProvider.GOOGLE, providerId, profileImageUrl);
            roles = socialUserService.getRolesByEmail(email);
            log.info("New social user created: {}", email);
        } else {
            // 기존 사용자 - 소셜 사용자인 경우 마지막 로그인 시간 업데이트
            Optional<SocialUser.SocialUserResponse> socialUserOpt = socialUserService.getSocialUserByEmail(email);
            if (socialUserOpt.isPresent()) {
                socialUserService.updateLastLogin(email);
                log.info("Existing social user found: {}", email);
            }
        }
        
        String accessToken = jwtTokenProvider.generateToken(email, roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email, roles);
        return ResponseEntity.ok(AuthResponse.of(accessToken, refreshToken, name, roles, 86400000L));
    }
} 