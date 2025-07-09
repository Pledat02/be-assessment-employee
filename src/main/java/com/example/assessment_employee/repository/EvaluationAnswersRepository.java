package com.example.assessment_employee.repository;

import com.example.assessment_employee.dto.response.CriteriaAverageResponse;
import com.example.assessment_employee.dto.response.CriteriaEmployeeResponse;
import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.entity.SummaryAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationAnswersRepository extends JpaRepository<EvaluationAnswers, Long> {


    /**
     * Count number of evaluated employees
     */
    @Query("SELECT COUNT(DISTINCT ea.summaryAssessment.employee.code) FROM EvaluationAnswers ea")
    long countEvaluatedEmployees();

    /**
     * Calculate average total score of all evaluations
     */
    @Query("SELECT AVG(ea.totalScore) FROM EvaluationAnswers ea")
    Double averageTotalScore();

    /**
     * Count employees with total score >= 90 (rated 'Excellent')
     */
    @Query("SELECT COUNT(DISTINCT ea.summaryAssessment.employee.code) FROM EvaluationAnswers ea WHERE ea.summaryAssessment.sentiment= 'Tốt' ")
    long countExcellentEmployees();

    /**
     * Find top 5 employees by average score
     */
    @Query("SELECT e.fullName, e.staffType, d.departmentName, ea.summaryAssessment.sentiment," +
            " ea.summaryAssessment.averageScore AS avgScore " +
            "FROM EvaluationAnswers ea " +
            "JOIN ea.summaryAssessment.employee e " +
            "JOIN e.department d " +
            "GROUP BY e.code, e.fullName, e.staffType, d.departmentName " +
            "ORDER BY ea.summaryAssessment.averageScore DESC")
    List<Object[]> findTop5Employees();

    @Query("SELECT ea FROM EvaluationAnswers ea WHERE ea.summaryAssessment.summaryAssessmentId = :summaryAssessmentId AND ea.question.evaluationQuestionId = :questionId")
    Optional<EvaluationAnswers> findBySummaryAssessmentIdAndQuestionId(@Param("summaryAssessmentId") Long summaryAssessmentId, @Param("questionId") Long questionId);

    /**
     * Fetch average scores by criteria for a specific employee
     */
    /**
     * Fetch average scores by criteria within a date range and optional department
     */
    @Query(value = """
    SELECT ec.criteria_name AS criteria,
           SUM(COALESCE(ea.total_score, 0)) AS averageScore
    FROM evaluation_answers ea
    JOIN summary_assessment sa ON ea.summary_assessment_id = sa.summary_assessment_id
    JOIN criteria_form cf ON sa.criteria_form_id = cf.criteria_form_id
    JOIN evaluation_cycles ec2 ON cf.evaluation_cycle_id = ec2.evaluation_cycle_id
    JOIN evaluation_questions eq ON ea.question_id = eq.evaluation_question_id
    JOIN evaluation_criteria ec ON eq.evaluation_criteria_id = ec.evaluation_criteria_id
    JOIN employee e ON sa.employee_id = e.id
    WHERE ec2.start_date >= :startDate
      AND ec2.end_date <= :endDate
    GROUP BY ec.criteria_name
    ORDER BY ec.criteria_name
    """, nativeQuery = true)
    List<Object[]> getAverageScoreByCriteria(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query("SELECT new com.example.assessment_employee.dto.response.CriteriaEmployeeResponse(" +
            "eq.evaluationCriteria.criteriaName, " +
            "SUM(COALESCE(ea.totalScore, 0)), " +
            "SUM(COALESCE(eq.maxScore, 0))) " +
            "FROM EvaluationAnswers ea " +
            "JOIN ea.summaryAssessment.employee e " +
            "JOIN ea.question eq " +
            "WHERE e.code = :employeeId " +
            "GROUP BY eq.evaluationCriteria.criteriaName")
    List<CriteriaEmployeeResponse> fetchAverageScoresByCriteriaForEmployee(@Param("employeeId") Long employeeId);
}