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
public class EvaluationCriteriaResponse {
    
    private Long evaluationCriteriaId;
    private String criteriaName;
    private List<QuestionInfo> evaluationQuestions;
    
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
