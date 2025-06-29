package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriteriaFormCreateRequest {
    
    @NotBlank(message = "Criteria form name is required")
    @Size(min = 2, max = 100, message = "Criteria form name must be between 2 and 100 characters")
    private String criteriaFormName;
    
    @NotBlank(message = "Evaluation cycle ID is required")
    private String evaluationCycleId;
    
    @NotEmpty(message = "At least one evaluation criteria ID is required")
    private List<Long> evaluationCriteriaIds;
}
