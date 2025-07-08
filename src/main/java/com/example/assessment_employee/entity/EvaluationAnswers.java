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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private EvaluationQuestions question;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "summary_assessment_id")
    private SummaryAssessment summaryAssessment;

}