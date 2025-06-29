package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.LoginRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.LoginResponse;
import com.example.assessment_employee.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        
        LoginResponse response = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .code(200)
                .message("Login successful")
                .result(response)
                .build());
    }
    
    /**
     * User logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request");
        
        authService.logout(token);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Logout successful")
                .build());
    }
    
    /**
     * Get current user information
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String token) {
        log.info("Get current user request");
        
        // Extract username from token
        String username = authService.validateTokenAndGetUsername(token);
        LoginResponse.UserInfo userInfo = authService.getCurrentUser(username);
        
        return ResponseEntity.ok(ApiResponse.<LoginResponse.UserInfo>builder()
                .code(200)
                .message("User information retrieved successfully")
                .result(userInfo)
                .build());
    }
}
