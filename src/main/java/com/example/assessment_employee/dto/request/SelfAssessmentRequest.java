package com.example.assessment_employee.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelfAssessmentRequest {
    
    @NotNull(message = "Evaluation answer ID is required")
    private Long evaluationAnswerId;
    
    @NotEmpty(message = "At least one assessment item is required")
    @Valid
    private List<AssessmentItem> assessmentItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssessmentItem {
        
        @NotNull(message = "Question ID is required")
        private Long questionId;
        
        @NotNull(message = "Employee score is required")
        private Long employeeScore;
        
        private String employeeComment;
    }
}
