package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationQuestionResponse {
    
    private Long evaluationQuestionId;
    private String questionName;
    private Long maxScore;
    private EvaluationCriteriaInfo evaluationCriteria;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EvaluationCriteriaInfo {
        private Long evaluationCriteriaId;
        private String criteriaName;
    }
}
