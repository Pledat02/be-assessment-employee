package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.EvaluationAnswersDetail;
import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.EvaluationQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationAnswersDetailRepository extends JpaRepository<EvaluationAnswersDetail, Long> {
    
    /**
     * Find answer details by evaluation answer
     * @param evaluationAnswer the evaluation answer to search for
     * @return List<EvaluationAnswersDetail>
     */
    List<EvaluationAnswersDetail> findByEvaluationAnswer(EvaluationAnswers evaluationAnswer);
    
    /**
     * Find answer details by evaluation answer ID
     * @param evaluationAnswerId the evaluation answer ID to search for
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId")
    List<EvaluationAnswersDetail> findByEvaluationAnswerId(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Find answer details by evaluation question
     * @param evaluationQuestion the evaluation question to search for
     * @return List<EvaluationAnswersDetail>
     */
    List<EvaluationAnswersDetail> findByEvaluationQuestion(EvaluationQuestions evaluationQuestion);
    
    /**
     * Find answer details by question ID
     * @param questionId the question ID to search for
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationQuestion.evaluationQuestionId = :questionId")
    List<EvaluationAnswersDetail> findByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * Find answer detail by evaluation answer and question
     * @param evaluationAnswerId the evaluation answer ID
     * @param questionId the question ID
     * @return Optional<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.evaluationQuestion.evaluationQuestionId = :questionId")
    Optional<EvaluationAnswersDetail> findByEvaluationAnswerIdAndQuestionId(@Param("evaluationAnswerId") Long evaluationAnswerId, @Param("questionId") Long questionId);
    
    /**
     * Find answer details with evaluation answer and question information
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead LEFT JOIN FETCH ead.evaluationAnswer LEFT JOIN FETCH ead.evaluationQuestion")
    List<EvaluationAnswersDetail> findAllWithFullInfo();
    
    /**
     * Find answer details by evaluation answer ID with question info
     * @param evaluationAnswerId the evaluation answer ID
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead LEFT JOIN FETCH ead.evaluationQuestion WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId")
    List<EvaluationAnswersDetail> findByEvaluationAnswerIdWithQuestion(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Find answer details that have employee scores
     * @param evaluationAnswerId the evaluation answer ID
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.employeeScore IS NOT NULL")
    List<EvaluationAnswersDetail> findByEvaluationAnswerIdWithEmployeeScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Find answer details that have supervisor scores
     * @param evaluationAnswerId the evaluation answer ID
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.supervisorScore IS NOT NULL")
    List<EvaluationAnswersDetail> findByEvaluationAnswerIdWithSupervisorScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Find answer details that have manager scores
     * @param evaluationAnswerId the evaluation answer ID
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.managerScore IS NOT NULL")
    List<EvaluationAnswersDetail> findByEvaluationAnswerIdWithManagerScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Find incomplete answer details (missing any score)
     * @param evaluationAnswerId the evaluation answer ID
     * @return List<EvaluationAnswersDetail>
     */
    @Query("SELECT ead FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND (ead.employeeScore IS NULL OR ead.supervisorScore IS NULL OR ead.managerScore IS NULL)")
    List<EvaluationAnswersDetail> findIncompleteByEvaluationAnswerId(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Count answer details by evaluation answer ID
     * @param evaluationAnswerId the evaluation answer ID
     * @return long
     */
    @Query("SELECT COUNT(ead) FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId")
    long countByEvaluationAnswerId(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Calculate average employee score for evaluation
     * @param evaluationAnswerId the evaluation answer ID
     * @return Double
     */
    @Query("SELECT AVG(ead.employeeScore) FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.employeeScore IS NOT NULL")
    Double calculateAverageEmployeeScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Calculate average supervisor score for evaluation
     * @param evaluationAnswerId the evaluation answer ID
     * @return Double
     */
    @Query("SELECT AVG(ead.supervisorScore) FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.supervisorScore IS NOT NULL")
    Double calculateAverageSupervisorScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
    
    /**
     * Calculate average manager score for evaluation
     * @param evaluationAnswerId the evaluation answer ID
     * @return Double
     */
    @Query("SELECT AVG(ead.managerScore) FROM EvaluationAnswersDetail ead WHERE ead.evaluationAnswer.evaluationAnswerId = :evaluationAnswerId AND ead.managerScore IS NOT NULL")
    Double calculateAverageManagerScore(@Param("evaluationAnswerId") Long evaluationAnswerId);
}
