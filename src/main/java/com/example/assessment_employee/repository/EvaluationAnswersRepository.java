package com.example.assessment_employee.repository;

import com.example.assessment_employee.dto.response.CriteriaAverageResponse;
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

    /**
     * Đếm số nhân viên đã được đánh giá
     */
    @Query("SELECT COUNT(DISTINCT ea.employee.code) FROM EvaluationAnswers ea")
    long countEvaluatedEmployees();

    /**
     * Tính điểm trung bình tất cả bản đánh giá
     */
    @Query("SELECT AVG(ea.totalScore) FROM EvaluationAnswers ea")
    Double averageTotalScore();

    /**
     * Đếm số nhân viên có tổng điểm >= 90 (được xếp 'Xuất sắc')
     */
    @Query("SELECT COUNT(DISTINCT ea.employee.code) FROM EvaluationAnswers ea WHERE ea.totalScore >= 90")
    long countExcellentEmployees();


    @Query("""
        SELECT 
            e.fullName,
            e.staffType,
            d.departmentName,
            AVG(a.totalScore) AS avgScore
        FROM EvaluationAnswers a
        JOIN a.employee e
        JOIN e.department d
        GROUP BY e.code, e.fullName, e.staffType, d.departmentName
        ORDER BY avgScore DESC
        """)
    List<Object[]> findTop5Employees();

    @Query(value = """
        SELECT 
            ec.criteria_name AS criteria,
            ROUND(AVG(ead.employee_score), 2) AS averageScore
        FROM evaluation_answers ea
        JOIN employee e ON ea.employee_id = e.id
        JOIN criteria_form cf ON ea.criteria_form_id = cf.criteria_form_id
        JOIN evaluation_cycles ecx ON ecx.evaluation_cycle_id = cf.evaluation_cycle_id
        JOIN evaluation_answers_detail ead ON ead.evaluation_answer_id = ea.evaluation_answer_id
        JOIN evaluation_questions eq ON ead.evaluation_question_id = eq.evaluation_question_id
        JOIN evaluation_criteria ec ON eq.evaluation_criteria_id = ec.evaluation_criteria_id
        WHERE ecx.start_date >= :startDate AND ecx.end_date <= :endDate
          AND (:departmentId IS NULL OR e.department_id = :departmentId)
        GROUP BY ec.criteria_name
        ORDER BY ec.criteria_name
        """, nativeQuery = true)
    List<Object[]> fetchAverageByCriteriaRaw(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("departmentId") Long departmentId
    );

    @Query("""
    SELECT new com.example.assessment_employee.dto.response.CriteriaAverageResponse(
        q.evaluationCriteria.criteriaName,
        AVG(COALESCE(ed.managerScore, 0))
    )
    FROM EvaluationAnswers ea
    JOIN ea.employee e
    JOIN EvaluationAnswersDetail ed ON ed.evaluationAnswer = ea
    JOIN EvaluationQuestions q ON q = ed.evaluationQuestion
    WHERE e.code = :employeeId
    GROUP BY q.evaluationCriteria.criteriaName
""")
    List<CriteriaAverageResponse> fetchAverageScoresByCriteriaForEmployee(@Param("employeeId") Long employeeId);

}
