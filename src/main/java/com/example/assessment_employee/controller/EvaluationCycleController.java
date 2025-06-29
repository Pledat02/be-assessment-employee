package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.EvaluationCycleCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.EvaluationCycleResponse;
import com.example.assessment_employee.service.EvaluationCycleService;
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
@RequestMapping("/api/evaluation-cycles")
@RequiredArgsConstructor
@Slf4j
public class EvaluationCycleController {
    
    private final EvaluationCycleService evaluationCycleService;
    
    /**
     * Get all evaluation cycles with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluationCycleResponse>>> getAllEvaluationCycles(Pageable pageable) {
        log.info("Get all evaluation cycles request with pagination");
        
        Page<EvaluationCycleResponse> cycles = evaluationCycleService.getAllEvaluationCycles(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
    
    /**
     * Get all evaluation cycles without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EvaluationCycleResponse>>> getAllEvaluationCyclesWithoutPagination() {
        log.info("Get all evaluation cycles request without pagination");
        
        List<EvaluationCycleResponse> cycles = evaluationCycleService.getAllEvaluationCycles();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
    
    /**
     * Get evaluation cycle by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationCycleResponse>> getEvaluationCycleById(@PathVariable Long id) {
        log.info("Get evaluation cycle by ID: {}", id);
        
        EvaluationCycleResponse cycle = evaluationCycleService.getEvaluationCycleById(id);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCycleResponse>builder()
                .code(200)
                .message("Evaluation cycle retrieved successfully")
                .result(cycle)
                .build());
    }
    
    /**
     * Create new evaluation cycle
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationCycleResponse>> createEvaluationCycle(
            @Valid @RequestBody EvaluationCycleCreateRequest request) {
        log.info("Create evaluation cycle request for department: {}", request.getDepartmentId());
        
        EvaluationCycleResponse cycle = evaluationCycleService.createEvaluationCycle(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EvaluationCycleResponse>builder()
                        .code(201)
                        .message("Evaluation cycle created successfully")
                        .result(cycle)
                        .build());
    }
    
    /**
     * Update evaluation cycle
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationCycleResponse>> updateEvaluationCycle(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCycleCreateRequest request) {
        log.info("Update evaluation cycle request for ID: {}", id);
        
        EvaluationCycleResponse cycle = evaluationCycleService.updateEvaluationCycle(id, request);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCycleResponse>builder()
                .code(200)
                .message("Evaluation cycle updated successfully")
                .result(cycle)
                .build());
    }
    
    /**
     * Update evaluation cycle status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EvaluationCycleResponse>> updateEvaluationCycleStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("Update evaluation cycle status request for ID: {} to status: {}", id, status);
        
        EvaluationCycleResponse cycle = evaluationCycleService.updateEvaluationCycleStatus(id, status);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCycleResponse>builder()
                .code(200)
                .message("Evaluation cycle status updated successfully")
                .result(cycle)
                .build());
    }
    
    /**
     * Delete evaluation cycle
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluationCycle(@PathVariable Long id) {
        log.info("Delete evaluation cycle request for ID: {}", id);
        
        evaluationCycleService.deleteEvaluationCycle(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Evaluation cycle deleted successfully")
                .build());
    }
    
    /**
     * Get evaluation cycles by department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<EvaluationCycleResponse>>> getEvaluationCyclesByDepartment(
            @PathVariable Long departmentId) {
        log.info("Get evaluation cycles by department ID: {}", departmentId);
        
        List<EvaluationCycleResponse> cycles = evaluationCycleService.getEvaluationCyclesByDepartment(departmentId);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
    
    /**
     * Get evaluation cycles by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EvaluationCycleResponse>>> getEvaluationCyclesByStatus(
            @PathVariable String status) {
        log.info("Get evaluation cycles by status: {}", status);
        
        List<EvaluationCycleResponse> cycles = evaluationCycleService.getEvaluationCyclesByStatus(status);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
    
    /**
     * Get active evaluation cycles
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EvaluationCycleResponse>>> getActiveEvaluationCycles() {
        log.info("Get active evaluation cycles request");
        
        List<EvaluationCycleResponse> cycles = evaluationCycleService.getActiveEvaluationCycles();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Active evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
    
    /**
     * Get active cycles by department
     */
    @GetMapping("/department/{departmentId}/active")
    public ResponseEntity<ApiResponse<List<EvaluationCycleResponse>>> getActiveCyclesByDepartment(
            @PathVariable Long departmentId) {
        log.info("Get active evaluation cycles by department ID: {}", departmentId);
        
        List<EvaluationCycleResponse> cycles = evaluationCycleService.getActiveCyclesByDepartment(departmentId);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCycleResponse>>builder()
                .code(200)
                .message("Active evaluation cycles retrieved successfully")
                .result(cycles)
                .build());
    }
}
