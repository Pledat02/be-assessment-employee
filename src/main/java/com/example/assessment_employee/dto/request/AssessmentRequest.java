package com.example.assessment_employee.dto.request;

import com.example.assessment_employee.entity.Employee;
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
public class AssessmentRequest {
    
    @NotEmpty(message = "At least one assessment item is required")
    @Valid
    private List<AssessmentItem> assessmentItems;
    @NotNull(message = "Employee score is required")
    private long assessorId;
    private String comment;
    private long employeeId;
    private long formId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssessmentItem {
        
        @NotNull(message = "Question ID is required")
        private Long questionId;

        private int employeeScore;
        private int supervisorScore;
        private int managerScore;



    }
}
