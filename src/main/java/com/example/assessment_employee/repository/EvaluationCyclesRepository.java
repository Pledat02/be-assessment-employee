package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.EvaluationCycles;
import com.example.assessment_employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationCyclesRepository extends JpaRepository<EvaluationCycles, Long> {
    
    /**
     * Find evaluation cycles by department
     * @param department the department to search for
     * @return List<EvaluationCycles>
     */
    List<EvaluationCycles> findByDepartment(Department department);
    
    /**
     * Find evaluation cycles by department ID
     * @param departmentId the department ID to search for
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.department.departmentId = :departmentId")
    List<EvaluationCycles> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    /**
     * Find evaluation cycles by status
     * @param status the status to search for
     * @return List<EvaluationCycles>
     */
    List<EvaluationCycles> findByStatus(String status);
    
    /**
     * Find evaluation cycles by department and status
     * @param department the department
     * @param status the status
     * @return List<EvaluationCycles>
     */
    List<EvaluationCycles> findByDepartmentAndStatus(Department department, String status);
    
    /**
     * Find evaluation cycles by department ID and status
     * @param departmentId the department ID
     * @param status the status
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.department.departmentId = :departmentId AND ec.status = :status")
    List<EvaluationCycles> findByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, @Param("status") String status);
    
    /**
     * Find active evaluation cycles (status = 'ACTIVE')
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.status = 'ACTIVE'")
    List<EvaluationCycles> findActiveCycles();
    
    /**
     * Find completed evaluation cycles (status = 'COMPLETED')
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.status = 'COMPLETED'")
    List<EvaluationCycles> findCompletedCycles();
    
    /**
     * Find draft evaluation cycles (status = 'DRAFT')
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.status = 'DRAFT'")
    List<EvaluationCycles> findDraftCycles();
    
    /**
     * Find evaluation cycles with department information
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec LEFT JOIN FETCH ec.department")
    List<EvaluationCycles> findAllWithDepartment();
    
    /**
     * Find evaluation cycle by ID with department information
     * @param cycleId the cycle ID
     * @return Optional<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec LEFT JOIN FETCH ec.department WHERE ec.evaluationCycleId = :cycleId")
    Optional<EvaluationCycles> findByIdWithDepartment(@Param("cycleId") Long cycleId);
    
    /**
     * Find active cycles by department ID
     * @param departmentId the department ID
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.department.departmentId = :departmentId AND ec.status = 'ACTIVE'")
    List<EvaluationCycles> findActiveCyclesByDepartmentId(@Param("departmentId") Long departmentId);
    
    /**
     * Find cycles by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return List<EvaluationCycles>
     */
    @Query("SELECT ec FROM EvaluationCycles ec WHERE ec.startDate >= :startDate AND ec.endDate <= :endDate")
    List<EvaluationCycles> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
