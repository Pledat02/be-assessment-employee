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
public class EvaluationResponse {
    
    private Long evaluationAnswerId;
    private Long totalScore;
    private String totalScoreManager;
    private Long criteriaFormId;
    private String criteriaFormName;
    private EmployeeInfo employee;
    private List<EvaluationDetail> evaluationDetails;
    
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
    public static class EvaluationDetail {
        private Long evaluationQuestionDetailId;
        private QuestionInfo question;
        private Long employeeScore;
        private String employeeComment;
        private Long supervisorScore;
        private String supervisorComment;
        private Long managerScore;
        private String managerComment;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionInfo {
        private Long evaluationQuestionId;
        private String questionName;
        private Long maxScore;
        private String criteriaName;
    }
}
