package com.example.assessment_employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    
    private Long code;
    private String fullName;
    private String division;
    private String basic;
    private String staffType;
    private String startDate;
    private String type;
    private AccountInfo account;
    private DepartmentInfo department;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountInfo {
        private Long id;
        private String username;
        private String role;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentInfo {
        private Long departmentId;
        private String departmentName;
        private String managerCode;
    }
}
