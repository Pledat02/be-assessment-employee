package com.example.assessment_employee.constants;

/**
 * Constants cho các role trong hệ thống
 * Sử dụng cho @PreAuthorize và các annotation khác
 */
public final class RoleConstants {
    
    // Role names
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String SUPERVISOR = "SUPERVISOR";
    public static final String MANAGER = "MANAGER";
    
    // Role expressions for @PreAuthorize
    public static final String HAS_ROLE_EMPLOYEE = "hasAuthority('EMPLOYEE')";
    public static final String HAS_ROLE_SUPERVISOR = "hasAuthority('SUPERVISOR')";
    public static final String HAS_ROLE_MANAGER = "hasAuthority('MANAGER')";
    
    // Combined role expressions
    public static final String HAS_ROLE_SUPERVISOR_OR_MANAGER = "hasAnyAuthority('SUPERVISOR', 'MANAGER')";
    public static final String HAS_ROLE_EMPLOYEE_OR_SUPERVISOR = "hasAnyAuthority('EMPLOYEE', 'SUPERVISOR')";
    public static final String HAS_ANY_ROLE = "hasAnyAuthority('EMPLOYEE', 'SUPERVISOR', 'MANAGER')";
    
    // Prevent instantiation
    private RoleConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
