package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.EvaluationQuestionCreateRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.EvaluationQuestionResponse;
import com.example.assessment_employee.service.EvaluationQuestionService;
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
@RequestMapping("/api/evaluation-questions")
@RequiredArgsConstructor
@Slf4j
public class EvaluationQuestionController {
    
    private final EvaluationQuestionService evaluationQuestionService;
    
    /**
     * Get all evaluation questions with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluationQuestionResponse>>> getAllEvaluationQuestions(Pageable pageable) {
        log.info("Get all evaluation questions request with pagination");
        
        Page<EvaluationQuestionResponse> questions = evaluationQuestionService.getAllEvaluationQuestions(pageable);
        
        return ResponseEntity.ok(ApiResponse.<Page<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get all evaluation questions without pagination
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getAllEvaluationQuestionsWithoutPagination() {
        log.info("Get all evaluation questions request without pagination");
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getAllEvaluationQuestions();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get evaluation question by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationQuestionResponse>> getEvaluationQuestionById(@PathVariable Long id) {
        log.info("Get evaluation question by ID: {}", id);
        
        EvaluationQuestionResponse question = evaluationQuestionService.getEvaluationQuestionById(id);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationQuestionResponse>builder()
                .code(200)
                .message("Evaluation question retrieved successfully")
                .result(question)
                .build());
    }
    
    /**
     * Create new evaluation question
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationQuestionResponse>> createEvaluationQuestion(
            @Valid @RequestBody EvaluationQuestionCreateRequest request) {
        log.info("Create evaluation question request for criteria ID: {}", request.getEvaluationCriteriaId());
        
        EvaluationQuestionResponse question = evaluationQuestionService.createEvaluationQuestion(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EvaluationQuestionResponse>builder()
                        .code(201)
                        .message("Evaluation question created successfully")
                        .result(question)
                        .build());
    }
    
    /**
     * Update evaluation question
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationQuestionResponse>> updateEvaluationQuestion(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationQuestionCreateRequest request) {
        log.info("Update evaluation question request for ID: {}", id);
        
        EvaluationQuestionResponse question = evaluationQuestionService.updateEvaluationQuestion(id, request);
        
        return ResponseEntity.ok(ApiResponse.<EvaluationQuestionResponse>builder()
                .code(200)
                .message("Evaluation question updated successfully")
                .result(question)
                .build());
    }
    
    /**
     * Delete evaluation question
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluationQuestion(@PathVariable Long id) {
        log.info("Delete evaluation question request for ID: {}", id);
        
        evaluationQuestionService.deleteEvaluationQuestion(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200)
                .message("Evaluation question deleted successfully")
                .build());
    }
    
    /**
     * Get questions by criteria ID
     */
    @GetMapping("/criteria/{criteriaId}")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getQuestionsByCriteriaId(
            @PathVariable Long criteriaId) {
        log.info("Get evaluation questions by criteria ID: {}", criteriaId);
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getQuestionsByCriteriaId(criteriaId);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get questions by multiple criteria IDs
     */
    @PostMapping("/criteria/by-ids")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getQuestionsByCriteriaIds(
            @RequestBody List<Long> criteriaIds) {
        log.info("Get evaluation questions by criteria IDs: {}", criteriaIds);
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getQuestionsByCriteriaIds(criteriaIds);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Search questions by name
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> searchQuestionsByName(
            @RequestParam String keyword) {
        log.info("Search evaluation questions by name keyword: {}", keyword);
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.searchQuestionsByName(keyword);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get questions by max score
     */
    @GetMapping("/max-score/{maxScore}")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getQuestionsByMaxScore(
            @PathVariable Long maxScore) {
        log.info("Get evaluation questions by max score: {}", maxScore);
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getQuestionsByMaxScore(maxScore);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get questions by max score range
     */
    @GetMapping("/max-score-range")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getQuestionsByMaxScoreRange(
            @RequestParam Long minScore,
            @RequestParam Long maxScore) {
        log.info("Get evaluation questions by max score range: {} to {}", minScore, maxScore);
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getQuestionsByMaxScoreRange(minScore, maxScore);
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Get all questions ordered by criteria and name
     */
    @GetMapping("/ordered")
    public ResponseEntity<ApiResponse<List<EvaluationQuestionResponse>>> getAllQuestionsOrdered() {
        log.info("Get all evaluation questions ordered by criteria and name");
        
        List<EvaluationQuestionResponse> questions = evaluationQuestionService.getAllQuestionsOrdered();
        
        return ResponseEntity.ok(ApiResponse.<List<EvaluationQuestionResponse>>builder()
                .code(200)
                .message("Evaluation questions retrieved successfully")
                .result(questions)
                .build());
    }
    
    /**
     * Count questions by criteria ID
     */
    @GetMapping("/criteria/{criteriaId}/count")
    public ResponseEntity<ApiResponse<Long>> countQuestionsByCriteriaId(@PathVariable Long criteriaId) {
        log.info("Count questions for criteria ID: {}", criteriaId);
        
        long count = evaluationQuestionService.countQuestionsByCriteriaId(criteriaId);
        
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .code(200)
                .message("Question count retrieved successfully")
                .result(count)
                .build());
    }
}
