package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.EmployeeCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.EmployeeResponse;
import com.example.assessment_employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    /**
     * Get all employees with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(Pageable pageable) {
        log.info("Get all employees request with pagination");
        
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
    
    /**
     * Get all employees without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployeesWithoutPagination() {
        log.info("Get all employees request without pagination");
        
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
    
    /**
     * Get employee by code
     */
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByCode(@PathVariable Long code) {
        log.info("Get employee by code: {}", code);
        
        EmployeeResponse employee = employeeService.getEmployeeByCode(code);
        
        return ResponseEntity.ok(ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .message("Employee retrieved successfully")
                .result(employee)
                .build());
    }
    
    /**
     * Get employee by account ID
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByAccountId(@PathVariable Long accountId) {
        log.info("Get employee by account ID: {}", accountId);
        
        EmployeeResponse employee = employeeService.getEmployeeByAccountId(accountId);
        
        return ResponseEntity.ok(ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .message("Employee retrieved successfully")
                .result(employee)
                .build());
    }
    
    /**
     * Create new employee
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        log.info("Create employee request for: {}", request.getFullName());
        
        EmployeeResponse employee = employeeService.createEmployee(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EmployeeResponse>builder()
                        .code(201)
                        .message("Employee created successfully")
                        .result(employee)
                        .build());
    }
    
    /**
     * Update employee
     */
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long code,
            @Valid @RequestBody EmployeeCreateRequest request) {
        log.info("Update employee request for code: {}", code);
        
        EmployeeResponse employee = employeeService.updateEmployee(code, request);
        
        return ResponseEntity.ok(ApiResponse.<EmployeeResponse>builder()
                .code(200)
                .message("Employee updated successfully")
                .result(employee)
                .build());
    }
    
    /**
     * Delete employee
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long code) {
        log.info("Delete employee request for code: {}", code);
        
        employeeService.deleteEmployee(code);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Employee deleted successfully")
                .build());
    }
    
    /**
     * Get employees by department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        log.info("Get employees by department ID: {}", departmentId);
        
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(departmentId);
        
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
    
    /**
     * Get employees by staff type
     */
    @GetMapping("/staff-type/{staffType}")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByStaffType(@PathVariable String staffType) {
        log.info("Get employees by staff type: {}", staffType);
        
        List<EmployeeResponse> employees = employeeService.getEmployeesByStaffType(staffType);
        
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
    
    /**
     * Search employees by fullName
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> searchEmployeesByFullName(@RequestParam String keyword) {
        log.info("Search employees by fullName keyword: {}", keyword);

        List<EmployeeResponse> employees = employeeService.searchEmployeesByFullName(keyword);

        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
    
    /**
     * Get employees by department and staff type
     */
    @GetMapping("/department/{departmentId}/staff-type/{staffType}")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartmentAndStaffType(
            @PathVariable Long departmentId,
            @PathVariable String staffType) {
        log.info("Get employees by department ID: {} and staff type: {}", departmentId, staffType);
        
        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartmentAndStaffType(departmentId, staffType);
        
        return ResponseEntity.ok(ApiResponse.<List<EmployeeResponse>>builder()
                .code(200)
                .message("Employees retrieved successfully")
                .result(employees)
                .build());
    }
}
