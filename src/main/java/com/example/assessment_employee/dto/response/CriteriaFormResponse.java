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
public class CriteriaFormResponse {
    
    private Long criteriaFormId;
    private String criteriaFormName;
    private String evaluationCycleId;
    private List<EvaluationCriteriaInfo> evaluationCriteria;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EvaluationCriteriaInfo {
        private Long evaluationCriteriaId;
        private String criteriaName;
        private List<QuestionInfo> questions;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionInfo {
        private Long evaluationQuestionId;
        private String questionName;
        private Long maxScore;
    }
}
