package com.example.assessment_employee.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopEmployeeResponse {
    private int rank;
    private String fullName;
    private String position;
    private String department;
    private double averageScore;
    private String classification;
    private double scoreDiffFromLast;
    private String avatarUrl;
}
