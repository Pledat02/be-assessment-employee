package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCycleCreateRequest {
    
    @NotBlank(message = "Start date is required")
    @Size(max = 20, message = "Start date must not exceed 20 characters")
    private String startDate;
    
    @NotBlank(message = "End date is required")
    @Size(max = 20, message = "End date must not exceed 20 characters")
    private String endDate;
    
    @Pattern(regexp = "DRAFT|ACTIVE|COMPLETED", message = "Status must be DRAFT, ACTIVE, or COMPLETED")
    @Builder.Default
    private String status = "DRAFT";
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
