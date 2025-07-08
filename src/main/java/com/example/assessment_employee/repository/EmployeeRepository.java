package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Find employees by department
     * @param department the department to search for
     * @return List<Employee>
     */
    List<Employee> findByDepartment(Department department);
    
    /**
     * Find employees by department ID
     * @param departmentId the department ID to search for
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e WHERE e.department.departmentId = :departmentId")
    List<Employee> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    /**
     * Find employees by staff type
     * @param staffType the staff type to search for
     * @return List<Employee>
     */
    List<Employee> findByStaffType(String staffType);
    
    /**
     * Find employees by division
     * @param division the division to search for
     * @return List<Employee>
     */
    List<Employee> findByDivision(String division);
    
    /**
     * Find employees by type
     * @param type the type to search for
     * @return List<Employee>
     */
    List<Employee> findByType(String type);
    
    /**
     * Find employee by account ID
     * @param accountId the account ID to search for
     * @return Optional<Employee>
     */
    @Query("SELECT e FROM Employee e WHERE e.account.id = :accountId")
    Optional<Employee> findByAccountId(@Param("accountId") Long accountId);
    
    /**
     * Find employees with account information
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.account")
    List<Employee> findAllWithAccount();
    
    /**
     * Find employees with department information
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
    
    /**
     * Find employees with full information (account + department)
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.account LEFT JOIN FETCH e.department")
    List<Employee> findAllWithFullInfo();
    
    /**
     * Search employees by fullname containing keyword
     * @param keyword the keyword to search for
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchByFullName(@Param("keyword") String keyword);
    
    /**
     * Find employees by department and staff type
     * @param departmentId the department ID
     * @param staffType the staff type
     * @return List<Employee>
     */
    @Query("SELECT e FROM Employee e WHERE e.department.departmentId = :departmentId AND e.staffType = :staffType")
    List<Employee> findByDepartmentIdAndStaffType(@Param("departmentId") Long departmentId, @Param("staffType") String staffType);

    /**
     * Thống kê tổng số nhân viên
     */
    @Query("SELECT COUNT(e) FROM Employee e")
    long countTotalEmployees();

    @Query("""
    SELECT DISTINCT ea.employee FROM EvaluationAnswers ea
""")
    List<Employee> findDistinctEvaluatedEmployees();


}
