package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

//    @ManyToMany
//    @JoinColumn(name = "criteria_form_id")
//    private List<CriteriaForm> criteriaForm;

    @OneToMany(mappedBy = "evaluationCriteria")
    private List<EvaluationQuestions> evaluationQuestions;


}
