package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.EvaluationCriteriaCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.EvaluationCriteriaResponse;
import com.example.assessment_employee.service.EvaluationCriteriaService;
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
@RequestMapping("/api/evaluation-criteria")
@RequiredArgsConstructor
@Slf4j
public class EvaluationCriteriaController {
    
    private final EvaluationCriteriaService evaluationCriteriaService;
    
    /**
     * Get all evaluation criteria with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluationCriteriaResponse>>> getAllEvaluationCriteria(Pageable pageable) {
        log.info("Get all evaluation criteria request with pagination");
        
        Page<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getAllEvaluationCriteria(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Evaluation criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get all evaluation criteria without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getAllEvaluationCriteriaWithoutPagination() {
        log.info("Get all evaluation criteria request without pagination");
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getAllEvaluationCriteria();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Evaluation criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get evaluation criteria by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getEvaluationCriteriaById(@PathVariable Long id) {
        log.info("Get evaluation criteria by ID: {}", id);
        
        EvaluationCriteriaResponse criteria = evaluationCriteriaService.getEvaluationCriteriaById(id);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCriteriaResponse>builder()
                .code(200)
                .message("Evaluation criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get evaluation criteria by name
     */
    @GetMapping("/name/{criteriaName}")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getEvaluationCriteriaByName(@PathVariable String criteriaName) {
        log.info("Get evaluation criteria by name: {}", criteriaName);
        
        EvaluationCriteriaResponse criteria = evaluationCriteriaService.getEvaluationCriteriaByName(criteriaName);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCriteriaResponse>builder()
                .code(200)
                .message("Evaluation criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Create new evaluation criteria
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> createEvaluationCriteria(
            @Valid @RequestBody EvaluationCriteriaCreateRequest request) {
        log.info("Create evaluation criteria request for: {}", request.getCriteriaName());
        
        EvaluationCriteriaResponse criteria = evaluationCriteriaService.createEvaluationCriteria(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EvaluationCriteriaResponse>builder()
                        .code(201)
                        .message("Evaluation criteria created successfully")
                        .result(criteria)
                        .build());
    }
    
    /**
     * Update evaluation criteria
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> updateEvaluationCriteria(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCriteriaCreateRequest request) {
        log.info("Update evaluation criteria request for ID: {}", id);
        
        EvaluationCriteriaResponse criteria = evaluationCriteriaService.updateEvaluationCriteria(id, request);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationCriteriaResponse>builder()
                .code(200)
                .message("Evaluation criteria updated successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Delete evaluation criteria
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluationCriteria(@PathVariable Long id) {
        log.info("Delete evaluation criteria request for ID: {}", id);
        
        evaluationCriteriaService.deleteEvaluationCriteria(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Evaluation criteria deleted successfully")
                .build());
    }
    
    /**
     * Search evaluation criteria by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> searchEvaluationCriteriaByName(
            @RequestParam String keyword) {
        log.info("Search evaluation criteria by name keyword: {}", keyword);
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.searchEvaluationCriteriaByName(keyword);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Evaluation criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get criteria with questions
     */
    @GetMapping("/with-questions")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getCriteriaWithQuestions() {
        log.info("Get criteria that have questions");
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getCriteriaWithQuestions();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Criteria with questions retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get criteria without questions
     */
    @GetMapping("/without-questions")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getCriteriaWithoutQuestions() {
        log.info("Get criteria that have no questions");
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getCriteriaWithoutQuestions();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Criteria without questions retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get criteria used in forms
     */
    @GetMapping("/used-in-forms")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getCriteriaUsedInForms() {
        log.info("Get criteria that are used in forms");
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getCriteriaUsedInForms();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Criteria used in forms retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get criteria not used in any form
     */
    @GetMapping("/not-used-in-forms")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getCriteriaNotUsedInForms() {
        log.info("Get criteria that are not used in any form");
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getCriteriaNotUsedInForms();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Criteria not used in forms retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get criteria by IDs
     */
    @PostMapping("/by-ids")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getCriteriaByIds(
            @RequestBody List<Long> criteriaIds) {
        log.info("Get criteria by IDs: {}", criteriaIds);
        
        List<EvaluationCriteriaResponse> criteria = evaluationCriteriaService.getCriteriaByIds(criteriaIds);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationCriteriaResponse>>builder()
                .code(200)
                .message("Criteria retrieved successfully")
                .result(criteria)
                .build());
    }
    
    /**
     * Get question count for criteria
     */
    @GetMapping("/{id}/question-count")
    public ResponseEntity<ApiResponse<Long>> getQuestionCountForCriteria(@PathVariable Long id) {
        log.info("Get question count for criteria ID: {}", id);
        
        long count = evaluationCriteriaService.getQuestionCountForCriteria(id);
        
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .code(200)
                .message("Question count retrieved successfully")
                .result(count)
                .build());
    }
}
