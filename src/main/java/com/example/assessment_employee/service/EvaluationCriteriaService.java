package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EvaluationCriteriaCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationCriteriaResponse;
import com.example.assessment_employee.entity.EvaluationCriteria;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.EvaluationCriteriaMapper;
import com.example.assessment_employee.repository.EvaluationCriteriaRepository;
import com.example.assessment_employee.repository.EvaluationQuestionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationCriteriaService {
    
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final EvaluationQuestionsRepository evaluationQuestionsRepository;
    private final EvaluationCriteriaMapper evaluationCriteriaMapper;
    
    /**
     * Create new evaluation criteria
     */
    public EvaluationCriteriaResponse createEvaluationCriteria(EvaluationCriteriaCreateRequest request) {
        log.info("Creating new evaluation criteria: {}", request.getCriteriaName());
        
        // Check if criteria name already exists
        if (evaluationCriteriaRepository.existsByCriteriaName(request.getCriteriaName())) {
            log.warn("Evaluation criteria creation failed: Criteria name already exists: {}", request.getCriteriaName());
            throw new AppException(ErrorCode.CRITERIA_NAME_EXISTED);
        }
        
        // Convert request to entity
        EvaluationCriteria criteria = evaluationCriteriaMapper.toEntity(request);
        
        // Save criteria
        EvaluationCriteria savedCriteria = evaluationCriteriaRepository.save(criteria);

        log.info("Evaluation criteria created successfully with ID: {}", savedCriteria.getEvaluationCriteriaId());

        // Manual mapping for response
        EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(savedCriteria);
        if (savedCriteria.getEvaluationQuestions() != null) {
            response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(savedCriteria.getEvaluationQuestions()));
        }

        return response;
    }
    
    /**
     * Get all evaluation criteria with pagination
     */
    @Transactional(readOnly = true)
    public Page<EvaluationCriteriaResponse> getAllEvaluationCriteria(Pageable pageable) {
        log.info("Getting all evaluation criteria with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<EvaluationCriteria> criteriaPage = evaluationCriteriaRepository.findAll(pageable);
        List<EvaluationCriteriaResponse> responses = criteriaPage.getContent().stream()
                .map(criteria -> {
                    EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(criteria);
                    if (criteria.getEvaluationQuestions() != null) {
                        response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(criteria.getEvaluationQuestions()));
                    }
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());

        return new PageImpl<>(responses, pageable, criteriaPage.getTotalElements());
    }
    
    /**
     * Get all evaluation criteria without pagination
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getAllEvaluationCriteria() {
        log.info("Getting all evaluation criteria");
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findAllWithQuestions();
        return criteria.stream()
                .map(criteriaItem -> {
                    EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(criteriaItem);
                    if (criteriaItem.getEvaluationQuestions() != null) {
                        response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(criteriaItem.getEvaluationQuestions()));
                    }
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get evaluation criteria by ID
     */
    @Transactional(readOnly = true)
    public EvaluationCriteriaResponse getEvaluationCriteriaById(Long id) {
        log.info("Getting evaluation criteria by ID: {}", id);
        
        EvaluationCriteria criteria = evaluationCriteriaRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation criteria not found with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                });
        
        // Manual mapping for response
        EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(criteria);
        if (criteria.getEvaluationQuestions() != null) {
            response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(criteria.getEvaluationQuestions()));
        }

        return response;
    }
    
    /**
     * Get evaluation criteria by name
     */
    @Transactional(readOnly = true)
    public EvaluationCriteriaResponse getEvaluationCriteriaByName(String criteriaName) {
        log.info("Getting evaluation criteria by name: {}", criteriaName);
        
        EvaluationCriteria criteria = evaluationCriteriaRepository.findByCriteriaName(criteriaName)
                .orElseThrow(() -> {
                    log.warn("Evaluation criteria not found with name: {}", criteriaName);
                    return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                });
        
        // Manual mapping for response
        EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(criteria);
        if (criteria.getEvaluationQuestions() != null) {
            response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(criteria.getEvaluationQuestions()));
        }

        return response;
    }
    
    /**
     * Update evaluation criteria
     */
    public EvaluationCriteriaResponse updateEvaluationCriteria(Long id, EvaluationCriteriaCreateRequest request) {
        log.info("Updating evaluation criteria with ID: {}", id);
        
        EvaluationCriteria existingCriteria = evaluationCriteriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation criteria not found for update with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                });
        
        // Check if criteria name is being changed and if new name already exists
        if (!existingCriteria.getCriteriaName().equals(request.getCriteriaName()) &&
            evaluationCriteriaRepository.existsByCriteriaName(request.getCriteriaName())) {
            log.warn("Evaluation criteria update failed: Criteria name already exists: {}", request.getCriteriaName());
            throw new AppException(ErrorCode.CRITERIA_NAME_EXISTED);
        }
        
        // Update criteria fields
        evaluationCriteriaMapper.updateEntity(request, existingCriteria);
        
        EvaluationCriteria updatedCriteria = evaluationCriteriaRepository.save(existingCriteria);
        
        log.info("Evaluation criteria updated successfully with ID: {}", updatedCriteria.getEvaluationCriteriaId());
        
        // Manual mapping for response
        EvaluationCriteriaResponse response = evaluationCriteriaMapper.toResponse(updatedCriteria);
        if (updatedCriteria.getEvaluationQuestions() != null) {
            response.setEvaluationQuestions(evaluationCriteriaMapper.toQuestionInfoList(updatedCriteria.getEvaluationQuestions()));
        }

        return response;
    }
    
    /**
     * Delete evaluation criteria
     */
    public void deleteEvaluationCriteria(Long id) {
        log.info("Deleting evaluation criteria with ID: {}", id);
        
        EvaluationCriteria criteria = evaluationCriteriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation criteria not found for deletion with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                });
        
        // Check if criteria has questions
        long questionCount = evaluationCriteriaRepository.countQuestionsByCriteriaId(id);
        if (questionCount > 0) {
            log.warn("Cannot delete evaluation criteria with existing questions: {}", id);
            throw new AppException(ErrorCode.CRITERIA_HAS_QUESTIONS);
        }
        
        // Check if criteria is being used in forms
        List<EvaluationCriteria> criteriaInUse = evaluationCriteriaRepository.findCriteriaUsedInForms();
        if (criteriaInUse.stream().anyMatch(c -> c.getEvaluationCriteriaId().equals(id))) {
            log.warn("Cannot delete evaluation criteria that is being used in forms: {}", id);
            throw new AppException(ErrorCode.CRITERIA_IN_USE);
        }
        
        evaluationCriteriaRepository.deleteById(id);
        
        log.info("Evaluation criteria deleted successfully with ID: {}", id);
    }
    
    /**
     * Search evaluation criteria by name
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> searchEvaluationCriteriaByName(String keyword) {
        log.info("Searching evaluation criteria by name keyword: {}", keyword);
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.searchByCriteriaName(keyword);
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Get criteria with questions
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getCriteriaWithQuestions() {
        log.info("Getting criteria that have questions");
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findCriteriaWithQuestions();
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Get criteria without questions
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getCriteriaWithoutQuestions() {
        log.info("Getting criteria that have no questions");
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findCriteriaWithoutQuestions();
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Get criteria used in forms
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getCriteriaUsedInForms() {
        log.info("Getting criteria that are used in forms");
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findCriteriaUsedInForms();
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Get criteria not used in any form
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getCriteriaNotUsedInForms() {
        log.info("Getting criteria that are not used in any form");
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findCriteriaNotUsedInForms();
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Get criteria by IDs
     */
    @Transactional(readOnly = true)
    public List<EvaluationCriteriaResponse> getCriteriaByIds(List<Long> criteriaIds) {
        log.info("Getting criteria by IDs: {}", criteriaIds);
        
        List<EvaluationCriteria> criteria = evaluationCriteriaRepository.findByCriteriaIds(criteriaIds);
        return evaluationCriteriaMapper.toResponseList(criteria);
    }
    
    /**
     * Check if criteria exists
     */
    @Transactional(readOnly = true)
    public boolean criteriaExists(Long id) {
        return evaluationCriteriaRepository.existsById(id);
    }
    
    /**
     * Check if criteria name exists
     */
    @Transactional(readOnly = true)
    public boolean criteriaNameExists(String criteriaName) {
        return evaluationCriteriaRepository.existsByCriteriaName(criteriaName);
    }
    
    /**
     * Get question count for criteria
     */
    @Transactional(readOnly = true)
    public long getQuestionCountForCriteria(Long criteriaId) {
        return evaluationCriteriaRepository.countQuestionsByCriteriaId(criteriaId);
    }
}
