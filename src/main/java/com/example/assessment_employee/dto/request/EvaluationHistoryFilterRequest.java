package com.example.assessment_employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationHistoryFilterRequest {
    
    private String sentiment; // EXCELLENT, GOOD, AVERAGE, POOR
    private String status; // COMPLETED, IN_PROGRESS, PENDING
    private String employeeName;
    private String cycleName;
    private Long employeeId; // For EMPLOYEE role filter
    private Long cycleId;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "createdAt";
    private String direction = "desc";
}
