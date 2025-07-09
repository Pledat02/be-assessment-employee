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
public class CycleStatisticsResponse {
    
    private String cycleName;
    private Integer totalEvaluations;
    private Integer completedEvaluations;
    private Double averageScore;
    private List<CriteriaChartDataResponse> criteriaData;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CriteriaChartDataResponse {
        private String criteriaName;
        private Double averageScore;
        private Double employeeScore;
        private Double supervisorScore;
        private Double managerScore;
        private String color;
    }
}
