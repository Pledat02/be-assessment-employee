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
public class EvaluationSummaryResponse {
    
    private Long evaluationAnswerId;
    private EmployeeInfo employee;
    private CriteriaFormInfo criteriaForm;
    private ScoreSummary scoreSummary;
    private List<CriteriaSummary> criteriaSummaries;
    private String evaluationStatus;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeInfo {
        private Long code;
        private String fullName;
        private String division;
        private String staffType;
        private String departmentName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CriteriaFormInfo {
        private Long criteriaFormId;
        private String criteriaFormName;
        private String evaluationCycleId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreSummary {
        private Long totalScore;
        private String totalScoreManager;
        private Double averageEmployeeScore;
        private Double averageSupervisorScore;
        private Double averageManagerScore;
        private Long totalQuestions;
        private Long completedQuestions;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CriteriaSummary {
        private String criteriaName;
        private Long totalQuestions;
        private Double averageEmployeeScore;
        private Double averageSupervisorScore;
        private Double averageManagerScore;
        private Long maxPossibleScore;
    }
}
