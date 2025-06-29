package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCriteriaCreateRequest {
    
    @NotBlank(message = "Criteria name is required")
    @Size(min = 2, max = 100, message = "Criteria name must be between 2 and 100 characters")
    private String criteriaName;
}
