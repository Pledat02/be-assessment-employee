package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EvaluationQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationQuestionId;
    private String questionName;
    private Long maxScore;

    @ManyToOne
    @JoinColumn(name = "evaluation_criteria_id")
    private EvaluationCriteria evaluationCriteria;

}
