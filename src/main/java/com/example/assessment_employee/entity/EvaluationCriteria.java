package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class EvaluationCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationCriteriaId;
    private String criteriaName;


    @OneToMany(mappedBy = "evaluationCriteria")
    @BatchSize(size = 50)
    private List<EvaluationQuestions> evaluationQuestions;
}
