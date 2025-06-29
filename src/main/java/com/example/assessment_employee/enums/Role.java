package com.example.assessment_employee.enums;

/**
 * Enum định nghĩa các role trong hệ thống đánh giá nhân viên
 */
public enum Role {
    /**
     * Nhân viên - Có thể tự đánh giá và xem kết quả của mình
     */
    EMPLOYEE("EMPLOYEE", "Nhân viên"),
    
    /**
     * Giám sát - Có thể đánh giá nhân viên dưới quyền và xem báo cáo
     */
    SUPERVISOR("SUPERVISOR", "Giám sát"),
    
    /**
     * Quản lý - Có thể đánh giá cuối cùng, quản lý chu kỳ đánh giá và xem tất cả báo cáo
     */
    MANAGER("MANAGER", "Quản lý");
    
    private final String code;
    private final String description;
    
    Role(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Kiểm tra xem role có quyền quản lý không
     */
    public boolean isManager() {
        return this == MANAGER;
    }
    
    /**
     * Kiểm tra xem role có quyền giám sát không
     */
    public boolean isSupervisor() {
        return this == SUPERVISOR || this == MANAGER;
    }
    
    /**
     * Kiểm tra xem role có quyền đánh giá nhân viên khác không
     */
    public boolean canEvaluateOthers() {
        return this == SUPERVISOR || this == MANAGER;
    }
    
    /**
     * Lấy role từ string
     */
    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return null;
        }
        
        for (Role role : Role.values()) {
            if (role.code.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Invalid role: " + roleStr);
    }
    
    /**
     * Kiểm tra xem string có phải là role hợp lệ không
     */
    public static boolean isValidRole(String roleStr) {
        try {
            fromString(roleStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
