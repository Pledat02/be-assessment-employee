package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationStatisticsResponse {
    private long totalEmployees;
    private long evaluatedEmployees;
    private double averageScore;
    private long excellentEmployees;
}
