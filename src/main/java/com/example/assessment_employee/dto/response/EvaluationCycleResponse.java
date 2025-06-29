package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCycleResponse {
    
    private Long evaluationCycleId;
    private String startDate;
    private String endDate;
    private String status;
    private DepartmentInfo department;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentInfo {
        private Long departmentId;
        private String departmentName;
        private String managerCode;
    }
}
