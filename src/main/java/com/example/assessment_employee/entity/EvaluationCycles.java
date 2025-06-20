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
public class EvaluationCycles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationCycleId;
    private String startDate;
    private String endDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


}