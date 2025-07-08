package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.DepartmentCreateRequest;
import com.example.assessment_employee.dto.request.EvaluationStartRequest;
import com.example.assessment_employee.dto.request.ReviewRequest;
import com.example.assessment_employee.dto.request.AssessmentRequest;
import com.example.assessment_employee.dto.response.*;
import com.example.assessment_employee.entity.SummaryAssessment;
import com.example.assessment_employee.service.EvaluationService;
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
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Slf4j
public class EvaluationController {
    
    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<ApiResponse<SummaryAssessmentResponse>> create(@Valid @RequestBody AssessmentRequest request) {

        SummaryAssessmentResponse submitAssessment = evaluationService.submitAssessment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<SummaryAssessmentResponse>builder()
                        .code(201)
                        .message("submit Assessment created successfully")
                        .result(submitAssessment)
                        .build());
    }
    @GetMapping("/{formId}/{employeeId}")
    public ResponseEntity<ApiResponse<SummaryAssessmentResponse>> getAssessment(
            @PathVariable Long formId,
            @PathVariable Long employeeId) {

        SummaryAssessmentResponse response = evaluationService.getAssessmentByFormAndEmployeeId(formId, employeeId);

        return ResponseEntity.ok(
                ApiResponse.<SummaryAssessmentResponse>builder()
                        .code(200)
                        .message("Assessment retrieved successfully")
                        .result(response)
                        .build()
        );
    }
}
