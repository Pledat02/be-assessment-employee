package com.example.assessment_employee.controller;

import com.example.assessment_employee.constants.RoleConstants;
import com.example.assessment_employee.dto.request.AccountCreateRequest;
import com.example.assessment_employee.dto.response.AccountResponse;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * Get all accounts with pagination
     * Chỉ MANAGER mới có quyền xem tất cả tài khoản
     */
    @GetMapping
    @PreAuthorize(RoleConstants.HAS_ROLE_MANAGER)
    public ResponseEntity<ApiResponse<Page<AccountResponse>>> getAllAccounts(Pageable pageable) {
        log.info("Get all accounts request with pagination");
        
        Page<AccountResponse> accounts = accountService.getAllAccounts(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<AccountResponse>>builder()
                .code(200)
                .message("Accounts retrieved successfully")
                .result(accounts)
                .build());
    }
    
    /**
     * Get all accounts without pagination
     * Chỉ MANAGER mới có quyền xem tất cả tài khoản
     */
    @GetMapping("/all")
    @PreAuthorize(RoleConstants.HAS_ROLE_MANAGER)
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccountsWithoutPagination() {
        log.info("Get all accounts request without pagination");
        
        List<AccountResponse> accounts = accountService.getAllAccounts();
        
        return ResponseEntity.ok(ApiResponse.<List<AccountResponse>>builder()
                .code(200)
                .message("Accounts retrieved successfully")
                .result(accounts)
                .build());
    }
    
    /**
     * Get account by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id) {
        log.info("Get account by ID: {}", id);
        
        AccountResponse account = accountService.getAccountById(id);
        
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .code(200)
                .message("Account retrieved successfully")
                .result(account)
                .build());
    }
    
    /**
     * Get account by username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByUsername(@PathVariable String username) {
        log.info("Get account by username: {}", username);
        
        AccountResponse account = accountService.getAccountByUsername(username);
        
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .code(200)
                .message("Account retrieved successfully")
                .result(account)
                .build());
    }
    
    /**
     * Create new account
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        log.info("Create account request for username: {}", request.getUsername());
        
        AccountResponse account = accountService.createAccount(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<AccountResponse>builder()
                        .code(201)
                        .message("Account created successfully")
                        .result(account)
                        .build());
    }
    
    /**
     * Update account
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountCreateRequest request) {
        log.info("Update account request for ID: {}", id);
        
        AccountResponse account = accountService.updateAccount(id, request);
        
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .code(200)
                .message("Account updated successfully")
                .result(account)
                .build());
    }
    
    /**
     * Change password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestBody String newPassword) {
        log.info("Change password request for account ID: {}", id);
        
        accountService.changePassword(id, newPassword);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Password changed successfully")
                .build());
    }
    
    /**
     * Update account status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Update account status request for ID: {} to status: {}", id, status);
        
        AccountResponse account = accountService.updateAccountStatus(id, status);
        
        return ResponseEntity.ok(ApiResponse.<AccountResponse>builder()
                .code(200)
                .message("Account status updated successfully")
                .result(account)
                .build());
    }
    
    /**
     * Delete account
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        log.info("Delete account request for ID: {}", id);
        
        accountService.deleteAccount(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Account deleted successfully")
                .build());
    }
    
    /**
     * Get accounts by role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByRole(@PathVariable String role) {
        log.info("Get accounts by role: {}", role);
        
        List<AccountResponse> accounts = accountService.getAccountsByRole(role);
        
        return ResponseEntity.ok(ApiResponse.<List<AccountResponse>>builder()
                .code(200)
                .message("Accounts retrieved successfully")
                .result(accounts)
                .build());
    }
    
    /**
     * Get active accounts
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getActiveAccounts() {
        log.info("Get active accounts request");
        
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        
        return ResponseEntity.ok(ApiResponse.<List<AccountResponse>>builder()
                .code(200)
                .message("Active accounts retrieved successfully")
                .result(accounts)
                .build());
    }
}
