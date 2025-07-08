package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class EvaluationAnswers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationAnswerId;
    private int totalScoreByEmployee;
    private int totalScoreByManager;
    private int totalScoreBySupervision;
    private int totalScore;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private EvaluationQuestions question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_assessment_id")
    private SummaryAssessment summaryAssessment;

    public int getAVGScore() {
        // Kiểm tra nếu tất cả các điểm số đều <= 0
        if (totalScoreByEmployee <= 0 && totalScoreBySupervision <= 0 && totalScoreByManager <= 0) {
            return 0;
        }

        // Trường hợp chỉ có totalScoreByEmployee > 0
        if (totalScoreByEmployee > 0 && totalScoreBySupervision <= 0 && totalScoreByManager <= 0) {
            return totalScoreByEmployee;
        }

        // Trường hợp chỉ có totalScoreBySupervision > 0
        if (totalScoreByEmployee <= 0 && totalScoreBySupervision > 0 && totalScoreByManager <= 0) {
            return totalScoreBySupervision;
        }

        // Trường hợp chỉ có totalScoreByManager > 0
        if (totalScoreByEmployee <= 0 && totalScoreBySupervision <= 0 && totalScoreByManager > 0) {
            return totalScoreByManager;
        }

        // Trường hợp có hai điểm số > 0
        if (totalScoreByEmployee > 0 && totalScoreBySupervision > 0 && totalScoreByManager <= 0) {
            return (totalScoreByEmployee + totalScoreBySupervision) / 2;
        }
        if (totalScoreByEmployee > 0 && totalScoreByManager > 0 && totalScoreBySupervision <= 0) {
            return (totalScoreByEmployee + totalScoreByManager) / 2;
        }
        if (totalScoreBySupervision > 0 && totalScoreByManager > 0 && totalScoreByEmployee <= 0) {
            return (totalScoreBySupervision + totalScoreByManager) / 2;
        }

        // Trường hợp tất cả ba điểm số > 0
        if (totalScoreByEmployee > 0 && totalScoreBySupervision > 0 && totalScoreByManager > 0) {
            return (totalScoreByEmployee*2 + totalScoreBySupervision*4 + totalScoreByManager*4) / 10;
        }

        // Trường hợp mặc định (nên không bao giờ đạt được nếu logic trên đầy đủ)
        return 0;
    }
}