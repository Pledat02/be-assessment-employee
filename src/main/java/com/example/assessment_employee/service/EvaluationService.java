package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EvaluationStartRequest;
import com.example.assessment_employee.dto.request.ReviewRequest;
import com.example.assessment_employee.dto.request.SelfAssessmentRequest;
import com.example.assessment_employee.dto.response.EvaluationResponse;
import com.example.assessment_employee.dto.response.EvaluationSummaryResponse;
import com.example.assessment_employee.entity.*;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.EvaluationMapper;
import com.example.assessment_employee.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationService {
    
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final EvaluationAnswersDetailRepository evaluationAnswersDetailRepository;
    private final EmployeeRepository employeeRepository;
    private final CriteriaFormRepository criteriaFormRepository;
    private final EvaluationQuestionsRepository evaluationQuestionsRepository;
    private final EvaluationMapper evaluationMapper;
    
    /**
     * Start new evaluation for employee
     */
    public EvaluationResponse startEvaluation(EvaluationStartRequest request) {
        log.info("Starting evaluation for employee: {} with criteria form: {}", 
                request.getEmployeeCode(), request.getCriteriaFormId());
        
        // Validate employee exists
        Employee employee = employeeRepository.findById(request.getEmployeeCode())
                .orElseThrow(() -> {
                    log.warn("Employee not found with code: {}", request.getEmployeeCode());
                    return new AppException(ErrorCode.EMPLOYEE_NOT_FOUND);
                });
        
        // Validate criteria form exists
        CriteriaForm criteriaForm = criteriaFormRepository.findByIdWithEvaluationCriteria(request.getCriteriaFormId())
                .orElseThrow(() -> {
                    log.warn("Criteria form not found with ID: {}", request.getCriteriaFormId());
                    return new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND);
                });
        
        // Check if evaluation already exists for this employee and criteria form
        if (evaluationAnswersRepository.findByEmployeeIdAndCriteriaFormId(
                request.getEmployeeCode(), request.getCriteriaFormId()).isPresent()) {
            log.warn("Evaluation already exists for employee: {} and criteria form: {}", 
                    request.getEmployeeCode(), request.getCriteriaFormId());
            throw new AppException(ErrorCode.EVALUATION_ALREADY_EXISTS);
        }
        
        // Create evaluation answers
        EvaluationAnswers evaluationAnswers = evaluationMapper.toEntity(request);
        evaluationAnswers.setEmployee(employee);
        evaluationAnswers.setTotalScore(0L); // Initialize with 0
        
        EvaluationAnswers savedEvaluation = evaluationAnswersRepository.save(evaluationAnswers);
        
        // Get all questions from criteria form
        List<EvaluationQuestions> allQuestions = new ArrayList<>();
        for (EvaluationCriteria criteria : criteriaForm.getEvaluationCriteria()) {
            List<EvaluationQuestions> questions = evaluationQuestionsRepository.findByCriteriaId(criteria.getEvaluationCriteriaId());
            allQuestions.addAll(questions);
        }
        
        // Create evaluation answer details for each question
        List<EvaluationAnswersDetail> answerDetails = new ArrayList<>();
        for (EvaluationQuestions question : allQuestions) {
            EvaluationAnswersDetail detail = new EvaluationAnswersDetail();
            detail.setEvaluationAnswer(savedEvaluation);
            detail.setEvaluationQuestion(question);
            // Scores will be filled during assessment/review phases
            answerDetails.add(detail);
        }
        
        evaluationAnswersDetailRepository.saveAll(answerDetails);
        
        log.info("Evaluation started successfully with ID: {}", savedEvaluation.getEvaluationAnswerId());
        
        return getEvaluationDetail(savedEvaluation.getEvaluationAnswerId());
    }
    
    /**
     * Get evaluations for current employee (self evaluations)
     */
    @Transactional(readOnly = true)
    public Page<EvaluationResponse> getSelfEvaluations(Long employeeCode, Pageable pageable) {
        log.info("Getting self evaluations for employee: {}", employeeCode);
        
        List<EvaluationAnswers> evaluations = evaluationAnswersRepository.findByEmployeeIdWithFullInfo(employeeCode);
        
        List<EvaluationResponse> responses = new ArrayList<>();
        for (EvaluationAnswers evaluation : evaluations) {
            EvaluationResponse response = buildEvaluationResponse(evaluation);
            responses.add(response);
        }
        
        // Manual pagination (in real app, use repository pagination)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<EvaluationResponse> pageContent = responses.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, responses.size());
    }
    
    /**
     * Get evaluations pending review (for supervisors/managers)
     */
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getPendingReviews(Long reviewerEmployeeCode, String reviewerRole) {
        log.info("Getting pending reviews for reviewer: {} with role: {}", reviewerEmployeeCode, reviewerRole);
        
        // Get all pending evaluations
        List<EvaluationAnswers> pendingEvaluations = evaluationAnswersRepository.findPendingEvaluations();
        
        List<EvaluationResponse> responses = new ArrayList<>();
        for (EvaluationAnswers evaluation : pendingEvaluations) {
            // Check if reviewer has permission to review this evaluation
            if (hasReviewPermission(evaluation, reviewerEmployeeCode, reviewerRole)) {
                EvaluationResponse response = buildEvaluationResponse(evaluation);
                responses.add(response);
            }
        }
        
        return responses;
    }
    
    /**
     * Get evaluation detail by ID
     */
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationDetail(Long evaluationId) {
        log.info("Getting evaluation detail for ID: {}", evaluationId);
        
        EvaluationAnswers evaluation = evaluationAnswersRepository.findByIdWithEmployee(evaluationId)
                .orElseThrow(() -> {
                    log.warn("Evaluation not found with ID: {}", evaluationId);
                    return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                });
        
        return buildEvaluationResponse(evaluation);
    }
    
    /**
     * Submit self assessment
     */
    public EvaluationResponse submitSelfAssessment(SelfAssessmentRequest request) {
        log.info("Submitting self assessment for evaluation: {}", request.getEvaluationAnswerId());
        
        EvaluationAnswers evaluation = evaluationAnswersRepository.findById(request.getEvaluationAnswerId())
                .orElseThrow(() -> {
                    log.warn("Evaluation not found with ID: {}", request.getEvaluationAnswerId());
                    return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                });
        
        // Check if self assessment is already completed
        List<EvaluationAnswersDetail> existingDetails = evaluationAnswersDetailRepository
                .findByEvaluationAnswerIdWithEmployeeScore(request.getEvaluationAnswerId());
        if (!existingDetails.isEmpty()) {
            log.warn("Self assessment already completed for evaluation: {}", request.getEvaluationAnswerId());
            throw new AppException(ErrorCode.SELF_ASSESSMENT_COMPLETED);
        }
        
        // Update answer details with employee scores
        for (SelfAssessmentRequest.AssessmentItem item : request.getAssessmentItems()) {
            EvaluationAnswersDetail detail = evaluationAnswersDetailRepository
                    .findByEvaluationAnswerIdAndQuestionId(request.getEvaluationAnswerId(), item.getQuestionId())
                    .orElseThrow(() -> {
                        log.warn("Evaluation answer detail not found for question: {}", item.getQuestionId());
                        return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                    });
            
            // Validate score against question max score
            validateScore(item.getEmployeeScore(), detail.getEvaluationQuestion().getMaxScore());
            
            detail.setEmployeeScore(item.getEmployeeScore());
            detail.setEmployeeComment(item.getEmployeeComment());
            
            evaluationAnswersDetailRepository.save(detail);
        }
        
        // Calculate and update total score
        updateTotalScore(evaluation);
        
        log.info("Self assessment submitted successfully for evaluation: {}", request.getEvaluationAnswerId());
        
        return getEvaluationDetail(request.getEvaluationAnswerId());
    }
    
    /**
     * Submit supervisor/manager review
     */
    public EvaluationResponse submitReview(ReviewRequest request) {
        log.info("Submitting {} review for evaluation: {}", 
                request.getReviewerType(), request.getEvaluationAnswerId());
        
        EvaluationAnswers evaluation = evaluationAnswersRepository.findById(request.getEvaluationAnswerId())
                .orElseThrow(() -> {
                    log.warn("Evaluation not found with ID: {}", request.getEvaluationAnswerId());
                    return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                });
        
        // Validate reviewer type
        if (!"SUPERVISOR".equals(request.getReviewerType()) && !"MANAGER".equals(request.getReviewerType())) {
            log.warn("Invalid reviewer type: {}", request.getReviewerType());
            throw new AppException(ErrorCode.VALIDATION_ERROR);
        }
        
        // Check if review is already completed
        if ("SUPERVISOR".equals(request.getReviewerType())) {
            List<EvaluationAnswersDetail> existingDetails = evaluationAnswersDetailRepository
                    .findByEvaluationAnswerIdWithSupervisorScore(request.getEvaluationAnswerId());
            if (!existingDetails.isEmpty()) {
                log.warn("Supervisor review already completed for evaluation: {}", request.getEvaluationAnswerId());
                throw new AppException(ErrorCode.SUPERVISOR_REVIEW_COMPLETED);
            }
        } else {
            List<EvaluationAnswersDetail> existingDetails = evaluationAnswersDetailRepository
                    .findByEvaluationAnswerIdWithManagerScore(request.getEvaluationAnswerId());
            if (!existingDetails.isEmpty()) {
                log.warn("Manager review already completed for evaluation: {}", request.getEvaluationAnswerId());
                throw new AppException(ErrorCode.MANAGER_REVIEW_COMPLETED);
            }
        }
        
        // Update answer details with review scores
        for (ReviewRequest.ReviewItem item : request.getReviewItems()) {
            EvaluationAnswersDetail detail = evaluationAnswersDetailRepository
                    .findByEvaluationAnswerIdAndQuestionId(request.getEvaluationAnswerId(), item.getQuestionId())
                    .orElseThrow(() -> {
                        log.warn("Evaluation answer detail not found for question: {}", item.getQuestionId());
                        return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                    });
            
            // Validate score against question max score
            validateScore(item.getScore(), detail.getEvaluationQuestion().getMaxScore());
            
            if ("SUPERVISOR".equals(request.getReviewerType())) {
                detail.setSupervisorScore(item.getScore());
                detail.setSupervisorComment(item.getComment());
            } else {
                detail.setManagerScore(item.getScore());
                detail.setManagerComment(item.getComment());
            }
            
            evaluationAnswersDetailRepository.save(detail);
        }
        
        // If manager review, set total score manager and mark as completed
        if ("MANAGER".equals(request.getReviewerType()) && request.getTotalScoreManager() != null) {
            evaluation.setTotalScoreManager(request.getTotalScoreManager());
            evaluationAnswersRepository.save(evaluation);
        }
        
        // Update total score
        updateTotalScore(evaluation);
        
        log.info("{} review submitted successfully for evaluation: {}", 
                request.getReviewerType(), request.getEvaluationAnswerId());
        
        return getEvaluationDetail(request.getEvaluationAnswerId());
    }

    /**
     * Get evaluation summary with statistics
     */
    @Transactional(readOnly = true)
    public EvaluationSummaryResponse getEvaluationSummary(Long evaluationId) {
        log.info("Getting evaluation summary for ID: {}", evaluationId);

        EvaluationAnswers evaluation = evaluationAnswersRepository.findByIdWithEmployee(evaluationId)
                .orElseThrow(() -> {
                    log.warn("Evaluation not found with ID: {}", evaluationId);
                    return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        // Get criteria form
        CriteriaForm criteriaForm = criteriaFormRepository.findByIdWithEvaluationCriteria(evaluation.getCriteriaFormId())
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND));

        // Build summary response
        EvaluationSummaryResponse summary = evaluationMapper.toSummaryResponse(evaluation);

        // Set employee info
        summary.setEmployee(evaluationMapper.toSummaryEmployeeInfo(evaluation.getEmployee()));

        // Set criteria form info
        EvaluationSummaryResponse.CriteriaFormInfo formInfo = EvaluationSummaryResponse.CriteriaFormInfo.builder()
                .criteriaFormId(criteriaForm.getCriteriaFormId())
                .criteriaFormName(criteriaForm.getCriteriaFormName())
                .evaluationCycleId(criteriaForm.getEvaluationCycleId())
                .build();
        summary.setCriteriaForm(formInfo);

        // Calculate score summary
        EvaluationSummaryResponse.ScoreSummary scoreSummary = calculateScoreSummary(evaluationId);
        summary.setScoreSummary(scoreSummary);

        // Calculate criteria summaries
        List<EvaluationSummaryResponse.CriteriaSummary> criteriaSummaries = calculateCriteriaSummaries(evaluationId, criteriaForm);
        summary.setCriteriaSummaries(criteriaSummaries);

        // Set evaluation status
        summary.setEvaluationStatus(determineEvaluationStatus(evaluationId));

        return summary;
    }

    /**
     * Delete evaluation (only if not completed)
     */
    public void deleteEvaluation(Long evaluationId) {
        log.info("Deleting evaluation with ID: {}", evaluationId);

        EvaluationAnswers evaluation = evaluationAnswersRepository.findById(evaluationId)
                .orElseThrow(() -> {
                    log.warn("Evaluation not found for deletion with ID: {}", evaluationId);
                    return new AppException(ErrorCode.EVALUATION_NOT_FOUND);
                });

        // Check if evaluation is completed
        if (evaluation.getTotalScoreManager() != null) {
            log.warn("Cannot delete completed evaluation: {}", evaluationId);
            throw new AppException(ErrorCode.EVALUATION_ALREADY_COMPLETED);
        }

        // Delete answer details first
        List<EvaluationAnswersDetail> details = evaluationAnswersDetailRepository.findByEvaluationAnswerId(evaluationId);
        evaluationAnswersDetailRepository.deleteAll(details);

        // Delete evaluation
        evaluationAnswersRepository.deleteById(evaluationId);

        log.info("Evaluation deleted successfully with ID: {}", evaluationId);
    }

    /**
     * Get evaluations by employee
     */
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByEmployee(Long employeeCode) {
        log.info("Getting evaluations for employee: {}", employeeCode);

        List<EvaluationAnswers> evaluations = evaluationAnswersRepository.findByEmployeeIdWithFullInfo(employeeCode);

        List<EvaluationResponse> responses = new ArrayList<>();
        for (EvaluationAnswers evaluation : evaluations) {
            EvaluationResponse response = buildEvaluationResponse(evaluation);
            responses.add(response);
        }

        return responses;
    }

    /**
     * Get completed evaluations
     */
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getCompletedEvaluations() {
        log.info("Getting completed evaluations");

        List<EvaluationAnswers> evaluations = evaluationAnswersRepository.findCompletedEvaluations();

        List<EvaluationResponse> responses = new ArrayList<>();
        for (EvaluationAnswers evaluation : evaluations) {
            EvaluationResponse response = buildEvaluationResponse(evaluation);
            responses.add(response);
        }

        return responses;
    }

    /**
     * Get evaluations by department
     */
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByDepartment(Long departmentId) {
        log.info("Getting evaluations for department: {}", departmentId);

        List<EvaluationAnswers> evaluations = evaluationAnswersRepository.findByDepartmentId(departmentId);

        List<EvaluationResponse> responses = new ArrayList<>();
        for (EvaluationAnswers evaluation : evaluations) {
            EvaluationResponse response = buildEvaluationResponse(evaluation);
            responses.add(response);
        }

        return responses;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Build evaluation response with all details
     */
    private EvaluationResponse buildEvaluationResponse(EvaluationAnswers evaluation) {
        EvaluationResponse response = evaluationMapper.toResponse(evaluation);

        // Set employee info
        response.setEmployee(evaluationMapper.toEmployeeInfo(evaluation.getEmployee()));

        // Get criteria form name
        CriteriaForm criteriaForm = criteriaFormRepository.findById(evaluation.getCriteriaFormId())
                .orElse(null);
        if (criteriaForm != null) {
            response.setCriteriaFormName(criteriaForm.getCriteriaFormName());
        }

        // Get evaluation details
        List<EvaluationAnswersDetail> details = evaluationAnswersDetailRepository
                .findByEvaluationAnswerId(evaluation.getEvaluationAnswerId());
        response.setEvaluationDetails(evaluationMapper.toEvaluationDetailList(details));

        return response;
    }

    /**
     * Check if reviewer has permission to review this evaluation
     */
    private boolean hasReviewPermission(EvaluationAnswers evaluation, Long reviewerEmployeeCode, String reviewerRole) {
        // For simplicity, assume:
        // - SUPERVISOR can review employees in same department
        // - MANAGER can review all employees in their department

        if ("SUPERVISOR".equals(reviewerRole) || "MANAGER".equals(reviewerRole)) {
            // Get reviewer's department
            Employee reviewer = employeeRepository.findById(reviewerEmployeeCode).orElse(null);
            if (reviewer != null && evaluation.getEmployee().getDepartment() != null) {
                return reviewer.getDepartment().getDepartmentId()
                        .equals(evaluation.getEmployee().getDepartment().getDepartmentId());
            }
        }

        return false;
    }

    /**
     * Validate score against maximum allowed
     */
    private void validateScore(Long score, Long maxScore) {
        if (score < 0) {
            throw new AppException(ErrorCode.INVALID_SCORE_VALUE);
        }
        if (score > maxScore) {
            throw new AppException(ErrorCode.SCORE_EXCEEDS_MAXIMUM);
        }
    }

    /**
     * Update total score for evaluation
     */
    private void updateTotalScore(EvaluationAnswers evaluation) {
        // Calculate total score from employee scores
        Double averageScore = evaluationAnswersDetailRepository
                .calculateAverageEmployeeScore(evaluation.getEvaluationAnswerId());

        if (averageScore != null) {
            evaluation.setTotalScore(Math.round(averageScore));
            evaluationAnswersRepository.save(evaluation);
        }
    }

    /**
     * Calculate score summary for evaluation
     */
    private EvaluationSummaryResponse.ScoreSummary calculateScoreSummary(Long evaluationId) {
        Long totalQuestions = evaluationAnswersDetailRepository.countByEvaluationAnswerId(evaluationId);

        Double avgEmployeeScore = evaluationAnswersDetailRepository.calculateAverageEmployeeScore(evaluationId);
        Double avgSupervisorScore = evaluationAnswersDetailRepository.calculateAverageSupervisorScore(evaluationId);
        Double avgManagerScore = evaluationAnswersDetailRepository.calculateAverageManagerScore(evaluationId);

        // Count completed questions (those with employee score)
        Long completedQuestions = (long) evaluationAnswersDetailRepository
                .findByEvaluationAnswerIdWithEmployeeScore(evaluationId).size();

        EvaluationAnswers evaluation = evaluationAnswersRepository.findById(evaluationId).orElse(null);

        return EvaluationSummaryResponse.ScoreSummary.builder()
                .totalScore(evaluation != null ? evaluation.getTotalScore() : null)
                .totalScoreManager(evaluation != null ? evaluation.getTotalScoreManager() : null)
                .averageEmployeeScore(avgEmployeeScore)
                .averageSupervisorScore(avgSupervisorScore)
                .averageManagerScore(avgManagerScore)
                .totalQuestions(totalQuestions)
                .completedQuestions(completedQuestions)
                .build();
    }

    /**
     * Calculate criteria summaries
     */
    private List<EvaluationSummaryResponse.CriteriaSummary> calculateCriteriaSummaries(
            Long evaluationId, CriteriaForm criteriaForm) {

        List<EvaluationSummaryResponse.CriteriaSummary> summaries = new ArrayList<>();

        for (EvaluationCriteria criteria : criteriaForm.getEvaluationCriteria()) {
            // Get questions for this criteria
            List<EvaluationQuestions> questions = evaluationQuestionsRepository
                    .findByCriteriaId(criteria.getEvaluationCriteriaId());

            if (!questions.isEmpty()) {
                // Get answer details for these questions
                List<Long> questionIds = questions.stream()
                        .map(EvaluationQuestions::getEvaluationQuestionId)
                        .collect(Collectors.toList());

                // Calculate averages for this criteria (simplified calculation)
                Long totalQuestions = (long) questions.size();
                Long maxPossibleScore = questions.stream()
                        .mapToLong(EvaluationQuestions::getMaxScore)
                        .sum();

                EvaluationSummaryResponse.CriteriaSummary summary = EvaluationSummaryResponse.CriteriaSummary.builder()
                        .criteriaName(criteria.getCriteriaName())
                        .totalQuestions(totalQuestions)
                        .maxPossibleScore(maxPossibleScore)
                        .averageEmployeeScore(0.0) // Simplified - would need complex calculation
                        .averageSupervisorScore(0.0)
                        .averageManagerScore(0.0)
                        .build();

                summaries.add(summary);
            }
        }

        return summaries;
    }

    /**
     * Determine evaluation status
     */
    private String determineEvaluationStatus(Long evaluationId) {
        EvaluationAnswers evaluation = evaluationAnswersRepository.findById(evaluationId).orElse(null);
        if (evaluation == null) {
            return "NOT_FOUND";
        }

        // Check if manager review is completed
        if (evaluation.getTotalScoreManager() != null) {
            return "COMPLETED";
        }

        // Check if supervisor review is completed
        List<EvaluationAnswersDetail> supervisorReviews = evaluationAnswersDetailRepository
                .findByEvaluationAnswerIdWithSupervisorScore(evaluationId);
        if (!supervisorReviews.isEmpty()) {
            return "SUPERVISOR_REVIEWED";
        }

        // Check if self assessment is completed
        List<EvaluationAnswersDetail> selfAssessments = evaluationAnswersDetailRepository
                .findByEvaluationAnswerIdWithEmployeeScore(evaluationId);
        if (!selfAssessments.isEmpty()) {
            return "SELF_ASSESSED";
        }

        return "STARTED";
    }
}
