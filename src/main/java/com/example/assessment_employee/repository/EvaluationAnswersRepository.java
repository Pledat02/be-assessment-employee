package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationAnswersRepository extends JpaRepository<EvaluationAnswers, Long> {
    
    /**
     * Find evaluation answers by employee
     * @param employee the employee to search for
     * @return List<EvaluationAnswers>
     */
    List<EvaluationAnswers> findByEmployee(Employee employee);
    
    /**
     * Find evaluation answers by employee ID
     * @param employeeId the employee ID to search for
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.employee.code = :employeeId")
    List<EvaluationAnswers> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    /**
     * Find evaluation answers by criteria form ID
     * @param criteriaFormId the criteria form ID to search for
     * @return List<EvaluationAnswers>
     */
    List<EvaluationAnswers> findByCriteriaFormId(Long criteriaFormId);
    
    /**
     * Find evaluation answers by employee and criteria form
     * @param employeeId the employee ID
     * @param criteriaFormId the criteria form ID
     * @return Optional<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.employee.code = :employeeId AND ea.criteriaFormId = :criteriaFormId")
    Optional<EvaluationAnswers> findByEmployeeIdAndCriteriaFormId(@Param("employeeId") Long employeeId, @Param("criteriaFormId") Long criteriaFormId);
    
    /**
     * Find evaluation answers with employee information
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea LEFT JOIN FETCH ea.employee")
    List<EvaluationAnswers> findAllWithEmployee();
    
    /**
     * Find evaluation answer by ID with employee information
     * @param answerId the answer ID
     * @return Optional<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea LEFT JOIN FETCH ea.employee WHERE ea.evaluationAnswerId = :answerId")
    Optional<EvaluationAnswers> findByIdWithEmployee(@Param("answerId") Long answerId);
    
    /**
     * Find evaluation answers by employee with full employee info
     * @param employeeId the employee ID
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea LEFT JOIN FETCH ea.employee e LEFT JOIN FETCH e.department WHERE ea.employee.code = :employeeId")
    List<EvaluationAnswers> findByEmployeeIdWithFullInfo(@Param("employeeId") Long employeeId);
    
    /**
     * Find evaluation answers by department ID
     * @param departmentId the department ID
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.employee.department.departmentId = :departmentId")
    List<EvaluationAnswers> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    /**
     * Find completed evaluations (has totalScoreManager)
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.totalScoreManager IS NOT NULL")
    List<EvaluationAnswers> findCompletedEvaluations();
    
    /**
     * Find pending evaluations (no totalScoreManager)
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.totalScoreManager IS NULL")
    List<EvaluationAnswers> findPendingEvaluations();
    
    /**
     * Find evaluations by total score range
     * @param minScore the minimum score
     * @param maxScore the maximum score
     * @return List<EvaluationAnswers>
     */
    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.totalScore BETWEEN :minScore AND :maxScore")
    List<EvaluationAnswers> findByTotalScoreRange(@Param("minScore") Long minScore, @Param("maxScore") Long maxScore);
    
    /**
     * Count evaluations by employee ID
     * @param employeeId the employee ID
     * @return long
     */
    @Query("SELECT COUNT(ea) FROM EvaluationAnswers ea WHERE ea.employee.code = :employeeId")
    long countByEmployeeId(@Param("employeeId") Long employeeId);
    
    /**
     * Count evaluations by criteria form ID
     * @param criteriaFormId the criteria form ID
     * @return long
     */
    long countByCriteriaFormId(Long criteriaFormId);
}
