package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.EvaluationCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationCriteriaRepository extends JpaRepository<EvaluationCriteria, Long> {
    
    /**
     * Find evaluation criteria by criteria name
     * @param criteriaName the criteria name to search for
     * @return Optional<EvaluationCriteria>
     */
    Optional<EvaluationCriteria> findByCriteriaName(String criteriaName);
    
    /**
     * Find evaluation criteria by name containing keyword
     * @param keyword the keyword to search for
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec WHERE LOWER(ec.criteriaName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<EvaluationCriteria> searchByCriteriaName(@Param("keyword") String keyword);
    
    /**
     * Find evaluation criteria with questions
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec LEFT JOIN FETCH ec.evaluationQuestions")
    List<EvaluationCriteria> findAllWithQuestions();
    
    /**
     * Find evaluation criteria by ID with questions
     * @param criteriaId the criteria ID
     * @return Optional<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec LEFT JOIN FETCH ec.evaluationQuestions WHERE ec.evaluationCriteriaId = :criteriaId")
    Optional<EvaluationCriteria> findByIdWithQuestions(@Param("criteriaId") Long criteriaId);
    
    /**
     * Check if criteria name exists
     * @param criteriaName the criteria name to check
     * @return boolean
     */
    boolean existsByCriteriaName(String criteriaName);
    
    /**
     * Count questions for a criteria
     * @param criteriaId the criteria ID
     * @return long
     */
    @Query("SELECT COUNT(eq) FROM EvaluationQuestions eq WHERE eq.evaluationCriteria.evaluationCriteriaId = :criteriaId")
    long countQuestionsByCriteriaId(@Param("criteriaId") Long criteriaId);
    
    /**
     * Find criteria that have questions
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT DISTINCT ec FROM EvaluationCriteria ec WHERE EXISTS (SELECT 1 FROM EvaluationQuestions eq WHERE eq.evaluationCriteria = ec)")
    List<EvaluationCriteria> findCriteriaWithQuestions();
    
    /**
     * Find criteria that have no questions
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec WHERE NOT EXISTS (SELECT 1 FROM EvaluationQuestions eq WHERE eq.evaluationCriteria = ec)")
    List<EvaluationCriteria> findCriteriaWithoutQuestions();
    
    /**
     * Find criteria used in criteria forms
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT DISTINCT ec FROM EvaluationCriteria ec JOIN CriteriaForm cf ON ec MEMBER OF cf.evaluationCriteria")
    List<EvaluationCriteria> findCriteriaUsedInForms();
    
    /**
     * Find criteria not used in any criteria form
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec WHERE NOT EXISTS (SELECT 1 FROM CriteriaForm cf WHERE ec MEMBER OF cf.evaluationCriteria)")
    List<EvaluationCriteria> findCriteriaNotUsedInForms();
    
    /**
     * Find criteria by IDs
     * @param criteriaIds the list of criteria IDs
     * @return List<EvaluationCriteria>
     */
    @Query("SELECT ec FROM EvaluationCriteria ec WHERE ec.evaluationCriteriaId IN :criteriaIds")
    List<EvaluationCriteria> findByCriteriaIds(@Param("criteriaIds") List<Long> criteriaIds);
}
