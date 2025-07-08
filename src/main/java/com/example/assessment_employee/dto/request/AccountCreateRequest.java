package com.example.assessment_employee.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 2, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "EMPLOYEE|SUPERVISOR|MANAGER", message = "Role must be EMPLOYEE, SUPERVISOR, or MANAGER")
    private String role;
    
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    @Builder.Default
    private String status = "ACTIVE";
}
