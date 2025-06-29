package com.example.assessment_employee.enums;

/**
 * Enum định nghĩa trạng thái tài khoản
 */
public enum AccountStatus {
    /**
     * Tài khoản đang hoạt động
     */
    ACTIVE("ACTIVE", "Hoạt động"),
    
    /**
     * Tài khoản bị vô hiệu hóa
     */
    INACTIVE("INACTIVE", "Không hoạt động");
    
    private final String code;
    private final String description;
    
    AccountStatus(String code, String description) {
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
     * Lấy status từ string
     */
    public static AccountStatus fromString(String statusStr) {
        if (statusStr == null) {
            return null;
        }
        
        for (AccountStatus status : AccountStatus.values()) {
            if (status.code.equalsIgnoreCase(statusStr)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid account status: " + statusStr);
    }
    
    /**
     * Kiểm tra xem string có phải là status hợp lệ không
     */
    public static boolean isValidStatus(String statusStr) {
        try {
            fromString(statusStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
