package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationQuestionCreateRequest {
    
    @NotBlank(message = "Question name is required")
    @Size(min = 5, max = 500, message = "Question name must be between 5 and 500 characters")
    private String questionName;
    
    @NotNull(message = "Max score is required")
    @Min(value = 1, message = "Max score must be at least 1")
    private Long maxScore;
    
    @NotNull(message = "Evaluation criteria ID is required")
    private Long evaluationCriteriaId;
}
