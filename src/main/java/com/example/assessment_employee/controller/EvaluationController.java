package com.example.assessment_employee.controller;

import com.example.assessment_employee.constants.RoleConstants;
import com.example.assessment_employee.dto.request.EvaluationStartRequest;
import com.example.assessment_employee.dto.request.ReviewRequest;
import com.example.assessment_employee.dto.request.SelfAssessmentRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.EvaluationResponse;
import com.example.assessment_employee.dto.response.EvaluationSummaryResponse;
import com.example.assessment_employee.service.EvaluationService;
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
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Slf4j
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    /**
     * Start new evaluation for employee
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<EvaluationResponse>> startEvaluation(
            @Valid @RequestBody EvaluationStartRequest request) {
        log.info("Start evaluation request for employee: {} with criteria form: {}", 
                request.getEmployeeCode(), request.getCriteriaFormId());
        
        EvaluationResponse evaluation = evaluationService.startEvaluation(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EvaluationResponse>builder()
                        .code(201)
                        .message("Evaluation started successfully")
                        .result(evaluation)
                        .build());
    }
    
    /**
     * Get evaluations for current employee (self evaluations)
     */
    @GetMapping("/my-evaluations")
    public ResponseEntity<ApiResponse<Page<EvaluationResponse>>> getSelfEvaluations(
            @RequestParam Long employeeCode,
            Pageable pageable) {
        log.info("Get self evaluations request for employee: {}", employeeCode);
        
        Page<EvaluationResponse> evaluations = evaluationService.getSelfEvaluations(employeeCode, pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<EvaluationResponse>>builder()
                .code(200)
                .message("Self evaluations retrieved successfully")
                .result(evaluations)
                .build());
    }
    
    /**
     * Get evaluations pending review (for supervisors/managers)
     */
    @GetMapping("/pending-reviews")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getPendingReviews(
            @RequestParam Long reviewerEmployeeCode,
            @RequestParam String reviewerRole) {
        log.info("Get pending reviews request for reviewer: {} with role: {}", reviewerEmployeeCode, reviewerRole);
        
        List<EvaluationResponse> evaluations = evaluationService.getPendingReviews(reviewerEmployeeCode, reviewerRole);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationResponse>>builder()
                .code(200)
                .message("Pending reviews retrieved successfully")
                .result(evaluations)
                .build());
    }
    
    /**
     * Get evaluation detail by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationResponse>> getEvaluationDetail(@PathVariable Long id) {
        log.info("Get evaluation detail request for ID: {}", id);
        
        EvaluationResponse evaluation = evaluationService.getEvaluationDetail(id);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationResponse>builder()
                .code(200)
                .message("Evaluation detail retrieved successfully")
                .result(evaluation)
                .build());
    }
    
    /**
     * Submit self assessment
     */
    @PutMapping("/{id}/self-assess")
    public ResponseEntity<ApiResponse<EvaluationResponse>> submitSelfAssessment(
            @PathVariable Long id,
            @Valid @RequestBody SelfAssessmentRequest request) {
        log.info("Submit self assessment request for evaluation: {}", id);
        
        // Set evaluation ID from path parameter
        request.setEvaluationAnswerId(id);
        
        EvaluationResponse evaluation = evaluationService.submitSelfAssessment(request);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationResponse>builder()
                .code(200)
                .message("Self assessment submitted successfully")
                .result(evaluation)
                .build());
    }
    
    /**
     * Submit supervisor/manager review
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<ApiResponse<EvaluationResponse>> submitReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        log.info("Submit {} review request for evaluation: {}", request.getReviewerType(), id);
        
        // Set evaluation ID from path parameter
        request.setEvaluationAnswerId(id);
        
        EvaluationResponse evaluation = evaluationService.submitReview(request);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationResponse>builder()
                .code(200)
                .message(request.getReviewerType() + " review submitted successfully")
                .result(evaluation)
                .build());
    }
    
    /**
     * Get evaluation summary with statistics
     */
    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<EvaluationSummaryResponse>> getEvaluationSummary(@PathVariable Long id) {
        log.info("Get evaluation summary request for ID: {}", id);
        
        EvaluationSummaryResponse summary = evaluationService.getEvaluationSummary(id);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationSummaryResponse>builder()
                .code(200)
                .message("Evaluation summary retrieved successfully")
                .result(summary)
                .build());
    }
    
    /**
     * Delete evaluation (only if not completed)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluation(@PathVariable Long id) {
        log.info("Delete evaluation request for ID: {}", id);
        
        evaluationService.deleteEvaluation(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Evaluation deleted successfully")
                .build());
    }
    
    /**
     * Get evaluations by employee
     */
    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getEvaluationsByEmployee(
            @PathVariable Long employeeCode) {
        log.info("Get evaluations by employee code: {}", employeeCode);
        
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsByEmployee(employeeCode);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationResponse>>builder()
                .code(200)
                .message("Evaluations retrieved successfully")
                .result(evaluations)
                .build());
    }
    
    /**
     * Get completed evaluations
     */
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getCompletedEvaluations() {
        log.info("Get completed evaluations request");
        
        List<EvaluationResponse> evaluations = evaluationService.getCompletedEvaluations();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationResponse>>builder()
                .code(200)
                .message("Completed evaluations retrieved successfully")
                .result(evaluations)
                .build());
    }
    
    /**
     * Get evaluations by department
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getEvaluationsByDepartment(
            @PathVariable Long departmentId) {
        log.info("Get evaluations by department ID: {}", departmentId);
        
        List<EvaluationResponse> evaluations = evaluationService.getEvaluationsByDepartment(departmentId);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationResponse>>builder()
                .code(200)
                .message("Evaluations retrieved successfully")
                .result(evaluations)
                .build());
    }
}
