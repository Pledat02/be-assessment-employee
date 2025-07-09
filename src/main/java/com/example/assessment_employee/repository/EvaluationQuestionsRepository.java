package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.EvaluationQuestions;
import com.example.assessment_employee.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationQuestionsRepository extends JpaRepository<EvaluationQuestions, Long> {
    
    /**
     * Find questions by evaluation criteria
     * @param evaluationCriteria the evaluation criteria to search for
     * @return List<EvaluationQuestions>
     */
    List<EvaluationQuestions> findByEvaluationCriteria(EvaluationCriteria evaluationCriteria);
    
    /**
     * Find questions by criteria ID
     * @param criteriaId the criteria ID to search for
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq WHERE eq.evaluationCriteria.evaluationCriteriaId = :criteriaId")
    List<EvaluationQuestions> findByCriteriaId(@Param("criteriaId") Long criteriaId);
    
    /**
     * Find questions by question name containing keyword
     * @param keyword the keyword to search for
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq WHERE LOWER(eq.questionName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EvaluationQuestions> searchByQuestionName(@Param("keyword") String keyword);
    
    /**
     * Find questions by max score
     * @param maxScore the max score to search for
     * @return List<EvaluationQuestions>
     */
    List<EvaluationQuestions> findByMaxScore(Long maxScore);
    
    /**
     * Find questions by max score range
     * @param minScore the minimum score
     * @param maxScore the maximum score
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq WHERE eq.maxScore BETWEEN :minScore AND :maxScore")
    List<EvaluationQuestions> findByMaxScoreRange(@Param("minScore") Long minScore, @Param("maxScore") Long maxScore);
    
    /**
     * Find questions with evaluation criteria information
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq LEFT JOIN FETCH eq.evaluationCriteria")
    List<EvaluationQuestions> findAllWithCriteria();
    
    /**
     * Find question by ID with criteria information
     * @param questionId the question ID
     * @return Optional<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq LEFT JOIN FETCH eq.evaluationCriteria WHERE eq.evaluationQuestionId = :questionId")
    Optional<EvaluationQuestions> findByIdWithCriteria(@Param("questionId") Long questionId);
    
    /**
     * Find questions by criteria ID with criteria information
     * @param criteriaId the criteria ID
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq LEFT JOIN FETCH eq.evaluationCriteria WHERE eq.evaluationCriteria.evaluationCriteriaId = :criteriaId")
    List<EvaluationQuestions> findByCriteriaIdWithCriteria(@Param("criteriaId") Long criteriaId);
    
    /**
     * Check if question name exists for a criteria
     * @param questionName the question name
     * @param criteriaId the criteria ID
     * @return boolean
     */
    @Query("SELECT COUNT(eq) > 0 FROM EvaluationQuestions eq WHERE eq.questionName = :questionName AND eq.evaluationCriteria.evaluationCriteriaId = :criteriaId")
    boolean existsByQuestionNameAndCriteriaId(@Param("questionName") String questionName, @Param("criteriaId") Long criteriaId);
    
    /**
     * Count questions by criteria ID
     * @param criteriaId the criteria ID
     * @return long
     */
    @Query("SELECT COUNT(eq) FROM EvaluationQuestions eq WHERE eq.evaluationCriteria.evaluationCriteriaId = :criteriaId")
    long countByCriteriaId(@Param("criteriaId") Long criteriaId);
    
    /**
     * Find questions by multiple criteria IDs
     * @param criteriaIds the list of criteria IDs
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq WHERE eq.evaluationCriteria.evaluationCriteriaId IN :criteriaIds")
    List<EvaluationQuestions> findByCriteriaIds(@Param("criteriaIds") List<Long> criteriaIds);
    
    /**
     * Find questions ordered by criteria and question name
     * @return List<EvaluationQuestions>
     */
    @Query("SELECT eq FROM EvaluationQuestions eq ORDER BY eq.evaluationCriteria.criteriaName, eq.questionName")
    List<EvaluationQuestions> findAllOrderedByCriteriaAndName();

    /**
     * Count questions by criteria form ID
     * @param criteriaFormId the criteria form ID
     * @return int
     */
    @Query("SELECT COUNT(eq) FROM EvaluationQuestions eq JOIN CriteriaForm cf ON eq.evaluationCriteria MEMBER OF cf.evaluationCriteria WHERE cf.criteriaFormId = :criteriaFormId")
    int countByCriteriaFormId(@Param("criteriaFormId") Long criteriaFormId);
}
