package com.example.user.interfaces;

import com.example.user.application.dto.CreateUserRequest;
import com.example.user.application.dto.UserResponse;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "사용자 생성 요청 데이터", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Received request to create user: {}", request.getEmail());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회", description = "ID로 특정 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long id) {
        log.info("Received request to get user by id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 특정 사용자를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "사용자 이메일", required = true)
            @PathVariable String email) {
        log.info("Received request to get user by email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Received request to get all users");
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "사용자 상태 변경", description = "사용자의 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 상태 변경 성공",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserResponse> updateUserStatus(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "변경할 사용자 상태", required = true)
            @RequestParam User.UserStatus status) {
        log.info("Received request to update user status for id: {} to: {}", id, status);
        UserResponse response = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "서비스 정상 동작")
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is healthy");
    }
} 