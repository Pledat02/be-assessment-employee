package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCreateRequest {
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Size(max = 50, message = "Division must not exceed 50 characters")
    private String division;
    
    @Size(max = 50, message = "Basic must not exceed 50 characters")
    private String basic;
    
    @Size(max = 50, message = "Staff type must not exceed 50 characters")
    private String staffType;
    
    @Size(max = 20, message = "Start date must not exceed 20 characters")
    private String startDate;
    
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
