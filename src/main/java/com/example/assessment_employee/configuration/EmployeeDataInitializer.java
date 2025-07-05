package com.example.assessment_employee.configuration;

import com.example.assessment_employee.entity.Account;
import com.example.assessment_employee.entity.Department;
import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.repository.AccountRepository;
import com.example.assessment_employee.repository.DepartmentRepository;
import com.example.assessment_employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Employee Data Initializer - Khởi tạo dữ liệu mẫu nhân viên và tài khoản
 * Chạy một lần khi ứng dụng khởi động
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🚀 Starting employee data initialization...");
        
        // Kiểm tra xem đã có dữ liệu nhân viên chưa
        if (employeeRepository.count() > 0) {
            log.info("✅ Employee data already exists, skipping initialization");
            return;
        }

        try {
            initializeDepartments();
            initializeEmployeesAndAccounts();
            
            log.info("🎉 Employee data initialization completed successfully!");
        } catch (Exception e) {
            log.error("❌ Error during employee data initialization: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Khởi tạo các phòng ban
     */
    private void initializeDepartments() {
        log.info("📁 Initializing departments...");
        
        // Kiểm tra xem đã có phòng ban chưa
        if (departmentRepository.count() > 0) {
            log.info("✅ Departments already exist, skipping department initialization");
            return;
        }
        
        List<Department> departments = Arrays.asList(
            createDepartment("Phòng Nhân sự", "HR001"),
            createDepartment("Phòng Công nghệ thông tin", "IT001"),
            createDepartment("Phòng Kinh doanh", "SALES001"),
            createDepartment("Phòng Kế toán", "ACC001"),
            createDepartment("Phòng Marketing", "MKT001")
        );
        
        departmentRepository.saveAll(departments);
        log.info("✅ Created {} departments", departments.size());
    }

    /**
     * Khởi tạo nhân viên và tài khoản
     */
    private void initializeEmployeesAndAccounts() {
        log.info("👥 Initializing employees and accounts...");
        
        List<Department> departments = departmentRepository.findAll();
        
        // Admin account
        createEmployeeWithAccount(
            "Quản trị viên hệ thống", "Quản trị", "Thạc sĩ CNTT", 
            "Quản lý", "2020-01-15", "Toàn thời gian",
            departments.get(0), // HR Department
            "admin", "admin123", "MANAGER", "ACTIVE"
        );
        
        // HR employees
        createEmployeeWithAccount(
            "Nguyễn Văn An", "Nhân sự", "Cử nhân Quản trị", 
            "Trưởng phòng", "2021-03-01", "Toàn thời gian",
            departments.get(0), // HR Department
            "hr.manager", "hr123", "MANAGER", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Trần Thị Bình", "Nhân sự", "Cử nhân Tâm lý", 
            "Chuyên viên", "2022-06-15", "Toàn thời gian",
            departments.get(0), // HR Department
            "hr.staff", "hr123", "EMPLOYEE", "ACTIVE"
        );
        
        // IT employees
        createEmployeeWithAccount(
            "Lê Văn Cường", "Công nghệ", "Thạc sĩ CNTT", 
            "Trưởng phòng", "2020-08-01", "Toàn thời gian",
            departments.get(1), // IT Department
            "it.manager", "it123", "MANAGER", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Phạm Thị Dung", "Công nghệ", "Cử nhân CNTT", 
            "Lập trình viên Senior", "2021-01-15", "Toàn thời gian",
            departments.get(1), // IT Department
            "dev.senior", "dev123", "EMPLOYEE", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Hoàng Văn Em", "Công nghệ", "Cử nhân CNTT", 
            "Lập trình viên Junior", "2023-03-01", "Toàn thời gian",
            departments.get(1), // IT Department
            "dev.junior", "dev123", "EMPLOYEE", "ACTIVE"
        );
        
        // Sales employees
        createEmployeeWithAccount(
            "Vũ Thị Giang", "Kinh doanh", "Cử nhân Kinh tế", 
            "Trưởng phòng", "2021-05-01", "Toàn thời gian",
            departments.get(2), // Sales Department
            "sales.manager", "sales123", "MANAGER", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Đỗ Văn Hùng", "Kinh doanh", "Cử nhân Marketing", 
            "Nhân viên kinh doanh", "2022-09-01", "Toàn thời gian",
            departments.get(2), // Sales Department
            "sales.staff", "sales123", "EMPLOYEE", "ACTIVE"
        );
        
        // Accounting employees
        createEmployeeWithAccount(
            "Ngô Thị Lan", "Kế toán", "Cử nhân Kế toán", 
            "Trưởng phòng", "2020-11-01", "Toàn thời gian",
            departments.get(3), // Accounting Department
            "acc.manager", "acc123", "MANAGER", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Bùi Văn Minh", "Kế toán", "Cử nhân Tài chính", 
            "Kế toán viên", "2022-02-01", "Toàn thời gian",
            departments.get(3), // Accounting Department
            "acc.staff", "acc123", "EMPLOYEE", "ACTIVE"
        );
        
        // Marketing employees
        createEmployeeWithAccount(
            "Đinh Thị Nga", "Marketing", "Cử nhân Marketing", 
            "Trưởng phòng", "2021-07-01", "Toàn thời gian",
            departments.get(4), // Marketing Department
            "mkt.manager", "mkt123", "MANAGER", "ACTIVE"
        );
        
        createEmployeeWithAccount(
            "Lý Văn Phong", "Marketing", "Cử nhân Truyền thông", 
            "Chuyên viên Marketing", "2023-01-15", "Toàn thời gian",
            departments.get(4), // Marketing Department
            "mkt.staff", "mkt123", "EMPLOYEE", "ACTIVE"
        );
        
        // Test inactive employee
        createEmployeeWithAccount(
            "Trần Văn Test", "Thử nghiệm", "Cử nhân", 
            "Nhân viên thử nghiệm", "2023-12-01", "Bán thời gian",
            departments.get(0), // HR Department
            "test.inactive", "test123", "EMPLOYEE", "INACTIVE"
        );
        
        log.info("✅ Created employees and accounts");
    }

    // Helper methods
    private Department createDepartment(String name, String managerCode) {
        return Department.builder()
                .departmentName(name)
                .managerCode(managerCode)
                .build();
    }

    private void createEmployeeWithAccount(String fullName, String division, String basic,
                                         String staffType, String startDate, String type,
                                         Department department, String username, String password, 
                                         String role, String status) {
        // Tạo Account trước
        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .status(status)
                .build();
        
        Account savedAccount = accountRepository.save(account);
        
        // Tạo Employee và liên kết với Account
        Employee employee = Employee.builder()
                .fullName(fullName)
                .division(division)
                .basic(basic)
                .staffType(staffType)
                .startDate(startDate)
                .type(type)
                .account(savedAccount)
                .department(department)
                .build();
        
        employeeRepository.save(employee);
        
        log.debug("Created employee: {} with account: {}", fullName, username);
    }
}
