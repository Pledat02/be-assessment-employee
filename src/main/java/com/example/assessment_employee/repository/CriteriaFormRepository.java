package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.CriteriaForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriteriaFormRepository extends JpaRepository<CriteriaForm, Long> {
    
    /**
     * Find criteria forms by evaluation cycle ID
     * @param evaluationCycleId the evaluation cycle ID to search for
     * @return List<CriteriaForm>
     */
    List<CriteriaForm> findByEvaluationCycleId(String evaluationCycleId);
    
    /**
     * Find criteria form by name
     * @param criteriaFormName the criteria form name to search for
     * @return Optional<CriteriaForm>
     */
    Optional<CriteriaForm> findByCriteriaFormName(String criteriaFormName);
    
    /**
     * Find criteria forms by name containing keyword
     * @param keyword the keyword to search for
     * @return List<CriteriaForm>
     */
    @Query("SELECT cf FROM CriteriaForm cf WHERE LOWER(cf.criteriaFormName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CriteriaForm> searchByCriteriaFormName(@Param("keyword") String keyword);
    
    /**
     * Find criteria forms with evaluation criteria
     * @return List<CriteriaForm>
     */
    @Query("SELECT cf FROM CriteriaForm cf LEFT JOIN FETCH cf.evaluationCriteria")
    List<CriteriaForm> findAllWithEvaluationCriteria();
    
    /**
     * Find criteria form by ID with evaluation criteria
     * @param criteriaFormId the criteria form ID
     * @return Optional<CriteriaForm>
     */
    @Query("SELECT cf FROM CriteriaForm cf LEFT JOIN FETCH cf.evaluationCriteria WHERE cf.criteriaFormId = :criteriaFormId")
    Optional<CriteriaForm> findByIdWithEvaluationCriteria(@Param("criteriaFormId") Long criteriaFormId);
    
    /**
     * Find criteria forms by evaluation cycle ID with evaluation criteria
     * @param evaluationCycleId the evaluation cycle ID
     * @return List<CriteriaForm>
     */
    @Query("SELECT cf FROM CriteriaForm cf LEFT JOIN FETCH cf.evaluationCriteria WHERE cf.evaluationCycleId = :evaluationCycleId")
    List<CriteriaForm> findByEvaluationCycleIdWithCriteria(@Param("evaluationCycleId") String evaluationCycleId);
    
    /**
     * Check if criteria form name exists
     * @param criteriaFormName the criteria form name to check
     * @return boolean
     */
    boolean existsByCriteriaFormName(String criteriaFormName);
    
    /**
     * Check if criteria form exists for evaluation cycle
     * @param evaluationCycleId the evaluation cycle ID
     * @param criteriaFormName the criteria form name
     * @return boolean
     */
    boolean existsByEvaluationCycleIdAndCriteriaFormName(String evaluationCycleId, String criteriaFormName);
    
    /**
     * Count criteria forms by evaluation cycle ID
     * @param evaluationCycleId the evaluation cycle ID
     * @return long
     */
    long countByEvaluationCycleId(String evaluationCycleId);
    
    /**
     * Find criteria forms that contain specific evaluation criteria
     * @param criteriaId the evaluation criteria ID
     * @return List<CriteriaForm>
     */
    @Query("SELECT cf FROM CriteriaForm cf JOIN cf.evaluationCriteria ec WHERE ec.evaluationCriteriaId = :criteriaId")
    List<CriteriaForm> findByCriteriaId(@Param("criteriaId") Long criteriaId);
    
    /**
     * Find criteria forms with full details (criteria and their questions)
     * @return List<CriteriaForm>
     */
    @Query("SELECT DISTINCT cf FROM CriteriaForm cf LEFT JOIN FETCH cf.evaluationCriteria")
    List<CriteriaForm> findAllWithFullDetails();
}
