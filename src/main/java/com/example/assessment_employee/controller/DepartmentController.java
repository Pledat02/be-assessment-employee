package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.DepartmentCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.DepartmentResponse;
import com.example.assessment_employee.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    /**
     * Get all departments with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DepartmentResponse>>> getAllDepartments(Pageable pageable) {
        log.info("Get all departments request with pagination");
        
        Page<DepartmentResponse> departments = departmentService.getAllDepartments(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<DepartmentResponse>>builder()
                .code(200)
                .message("Departments retrieved successfully")
                .result(departments)
                .build());
    }
    
    /**
     * Get all departments without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartmentsWithoutPagination() {
        log.info("Get all departments request without pagination");
        
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        
        return ResponseEntity.ok(ApiResponse.<List<DepartmentResponse>>builder()
                .code(200)
                .message("Departments retrieved successfully")
                .result(departments)
                .build());
    }
    
    /**
     * Get department by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(@PathVariable Long id) {
        log.info("Get department by ID: {}", id);
        
        DepartmentResponse department = departmentService.getDepartmentById(id);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department retrieved successfully")
                .result(department)
                .build());
    }
    
    /**
     * Get department by name
     */
    @GetMapping("/name/{departmentName}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentByName(@PathVariable String departmentName) {
        log.info("Get department by name: {}", departmentName);
        
        DepartmentResponse department = departmentService.getDepartmentByName(departmentName);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department retrieved successfully")
                .result(department)
                .build());
    }
    
    /**
     * Get department by manager code
     */
    @GetMapping("/manager/{managerCode}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentByManagerCode(@PathVariable String managerCode) {
        log.info("Get department by manager code: {}", managerCode);
        
        DepartmentResponse department = departmentService.getDepartmentByManagerCode(managerCode);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department retrieved successfully")
                .result(department)
                .build());
    }
    
    /**
     * Create new department
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        log.info("Create department request for: {}", request.getDepartmentName());
        
        DepartmentResponse department = departmentService.createDepartment(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DepartmentResponse>builder()
                        .code(201)
                        .message("Department created successfully")
                        .result(department)
                        .build());
    }
    
    /**
     * Update department
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentCreateRequest request) {
        log.info("Update department request for ID: {}", id);
        
        DepartmentResponse department = departmentService.updateDepartment(id, request);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department updated successfully")
                .result(department)
                .build());
    }
    
    /**
     * Delete department
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        log.info("Delete department request for ID: {}", id);
        
        departmentService.deleteDepartment(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Department deleted successfully")
                .build());
    }
    
    /**
     * Search departments by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> searchDepartmentsByName(@RequestParam String keyword) {
        log.info("Search departments by name keyword: {}", keyword);
        
        List<DepartmentResponse> departments = departmentService.searchDepartmentsByName(keyword);
        
        return ResponseEntity.ok(ApiResponse.<List<DepartmentResponse>>builder()
                .code(200)
                .message("Departments retrieved successfully")
                .result(departments)
                .build());
    }
    
    /**
     * Get department with employees
     */
    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentWithEmployees(@PathVariable Long id) {
        log.info("Get department with employees for ID: {}", id);
        
        DepartmentResponse department = departmentService.getDepartmentWithEmployees(id);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department with employees retrieved successfully")
                .result(department)
                .build());
    }
    
    /**
     * Get department with evaluation cycles
     */
    @GetMapping("/{id}/evaluation-cycles")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentWithEvaluationCycles(@PathVariable Long id) {
        log.info("Get department with evaluation cycles for ID: {}", id);
        
        DepartmentResponse department = departmentService.getDepartmentWithEvaluationCycles(id);
        
        return ResponseEntity.ok(ApiResponse.<DepartmentResponse>builder()
                .code(200)
                .message("Department with evaluation cycles retrieved successfully")
                .result(department)
                .build());
    }
}
