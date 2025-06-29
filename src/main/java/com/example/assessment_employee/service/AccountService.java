package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.AccountCreateRequest;
import com.example.assessment_employee.dto.response.AccountResponse;
import com.example.assessment_employee.entity.Account;
import com.example.assessment_employee.enums.Role;
import com.example.assessment_employee.enums.AccountStatus;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.AccountMapper;
import com.example.assessment_employee.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Create new account
     */
    public AccountResponse createAccount(AccountCreateRequest request) {
        log.info("Creating new account with username: {}", request.getUsername());
        
        // Check if username already exists
        if (accountRepository.existsByUsername(request.getUsername())) {
            log.warn("Account creation failed: Username already exists: {}", request.getUsername());
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Validate role
        if (!Role.isValidRole(request.getRole())) {
            log.warn("Account creation failed: Invalid role: {}", request.getRole());
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }

        // Validate status
        if (request.getStatus() != null && !AccountStatus.isValidStatus(request.getStatus())) {
            log.warn("Account creation failed: Invalid status: {}", request.getStatus());
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }

        // Convert request to entity
        Account account = accountMapper.toEntity(request);
        
        // Encode password
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Save account
        Account savedAccount = accountRepository.save(account);
        
        log.info("Account created successfully with ID: {}", savedAccount.getId());
        
        return accountMapper.toResponse(savedAccount);
    }
    
    /**
     * Get all accounts with pagination
     */
    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(Pageable pageable) {
        log.info("Getting all accounts with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Account> accountPage = accountRepository.findAll(pageable);
        List<AccountResponse> responses = accountMapper.toResponseList(accountPage.getContent());
        
        return new PageImpl<>(responses, pageable, accountPage.getTotalElements());
    }
    
    /**
     * Get all accounts without pagination
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        log.info("Getting all accounts");
        
        List<Account> accounts = accountRepository.findAllWithEmployee();
        return accountMapper.toResponseList(accounts);
    }
    
    /**
     * Get account by ID
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        log.info("Getting account by ID: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        return accountMapper.toResponse(account);
    }
    
    /**
     * Get account by username
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountByUsername(String username) {
        log.info("Getting account by username: {}", username);
        
        Account account = accountRepository.findByUsernameWithEmployee(username)
                .orElseThrow(() -> {
                    log.warn("Account not found with username: {}", username);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        return accountMapper.toResponse(account);
    }
    
    /**
     * Update account
     */
    public AccountResponse updateAccount(Long id, AccountCreateRequest request) {
        log.info("Updating account with ID: {}", id);
        
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found for update with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        // Check if username is being changed and if new username already exists
        if (!existingAccount.getUsername().equals(request.getUsername()) &&
            accountRepository.existsByUsername(request.getUsername())) {
            log.warn("Account update failed: Username already exists: {}", request.getUsername());
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Update account fields
        accountMapper.updateEntity(request, existingAccount);
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        Account updatedAccount = accountRepository.save(existingAccount);
        
        log.info("Account updated successfully with ID: {}", updatedAccount.getId());
        
        return accountMapper.toResponse(updatedAccount);
    }
    
    /**
     * Change password
     */
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for account ID: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found for password change with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        
        log.info("Password changed successfully for account ID: {}", id);
    }
    
    /**
     * Update account status
     */
    public AccountResponse updateAccountStatus(Long id, String status) {
        log.info("Updating account status for ID: {} to status: {}", id, status);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Account not found for status update with ID: {}", id);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        
        log.info("Account status updated successfully for ID: {}", id);
        
        return accountMapper.toResponse(updatedAccount);
    }
    
    /**
     * Delete account
     */
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        
        if (!accountRepository.existsById(id)) {
            log.warn("Account not found for deletion with ID: {}", id);
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        
        accountRepository.deleteById(id);
        
        log.info("Account deleted successfully with ID: {}", id);
    }
    
    /**
     * Get accounts by role
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByRole(String role) {
        log.info("Getting accounts by role: {}", role);
        
        List<Account> accounts = accountRepository.findByRole(role);
        return accountMapper.toResponseList(accounts);
    }
    
    /**
     * Get active accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        log.info("Getting active accounts");
        
        List<Account> accounts = accountRepository.findActiveAccounts();
        return accountMapper.toResponseList(accounts);
    }
}
