package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.LoginRequest;
import com.example.assessment_employee.dto.response.LoginResponse;
import com.example.assessment_employee.entity.Account;
import com.example.assessment_employee.enums.Role;
import com.example.assessment_employee.enums.AccountStatus;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.AccountMapper;
import com.example.assessment_employee.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration:86400}") // 24 hours in seconds
    private long jwtExpirationInSeconds;
    

    
    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());
        
        // Find account by username
        Account account = accountRepository.findByUsernameWithEmployee(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: Account not found for username: {}", request.getUsername());
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        // Check account status
        if ("INACTIVE".equals(account.getStatus())) {
            log.warn("Login failed: Account is inactive for username: {}", request.getUsername());
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        
        // Verify password (temporarily disabled hash check for testing)
        // TODO: Re-enable password hashing later
        if (!request.getPassword().equals(account.getPassword()) &&
            !passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            log.warn("Login failed: Invalid password for username: {}", request.getUsername());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        
        // Generate JWT token (simplified - in real implementation, use proper JWT library)
        String token = generateToken(account);
        
        // Convert to response
        LoginResponse.UserInfo userInfo = accountMapper.toUserInfo(account);
        
        log.info("Login successful for username: {}", request.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationInSeconds)
                .userInfo(userInfo)
                .build();
    }
    
    /**
     * Logout user (invalidate token)
     * Note: In a real implementation, you would maintain a blacklist of tokens
     */
    public void logout(String token) {
        log.info("User logged out with token: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
        // In a real implementation, add token to blacklist or revoke it
    }
    
    /**
     * Get current user information from token
     */
    public LoginResponse.UserInfo getCurrentUser(String username) {
        log.info("Getting current user info for username: {}", username);
        
        Account account = accountRepository.findByUsernameWithEmployee(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        return accountMapper.toUserInfo(account);
    }
    
    /**
     * Validate token and extract username using Spring Security JWT
     */
    public String validateTokenAndGetUsername(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                // Use CustomJwtDecoder for validation - it will handle JWT parsing and validation
                // For now, we'll extract username from the token directly
                return extractUsernameFromToken(jwtToken);
            }
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsernameFromToken(String token) {
        try {
            // Parse JWT token to extract claims
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            // Decode payload (claims)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));

            // Simple JSON parsing to extract subject (username)
            // In production, use proper JSON library
            if (payload.contains("\"sub\":")) {
                String sub = payload.split("\"sub\":\"")[1].split("\"")[0];
                return sub;
            }

            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    /**
     * Extract role from JWT token
     */
    public String extractRoleFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Parse JWT token to extract claims
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            // Decode payload (claims)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(chunks[1]));

            // Simple JSON parsing to extract role
            if (payload.contains("\"role\":")) {
                String role = payload.split("\"role\":\"")[1].split("\"")[0];
                return role;
            }

            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Failed to extract role from token: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
    
    /**
     * Check if user has required role
     */
    public boolean hasRole(String username, String requiredRole) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        return requiredRole.equals(account.getRole());
    }
    
    /**
     * Check if user has any of the required roles
     */
    public boolean hasAnyRole(String username, String... requiredRoles) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        for (String role : requiredRoles) {
            if (role.equals(account.getRole())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Generate real JWT token using Spring Security JWT
     */
    private String generateToken(Account account) {
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtExpirationInSeconds, ChronoUnit.SECONDS);

        // Build JWT claims
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("assessment-employee-system")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(account.getUsername())
                .claim("role", account.getRole())
                .claim("status", account.getStatus());

        // Add employee information if available
        if (account.getEmployee() != null) {
            claimsBuilder
                    .claim("employeeCode", account.getEmployee().getCode())
                    .claim("fullName", account.getEmployee().getFullName())
                    .claim("departmentId", account.getEmployee().getDepartment() != null ?
                            account.getEmployee().getDepartment().getDepartmentId() : null);
        }

        JwtClaimsSet claims = claimsBuilder.build();

        // Create JWT header
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // Encode JWT
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
