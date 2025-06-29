package com.example.assessment_employee.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    
    @NotNull(message = "Evaluation answer ID is required")
    private Long evaluationAnswerId;
    
    @NotNull(message = "Reviewer type is required")
    @Pattern(regexp = "SUPERVISOR|MANAGER", message = "Reviewer type must be SUPERVISOR or MANAGER")
    private String reviewerType;
    
    @NotEmpty(message = "At least one review item is required")
    @Valid
    private List<ReviewItem> reviewItems;
    
    // Only for MANAGER review
    private String totalScoreManager;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewItem {
        
        @NotNull(message = "Question ID is required")
        private Long questionId;
        
        @NotNull(message = "Score is required")
        private Long score;
        
        private String comment;
    }
}
