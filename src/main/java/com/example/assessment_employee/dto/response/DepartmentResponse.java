package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    
    private Long departmentId;
    private String departmentName;
    private String managerCode;
    private List<EmployeeInfo> employees;
    private List<EvaluationCycleInfo> evaluationCycles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeInfo {
        private Long code;
        private String fullName;
        private String division;
        private String staffType;
        private String type;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EvaluationCycleInfo {
        private Long evaluationCycleId;
        private String startDate;
        private String endDate;
        private String status;
    }
}
