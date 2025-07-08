package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.CriteriaFormCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.CriteriaFormResponse;
import com.example.assessment_employee.service.CriteriaFormService;
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
@RequestMapping("/api/criteria-forms")
@RequiredArgsConstructor
@Slf4j
public class CriteriaFormController {
    
    private final CriteriaFormService criteriaFormService;
    
    /**
     * Get all criteria forms with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CriteriaFormResponse>>> getAllCriteriaForms(Pageable pageable) {
        log.info("Get all criteria forms request with pagination");
        
        Page<CriteriaFormResponse> forms = criteriaFormService.getAllCriteriaForms(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<CriteriaFormResponse>>builder()
                .code(200)
                .message("Criteria forms retrieved successfully")
                .result(forms)
                .build());
    }
    
    /**
     * Get all criteria forms without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CriteriaFormResponse>>> getAllCriteriaFormsWithoutPagination() {
        log.info("Get all criteria forms request without pagination");
        
        List<CriteriaFormResponse> forms = criteriaFormService.getAllCriteriaForms();
        
        return ResponseEntity.ok(ApiResponse.<List<CriteriaFormResponse>>builder()
                .code(200)
                .message("Criteria forms retrieved successfully")
                .result(forms)
                .build());
    }
    
    /**
     * Get criteria form by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CriteriaFormResponse>> getCriteriaFormById(@PathVariable Long id) {
        log.info("Get criteria form by ID: {}", id);
        
        CriteriaFormResponse form = criteriaFormService.getCriteriaFormById(id);
        
        return ResponseEntity.ok(ApiResponse.<CriteriaFormResponse>builder()
                .code(200)
                .message("Criteria form retrieved successfully")
                .result(form)
                .build());
    }
    
    /**
     * Get criteria form by name
     */
    @GetMapping("/name/{criteriaFormName}")
    public ResponseEntity<ApiResponse<CriteriaFormResponse>> getCriteriaFormByName(@PathVariable String criteriaFormName) {
        log.info("Get criteria form by name: {}", criteriaFormName);
        
        CriteriaFormResponse form = criteriaFormService.getCriteriaFormByName(criteriaFormName);
        
        return ResponseEntity.ok(ApiResponse.<CriteriaFormResponse>builder()
                .code(200)
                .message("Criteria form retrieved successfully")
                .result(form)
                .build());
    }
    
    /**
     * Create new criteria form
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CriteriaFormResponse>> createCriteriaForm(
            @Valid @RequestBody CriteriaFormCreateRequest request) {
        log.info("Create criteria form request for: {}", request.getCriteriaFormName());
        
        CriteriaFormResponse form = criteriaFormService.createCriteriaForm(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CriteriaFormResponse>builder()
                        .code(201)
                        .message("Criteria form created successfully")
                        .result(form)
                        .build());
    }
    
    /**
     * Update criteria form
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CriteriaFormResponse>> updateCriteriaForm(
            @PathVariable Long id,
            @Valid @RequestBody CriteriaFormCreateRequest request) {
        log.info("Update criteria form request for ID: {}", request);
        
        CriteriaFormResponse form = criteriaFormService.updateCriteriaForm(id, request);
        
        return ResponseEntity.ok(ApiResponse.<CriteriaFormResponse>builder()
                .code(200)
                .message("Criteria form updated successfully")
                .result(form)
                .build());
    }
    
    /**
     * Delete criteria form
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCriteriaForm(@PathVariable Long id) {
        log.info("Delete criteria form request for ID: {}", id);
        
        criteriaFormService.deleteCriteriaForm(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Criteria form deleted successfully")
                .build());
    }
    
    /**
     * Get criteria forms by evaluation cycle ID
     */
    @GetMapping("/evaluation-cycle/{evaluationCycleId}")
    public ResponseEntity<ApiResponse<List<CriteriaFormResponse>>> getCriteriaFormsByEvaluationCycleId(
            @PathVariable String evaluationCycleId) {
        log.info("Get criteria forms by evaluation cycle ID: {}", evaluationCycleId);
        
        List<CriteriaFormResponse> forms = criteriaFormService.getCriteriaFormsByEvaluationCycleId(evaluationCycleId);
        
        return ResponseEntity.ok(ApiResponse.<List<CriteriaFormResponse>>builder()
                .code(200)
                .message("Criteria forms retrieved successfully")
                .result(forms)
                .build());
    }
    
    /**
     * Search criteria forms by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CriteriaFormResponse>>> searchCriteriaFormsByName(
            @RequestParam String keyword) {
        log.info("Search criteria forms by name keyword: {}", keyword);
        
        List<CriteriaFormResponse> forms = criteriaFormService.searchCriteriaFormsByName(keyword);
        
        return ResponseEntity.ok(ApiResponse.<List<CriteriaFormResponse>>builder()
                .code(200)
                .message("Criteria forms retrieved successfully")
                .result(forms)
                .build());
    }
    
    /**
     * Get criteria forms that contain specific criteria
     */
    @GetMapping("/criteria/{criteriaId}")
    public ResponseEntity<ApiResponse<List<CriteriaFormResponse>>> getCriteriaFormsByCriteriaId(
            @PathVariable Long criteriaId) {
        log.info("Get criteria forms that contain criteria ID: {}", criteriaId);
        
        List<CriteriaFormResponse> forms = criteriaFormService.getCriteriaFormsByCriteriaId(criteriaId);
        
        return ResponseEntity.ok(ApiResponse.<List<CriteriaFormResponse>>builder()
                .code(200)
                .message("Criteria forms retrieved successfully")
                .result(forms)
                .build());
    }
    
    /**
     * Get criteria form with full details (criteria and questions)
     */
    @GetMapping("/{id}/full-details")
    public ResponseEntity<ApiResponse<CriteriaFormResponse>> getCriteriaFormWithFullDetails(@PathVariable Long id) {
        log.info("Get criteria form with full details for ID: {}", id);
        
        CriteriaFormResponse form = criteriaFormService.getCriteriaFormWithFullDetails(id);
        
        return ResponseEntity.ok(ApiResponse.<CriteriaFormResponse>builder()
                .code(200)
                .message("Criteria form with full details retrieved successfully")
                .result(form)
                .build());
    }
    
    /**
     * Count criteria forms by evaluation cycle
     */
    @GetMapping("/evaluation-cycle/{evaluationCycleId}/count")
    public ResponseEntity<ApiResponse<Long>> countCriteriaFormsByEvaluationCycle(
            @PathVariable String evaluationCycleId) {
        log.info("Count criteria forms for evaluation cycle ID: {}", evaluationCycleId);
        
        long count = criteriaFormService.countCriteriaFormsByEvaluationCycle(evaluationCycleId);
        
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .code(200)
                .message("Criteria form count retrieved successfully")
                .result(count)
                .build());
    }
}
