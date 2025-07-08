package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SummaryAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long summaryAssessmentId;
    private long criteriaFormId;
    private Double averageScore;

    @Column(columnDefinition = "TEXT")
    private String comment; // Bình luận

    @OneToMany(mappedBy = "summaryAssessment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EvaluationAnswers> evaluationAnswers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
