package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.CriteriaFormCreateRequest;
import com.example.assessment_employee.dto.response.CriteriaFormResponse;
import com.example.assessment_employee.entity.CriteriaForm;
import com.example.assessment_employee.entity.EvaluationCriteria;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.CriteriaFormMapper;
import com.example.assessment_employee.mapper.EvaluationCriteriaMapper;
import com.example.assessment_employee.repository.CriteriaFormRepository;
import com.example.assessment_employee.repository.EvaluationCriteriaRepository;
import com.example.assessment_employee.repository.EvaluationAnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CriteriaFormService {
    
    private final CriteriaFormRepository criteriaFormRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final CriteriaFormMapper criteriaFormMapper;
    private final EvaluationCriteriaMapper evaluationCriteriaMapper;

    /**
     * Helper method to manually map CriteriaForm to CriteriaFormResponse
     */
    private CriteriaFormResponse mapToResponse(CriteriaForm criteriaForm) {
        CriteriaFormResponse response = criteriaFormMapper.toResponse(criteriaForm);

        if (criteriaForm.getEvaluationCriteria() != null && !criteriaForm.getEvaluationCriteria().isEmpty()) {
            List<CriteriaFormResponse.EvaluationCriteriaInfo> criteriaInfoList = criteriaForm.getEvaluationCriteria().stream()
                    .map(criteria -> {
                        CriteriaFormResponse.EvaluationCriteriaInfo criteriaInfo =
                                new CriteriaFormResponse.EvaluationCriteriaInfo();
                        criteriaInfo.setEvaluationCriteriaId(criteria.getEvaluationCriteriaId());
                        criteriaInfo.setCriteriaName(criteria.getCriteriaName());

                        return criteriaInfo;
                    })
                    .collect(Collectors.toList());
            response.setEvaluationCriteria(criteriaInfoList);
        } else {
            response.setEvaluationCriteria(Collections.emptyList());
        }

        return response;
    }

    /**
     * Helper method to manually map list of CriteriaForm to list of CriteriaFormResponse
     */
    private List<CriteriaFormResponse> mapToResponseList(List<CriteriaForm> criteriaForms) {
        return criteriaForms.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Create new criteria form
     */
    public CriteriaFormResponse createCriteriaForm(CriteriaFormCreateRequest request) {
        log.info("Creating new criteria form: {}", request.getCriteriaFormName());
        
        // Check if criteria form name already exists
        if (criteriaFormRepository.existsByCriteriaFormName(request.getCriteriaFormName())) {
            log.warn("Criteria form creation failed: Form name already exists: {}", request.getCriteriaFormName());
            throw new AppException(ErrorCode.CRITERIA_FORM_NAME_EXISTED);
        }
        
        // Check if form name already exists for this evaluation cycle
        if (criteriaFormRepository.existsByEvaluationCycleIdAndCriteriaFormName(
                request.getEvaluationCycleId(), request.getCriteriaFormName())) {
            log.warn("Criteria form creation failed: Form name already exists for cycle: {}", 
                    request.getCriteriaFormName());
            throw new AppException(ErrorCode.CRITERIA_FORM_ALREADY_EXISTS);
        }
        
        // Validate that evaluation criteria IDs exist
        if (request.getEvaluationCriteriaIds() == null || request.getEvaluationCriteriaIds().isEmpty()) {
            log.warn("Criteria form creation failed: No evaluation criteria provided");
            throw new AppException(ErrorCode.CRITERIA_FORM_EMPTY);
        }

        Set<EvaluationCriteria> evaluationCriteria = new HashSet<>(
                evaluationCriteriaRepository.findByCriteriaIds(request.getEvaluationCriteriaIds())
        );

        if (evaluationCriteria.size() != request.getEvaluationCriteriaIds().size()) {
            log.warn("Criteria form creation failed: Some evaluation criteria not found");
            throw new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
        }
        
        // Convert request to entity
        CriteriaForm criteriaForm = criteriaFormMapper.toEntity(request);
        criteriaForm.setEvaluationCriteria(evaluationCriteria);
        criteriaForm.setEvaluationCycleId(request.getEvaluationCycleId());
        
        // Save criteria form
        CriteriaForm savedForm = criteriaFormRepository.save(criteriaForm);
        
        log.info("Criteria form created successfully with ID: {}", savedForm.getCriteriaFormId());
        
        return mapToResponse(savedForm);
    }
    
    /**
     * Get all criteria forms with pagination
     */
    @Transactional(readOnly = true)
    public Page<CriteriaFormResponse> getAllCriteriaForms(Pageable pageable) {
        log.info("Getting all criteria forms with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<CriteriaForm> formPage = criteriaFormRepository.findAll(pageable);
        List<CriteriaFormResponse> responses = mapToResponseList(formPage.getContent());
        
        return new PageImpl<>(responses, pageable, formPage.getTotalElements());
    }
    
    /**
     * Get all criteria forms without pagination
     */
    @Transactional(readOnly = true)
    public List<CriteriaFormResponse> getAllCriteriaForms() {
        log.info("Getting all criteria forms");
        
        List<CriteriaForm> forms = criteriaFormRepository.findAllWithFullDetails();
        return mapToResponseList(forms);
    }
    
    /**
     * Get criteria form by ID
     */
    @Transactional(readOnly = true)
    public CriteriaFormResponse getCriteriaFormById(Long id) {
        log.info("Getting criteria form by ID: {}", id);
        
        CriteriaForm form = criteriaFormRepository.findByIdWithEvaluationCriteria(id)
                .orElseThrow(() -> {
                    log.warn("Criteria form not found with ID: {}", id);
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });
        
        return mapToResponse(form);
    }
    
    /**
     * Get criteria form by name
     */
    @Transactional(readOnly = true)
    public CriteriaFormResponse getCriteriaFormByName(String criteriaFormName) {
        log.info("Getting criteria form by name: {}", criteriaFormName);
        
        CriteriaForm form = criteriaFormRepository.findByCriteriaFormName(criteriaFormName)
                .orElseThrow(() -> {
                    log.warn("Criteria form not found with name: {}", criteriaFormName);
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });
        
        return mapToResponse(form);
    }
    
    /**
     * Update criteria form
     */
    public CriteriaFormResponse updateCriteriaForm(Long id, CriteriaFormCreateRequest request) {
        log.info("Updating criteria form with ID: {}", id);
        
        CriteriaForm existingForm = criteriaFormRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Criteria form not found for update with ID: {}", id);
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });

        // Check if form name is being changed and if new name already exists
        if (!existingForm.getCriteriaFormName().equals(request.getCriteriaFormName()) &&
            criteriaFormRepository.existsByCriteriaFormName(request.getCriteriaFormName())) {
            log.warn("Criteria form update failed: Form name already exists: {}", request.getCriteriaFormName());
            throw new AppException(ErrorCode.CRITERIA_FORM_NAME_EXISTED);
        }
        
        // Check if form name already exists for the evaluation cycle (excluding current form)
        if (!existingForm.getCriteriaFormName().equals(request.getCriteriaFormName()) &&
            criteriaFormRepository.existsByEvaluationCycleIdAndCriteriaFormName(
                    request.getEvaluationCycleId(), request.getCriteriaFormName())) {
            log.warn("Criteria form update failed: Form name already exists for cycle: {}", 
                    request.getCriteriaFormName());
            throw new AppException(ErrorCode.CRITERIA_FORM_ALREADY_EXISTS);
        }
        
        // Validate that evaluation criteria IDs exist
        if (request.getEvaluationCriteriaIds() == null || request.getEvaluationCriteriaIds().isEmpty()) {
            log.warn("Criteria form update failed: No evaluation criteria provided");
            throw new AppException(ErrorCode.CRITERIA_FORM_EMPTY);
        }

        Set<EvaluationCriteria> evaluationCriteria = new HashSet<>(
                evaluationCriteriaRepository.findByCriteriaIds(request.getEvaluationCriteriaIds())
        );
        
        if (evaluationCriteria.size() != request.getEvaluationCriteriaIds().size()) {
            log.warn("Criteria form update failed: Some evaluation criteria not found");
            throw new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
        }
        
        // Update form fields
        criteriaFormMapper.updateEntity(request, existingForm);
        existingForm.setEvaluationCriteria(evaluationCriteria);
        existingForm.setEvaluationCycleId(request.getEvaluationCycleId());
        CriteriaForm updatedForm = criteriaFormRepository.save(existingForm);
        
        log.info("Criteria form updated successfully with ID: {}", updatedForm.getCriteriaFormId());
        
        return mapToResponse(updatedForm);
    }
    
    /**
     * Delete criteria form
     */
    public void deleteCriteriaForm(Long id) {
        log.info("Deleting criteria form with ID: {}", id);
        
        CriteriaForm form = criteriaFormRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Criteria form not found for deletion with ID: {}", id);
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });
        
        criteriaFormRepository.deleteById(id);
        
        log.info("Criteria form deleted successfully with ID: {}", id);
    }
    
    /**
     * Get criteria forms by evaluation cycle ID
     */
    @Transactional(readOnly = true)
    public List<CriteriaFormResponse> getCriteriaFormsByEvaluationCycleId(String evaluationCycleId) {
        log.info("Getting criteria forms by evaluation cycle ID: {}", evaluationCycleId);
        
        List<CriteriaForm> forms = criteriaFormRepository.findByEvaluationCycleIdWithCriteria(evaluationCycleId);
        return mapToResponseList(forms);
    }
    
    /**
     * Search criteria forms by name
     */
    @Transactional(readOnly = true)
    public List<CriteriaFormResponse> searchCriteriaFormsByName(String keyword) {
        log.info("Searching criteria forms by name keyword: {}", keyword);
        
        List<CriteriaForm> forms = criteriaFormRepository.searchByCriteriaFormName(keyword);
        return mapToResponseList(forms);
    }
    
    /**
     * Get criteria forms that contain specific criteria
     */
    @Transactional(readOnly = true)
    public List<CriteriaFormResponse> getCriteriaFormsByCriteriaId(Long criteriaId) {
        log.info("Getting criteria forms that contain criteria ID: {}", criteriaId);
        
        List<CriteriaForm> forms = criteriaFormRepository.findByCriteriaId(criteriaId);
        return mapToResponseList(forms);
    }

    /**
     * Get criteria form with full details
     */
    @Transactional(readOnly = true)
    public CriteriaFormResponse getCriteriaFormWithFullDetails(Long id) {
        log.info("Getting criteria form with full details for ID: {}", id);

        CriteriaForm form = criteriaFormRepository.findByIdWithEvaluationCriteria(id)
                .orElseThrow(() -> {
                    log.warn("Criteria form not found with ID: {}", id);
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });

        return mapToResponse(form);
    }
    
    /**
     * Check if criteria form exists
     */
    @Transactional(readOnly = true)
    public boolean criteriaFormExists(Long id) {
        return criteriaFormRepository.existsById(id);
    }
    
    /**
     * Check if criteria form name exists
     */
    @Transactional(readOnly = true)
    public boolean criteriaFormNameExists(String criteriaFormName) {
        return criteriaFormRepository.existsByCriteriaFormName(criteriaFormName);
    }
    
    /**
     * Count criteria forms by evaluation cycle
     */
    @Transactional(readOnly = true)
    public long countCriteriaFormsByEvaluationCycle(String evaluationCycleId) {
        return criteriaFormRepository.countByEvaluationCycleId(evaluationCycleId);
    }
}
