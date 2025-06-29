package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    /**
     * Find department by manager code
     * @param managerCode the manager code to search for
     * @return Optional<Department>
     */
    Optional<Department> findByManagerCode(String managerCode);
    
    /**
     * Find departments by department name containing keyword
     * @param keyword the keyword to search for
     * @return List<Department>
     */
    @Query("SELECT d FROM Department d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Department> searchByDepartmentName(@Param("keyword") String keyword);
    
    /**
     * Find department by exact name
     * @param departmentName the department name to search for
     * @return Optional<Department>
     */
    Optional<Department> findByDepartmentName(String departmentName);
    
    /**
     * Find departments with employees
     * @return List<Department>
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
    List<Department> findAllWithEmployees();
    
    /**
     * Find departments with evaluation cycles
     * @return List<Department>
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.evaluationCycles")
    List<Department> findAllWithEvaluationCycles();
    
    /**
     * Find departments with full information (employees + evaluation cycles)
     * @return List<Department>
     */
    @Query("SELECT DISTINCT d FROM Department d " +
           "LEFT JOIN FETCH d.employees " +
           "LEFT JOIN FETCH d.evaluationCycles")
    List<Department> findAllWithFullInfo();
    
    /**
     * Find department by ID with employees
     * @param departmentId the department ID
     * @return Optional<Department>
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.departmentId = :departmentId")
    Optional<Department> findByIdWithEmployees(@Param("departmentId") Long departmentId);
    
    /**
     * Find department by ID with evaluation cycles
     * @param departmentId the department ID
     * @return Optional<Department>
     */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.evaluationCycles WHERE d.departmentId = :departmentId")
    Optional<Department> findByIdWithEvaluationCycles(@Param("departmentId") Long departmentId);
    
    /**
     * Check if department name exists
     * @param departmentName the department name to check
     * @return boolean
     */
    boolean existsByDepartmentName(String departmentName);
    
    /**
     * Check if manager code exists
     * @param managerCode the manager code to check
     * @return boolean
     */
    boolean existsByManagerCode(String managerCode);
}
