package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EvaluationQuestionCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationQuestionResponse;
import com.example.assessment_employee.entity.EvaluationCriteria;
import com.example.assessment_employee.entity.EvaluationQuestions;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.EvaluationQuestionMapper;
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
public class EvaluationQuestionService {
    
    private final EvaluationQuestionsRepository evaluationQuestionsRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final EvaluationQuestionMapper evaluationQuestionMapper;
    
    /**
     * Create new evaluation question
     */
    public EvaluationQuestionResponse createEvaluationQuestion(EvaluationQuestionCreateRequest request) {
        log.info("Creating new evaluation question for criteria ID: {}", request.getEvaluationCriteriaId());
        
        // Validate evaluation criteria exists
        EvaluationCriteria criteria = evaluationCriteriaRepository.findById(request.getEvaluationCriteriaId())
                .orElseThrow(() -> {
                    log.warn("Evaluation criteria not found with ID: {}", request.getEvaluationCriteriaId());
                    return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                });
        
        // Validate max score
        if (request.getMaxScore() <= 0) {
            log.warn("Invalid max score value: {}", request.getMaxScore());
            throw new AppException(ErrorCode.INVALID_MAX_SCORE);
        }
        
        // Check if question name already exists for this criteria
        if (evaluationQuestionsRepository.existsByQuestionNameAndCriteriaId(
                request.getQuestionName(), request.getEvaluationCriteriaId())) {
            log.warn("Evaluation question creation failed: Question name already exists for criteria: {}", 
                    request.getQuestionName());
            throw new AppException(ErrorCode.QUESTION_NAME_EXISTED);
        }
        
        // Convert request to entity
        EvaluationQuestions question = evaluationQuestionMapper.toEntity(request);
        question.setEvaluationCriteria(criteria);
        
        // Save question
        EvaluationQuestions savedQuestion = evaluationQuestionsRepository.save(question);
        
        log.info("Evaluation question created successfully with ID: {}", savedQuestion.getEvaluationQuestionId());
        
        return evaluationQuestionMapper.toResponse(savedQuestion);
    }
    
    /**
     * Get all evaluation questions with pagination
     */
    @Transactional(readOnly = true)
    public Page<EvaluationQuestionResponse> getAllEvaluationQuestions(Pageable pageable) {
        log.info("Getting all evaluation questions with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<EvaluationQuestions> questionPage = evaluationQuestionsRepository.findAll(pageable);
        List<EvaluationQuestionResponse> responses = evaluationQuestionMapper.toResponseList(questionPage.getContent());
        
        return new PageImpl<>(responses, pageable, questionPage.getTotalElements());
    }
    
    /**
     * Get all evaluation questions without pagination
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getAllEvaluationQuestions() {
        log.info("Getting all evaluation questions");
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findAllWithCriteria();
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Get evaluation question by ID
     */
    @Transactional(readOnly = true)
    public EvaluationQuestionResponse getEvaluationQuestionById(Long id) {
        log.info("Getting evaluation question by ID: {}", id);
        
        EvaluationQuestions question = evaluationQuestionsRepository.findByIdWithCriteria(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation question not found with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_QUESTION_NOT_FOUND);
                });
        
        return evaluationQuestionMapper.toResponse(question);
    }
    
    /**
     * Update evaluation question
     */
    public EvaluationQuestionResponse updateEvaluationQuestion(Long id, EvaluationQuestionCreateRequest request) {
        log.info("Updating evaluation question with ID: {}", id);
        
        EvaluationQuestions existingQuestion = evaluationQuestionsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation question not found for update with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_QUESTION_NOT_FOUND);
                });
        
        // Validate evaluation criteria exists if being changed
        if (!existingQuestion.getEvaluationCriteria().getEvaluationCriteriaId().equals(request.getEvaluationCriteriaId())) {
            EvaluationCriteria newCriteria = evaluationCriteriaRepository.findById(request.getEvaluationCriteriaId())
                    .orElseThrow(() -> {
                        log.warn("Evaluation criteria not found with ID: {}", request.getEvaluationCriteriaId());
                        return new AppException(ErrorCode.EVALUATION_CRITERIA_NOT_FOUND);
                    });
            existingQuestion.setEvaluationCriteria(newCriteria);
        }
        
        // Validate max score
        if (request.getMaxScore() <= 0) {
            log.warn("Invalid max score value: {}", request.getMaxScore());
            throw new AppException(ErrorCode.INVALID_MAX_SCORE);
        }
        
        // Check if question name is being changed and if new name already exists for the criteria
        if (!existingQuestion.getQuestionName().equals(request.getQuestionName()) &&
            evaluationQuestionsRepository.existsByQuestionNameAndCriteriaId(
                    request.getQuestionName(), request.getEvaluationCriteriaId())) {
            log.warn("Evaluation question update failed: Question name already exists for criteria: {}", 
                    request.getQuestionName());
            throw new AppException(ErrorCode.QUESTION_NAME_EXISTED);
        }
        
        // Update question fields
        evaluationQuestionMapper.updateEntity(request, existingQuestion);
        
        EvaluationQuestions updatedQuestion = evaluationQuestionsRepository.save(existingQuestion);
        
        log.info("Evaluation question updated successfully with ID: {}", updatedQuestion.getEvaluationQuestionId());
        
        return evaluationQuestionMapper.toResponse(updatedQuestion);
    }
    
    /**
     * Delete evaluation question
     */
    public void deleteEvaluationQuestion(Long id) {
        log.info("Deleting evaluation question with ID: {}", id);
        
        EvaluationQuestions question = evaluationQuestionsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation question not found for deletion with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_QUESTION_NOT_FOUND);
                });
        
        // Check if question has answers (would need to check EvaluationAnswersDetail)
        // For now, we'll allow deletion but in a real system, you'd check for existing answers
        // This would require a method like: evaluationAnswersDetailRepository.existsByQuestionId(id)
        
        evaluationQuestionsRepository.deleteById(id);
        
        log.info("Evaluation question deleted successfully with ID: {}", id);
    }
    
    /**
     * Get questions by criteria ID
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getQuestionsByCriteriaId(Long criteriaId) {
        log.info("Getting evaluation questions by criteria ID: {}", criteriaId);
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findByCriteriaIdWithCriteria(criteriaId);
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Get questions by multiple criteria IDs
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getQuestionsByCriteriaIds(List<Long> criteriaIds) {
        log.info("Getting evaluation questions by criteria IDs: {}", criteriaIds);
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findByCriteriaIds(criteriaIds);
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Search questions by name
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> searchQuestionsByName(String keyword) {
        log.info("Searching evaluation questions by name keyword: {}", keyword);
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.searchByQuestionName(keyword);
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Get questions by max score
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getQuestionsByMaxScore(Long maxScore) {
        log.info("Getting evaluation questions by max score: {}", maxScore);
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findByMaxScore(maxScore);
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Get questions by max score range
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getQuestionsByMaxScoreRange(Long minScore, Long maxScore) {
        log.info("Getting evaluation questions by max score range: {} to {}", minScore, maxScore);
        
        if (minScore > maxScore) {
            log.warn("Invalid score range: min {} is greater than max {}", minScore, maxScore);
            throw new AppException(ErrorCode.INVALID_MAX_SCORE);
        }
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findByMaxScoreRange(minScore, maxScore);
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Get all questions ordered by criteria and name
     */
    @Transactional(readOnly = true)
    public List<EvaluationQuestionResponse> getAllQuestionsOrdered() {
        log.info("Getting all evaluation questions ordered by criteria and name");
        
        List<EvaluationQuestions> questions = evaluationQuestionsRepository.findAllOrderedByCriteriaAndName();
        return evaluationQuestionMapper.toResponseList(questions);
    }
    
    /**
     * Count questions by criteria ID
     */
    @Transactional(readOnly = true)
    public long countQuestionsByCriteriaId(Long criteriaId) {
        log.info("Counting questions for criteria ID: {}", criteriaId);
        
        return evaluationQuestionsRepository.countByCriteriaId(criteriaId);
    }
    
    /**
     * Check if question exists
     */
    @Transactional(readOnly = true)
    public boolean questionExists(Long id) {
        return evaluationQuestionsRepository.existsById(id);
    }
    
    /**
     * Check if question name exists for criteria
     */
    @Transactional(readOnly = true)
    public boolean questionNameExistsForCriteria(String questionName, Long criteriaId) {
        return evaluationQuestionsRepository.existsByQuestionNameAndCriteriaId(questionName, criteriaId);
    }
}
