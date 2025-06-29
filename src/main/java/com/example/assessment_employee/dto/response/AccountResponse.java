package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    
    private Long id;
    private String username;
    private String role;
    private String status;
    private EmployeeInfo employee;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmployeeInfo {
        private Long code;
        private String fullName;
        private String division;
        private String basic;
        private String staffType;
        private String startDate;
        private String type;
        private String departmentName;
    }
}
