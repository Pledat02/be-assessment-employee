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
    private Long totalScore;
    private String totalScoreManager;
    private long criteriaFormId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;


}