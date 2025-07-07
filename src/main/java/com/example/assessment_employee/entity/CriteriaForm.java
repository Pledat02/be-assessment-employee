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
public class CriteriaForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long criteriaFormId;
    private String evaluationCycleId;
    private String criteriaFormName;

    @ManyToMany
    private List<EvaluationCriteria> evaluationCriteria;
}
