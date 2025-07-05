package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EmployeeCreateRequest;
import com.example.assessment_employee.dto.response.EmployeeResponse;
import com.example.assessment_employee.entity.Account;
import com.example.assessment_employee.entity.Department;
import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.EmployeeMapper;
import com.example.assessment_employee.repository.AccountRepository;
import com.example.assessment_employee.repository.DepartmentRepository;
import com.example.assessment_employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    
    /**
     * Create new employee
     */
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        log.info("Creating new employee: {}", request.getFullName());
        
        // Validate account exists and is not already linked to another employee
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Account not found with ID: {}", request.getAccountId());
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        // Check if account is already linked to an employee
        if (employeeRepository.findByAccountId(request.getAccountId()).isPresent()) {
            log.warn("Account already linked to an employee: {}", request.getAccountId());
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        
        // Validate department exists
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", request.getDepartmentId());
                    return new AppException(ErrorCode.USER_NOT_EXISTED); // Using generic error for now
                });
        
        // Convert request to entity
        Employee employee = employeeMapper.toEntity(request);
        
        // Set relationships
        employee.setAccount(account);
        employee.setDepartment(department);
        
        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);
        
        log.info("Employee created successfully with code: {}", savedEmployee.getCode());
        
        return employeeMapper.toResponse(savedEmployee);
    }
    
    /**
     * Get all employees with pagination
     */
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.info("Getting all employees with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        List<EmployeeResponse> responses = employeeMapper.toResponseList(employeePage.getContent());
        
        return new PageImpl<>(responses, pageable, employeePage.getTotalElements());
    }
    
    /**
     * Get all employees without pagination
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        log.info("Getting all employees");
        
        List<Employee> employees = employeeRepository.findAllWithFullInfo();
        return employeeMapper.toResponseList(employees);
    }
    
    /**
     * Get employee by code
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByCode(Long code) {
        log.info("Getting employee by code: {}", code);
        
        Employee employee = employeeRepository.findById(code)
                .orElseThrow(() -> {
                    log.warn("Employee not found with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        return employeeMapper.toResponse(employee);
    }
    
    /**
     * Get employee by account ID
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByAccountId(Long accountId) {
        log.info("Getting employee by account ID: {}", accountId);
        
        Employee employee = employeeRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.warn("Employee not found with account ID: {}", accountId);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        return employeeMapper.toResponse(employee);
    }
    
    /**
     * Update employee
     */
    public EmployeeResponse updateEmployee(Long code, EmployeeCreateRequest request) {
        log.info("Updating employee with code: {}", code);
        
        Employee existingEmployee = employeeRepository.findById(code)
                .orElseThrow(() -> {
                    log.warn("Employee not found for update with code: {}", code);
                    return new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        
        // Validate account exists if being changed
        if (!existingEmployee.getAccount().getId().equals(request.getAccountId())) {
            Account newAccount = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> {
                        log.warn("Account not found with ID: {}", request.getAccountId());
                        return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });
            
            // Check if new account is already linked to another employee
            employeeRepository.findByAccountId(request.getAccountId())
                    .ifPresent(employee -> {
                        if (!employee.getCode().equals(code)) {
                            log.warn("Account already linked to another employee: {}", request.getAccountId());
                            throw new AppException(ErrorCode.USER_EXISTED);
                        }
                    });
            
            existingEmployee.setAccount(newAccount);
        }
        
        // Validate department exists if being changed
        if (!existingEmployee.getDepartment().getDepartmentId().equals(request.getDepartmentId())) {
            Department newDepartment = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        log.warn("Department not found with ID: {}", request.getDepartmentId());
                        return new AppException(ErrorCode.USER_NOT_EXISTED);
                    });
            
            existingEmployee.setDepartment(newDepartment);
        }
        
        // Update employee fields
        employeeMapper.updateEntity(request, existingEmployee);
        
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        
        log.info("Employee updated successfully with code: {}", updatedEmployee.getCode());
        
        return employeeMapper.toResponse(updatedEmployee);
    }
    
    /**
     * Delete employee
     */
    @Transactional
    public void deleteEmployee(Long code) {
        log.info("Deleting employee with code: {}", code);

        // Find the employee first to ensure it exists
        Employee employee = employeeRepository.findById(code)
            .orElseThrow(() -> {
                log.warn("Employee not found for deletion with code: {}", code);
                return new AppException(ErrorCode.USER_NOT_EXISTED);
            });

        // Delete the employee
        employeeRepository.delete(employee);

        log.info("Employee deleted successfully with code: {}", code);
    }
    
    /**
     * Get employees by department
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        log.info("Getting employees by department ID: {}", departmentId);
        
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);
        return employeeMapper.toResponseList(employees);
    }
    
    /**
     * Get employees by staff type
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByStaffType(String staffType) {
        log.info("Getting employees by staff type: {}", staffType);
        
        List<Employee> employees = employeeRepository.findByStaffType(staffType);
        return employeeMapper.toResponseList(employees);
    }
    
    /**
     * Search employees by fullName
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployeesByFullName(String keyword) {
        log.info("Searching employees by fullName keyword: {}", keyword);

        List<Employee> employees = employeeRepository.searchByFullName(keyword);
        return employeeMapper.toResponseList(employees);
    }
    
    /**
     * Get employees by department and staff type
     */
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartmentAndStaffType(Long departmentId, String staffType) {
        log.info("Getting employees by department ID: {} and staff type: {}", departmentId, staffType);
        
        List<Employee> employees = employeeRepository.findByDepartmentIdAndStaffType(departmentId, staffType);
        return employeeMapper.toResponseList(employees);
    }
}
