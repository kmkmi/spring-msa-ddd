package com.example.user.presentation;

import com.example.user.application.dto.CreateUserRequest;
import com.example.user.application.service.UserService;
import com.example.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "사용자 관리 API")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is healthy");
    }

    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
            content = @Content(schema = @Schema(implementation = User.UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User.UserResponse> createUser(
            @Parameter(description = "사용자 생성 요청", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("Creating user with email: {}", request.getEmail());
        User.UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "ID로 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = User.UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User.UserResponse> getUserById(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long id) {
        
        log.info("Getting user by id: {}", id);
        User.UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "모든 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = User.UserResponse.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User.UserResponse>> getAllUsers() {
        log.info("Getting all users");
        List<User.UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "사용자 상태 변경", description = "사용자의 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User.UserResponse> updateUserStatus(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "새로운 상태", required = true)
            @RequestParam User.UserStatus status) {
        
        log.info("Updating user status for id: {} to: {}", id, status);
        User.UserResponse userResponse = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = User.UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User.UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);
        User.UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }
} 