package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationHistoryResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String departmentName;
    private Long formId;
    private String formName;
    private String cycleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; // COMPLETED, IN_PROGRESS, PENDING
    private String comment;
    
    // Thống kê điểm số
    private Double averageScore;
    private Integer totalQuestions;
    private Integer completedQuestions;
    
    // Sentiment analysis
    private String sentiment; // EXCELLENT, GOOD, AVERAGE, POOR
    private String sentimentLabel;
    private String sentimentColor;
    
    // Chi tiết đánh giá
    private List<AssessmentItemResponse> assessmentItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssessmentItemResponse {
        private Long questionId;
        private String questionText;
        private String criteriaName;
        private Integer employeeScore;
        private Integer supervisorScore;
        private Integer managerScore;
        private Integer totalScore;
    }
}
