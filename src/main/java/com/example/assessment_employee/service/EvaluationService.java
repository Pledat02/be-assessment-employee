package com.example.assessment_employee.service;

import com.example.assessment_employee.constants.RoleConstants;
import com.example.assessment_employee.dto.request.AssessmentRequest;
import com.example.assessment_employee.dto.request.SentimentRequest;
import com.example.assessment_employee.dto.response.SentimentResponse;
import com.example.assessment_employee.dto.response.SummaryAssessmentResponse;
import com.example.assessment_employee.entity.*;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.SummaryAssessmentMapper;
import com.example.assessment_employee.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EvaluationService {

    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final EmployeeRepository employeeRepository;
    private final CriteriaFormRepository criteriaFormRepository;
    private final EvaluationQuestionsRepository evaluationQuestionsRepository;
    private final SummaryAssessmentRepository summaryAssessmentRepository;
    private final SummaryAssessmentMapper summaryAssessmentMapper;
    SentimentAnalysisClient sentimentAnalysisClient;

    /**
     * Submits an assessment for an employee by an assessor (employee, manager, or supervisor).
     */
    @Transactional
    public SummaryAssessmentResponse submitAssessment(AssessmentRequest request) {
        log.info("Submitting assessment for employee ID: {}", request.getEmployeeId());

        // Validate employee, criteria form, and assessor
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
        CriteriaForm criteriaForm = criteriaFormRepository.findById(request.getFormId())
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND));
        Employee assessor = employeeRepository.findById(request.getAssessorId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSESSOR_NOT_FOUND));

        // Validate assessment items
        if (request.getAssessmentItems() == null || request.getAssessmentItems().isEmpty()) {
            throw new AppException(ErrorCode.EVALUATION_QUESTION_NOT_FOUND);
        }

        // Create or retrieve SummaryAssessment
        SummaryAssessment summaryAssessment = summaryAssessmentRepository
                .findByEmployeeAndCriteriaFormId(employee, criteriaForm.getCriteriaFormId())
                .orElse(SummaryAssessment.builder()
                        .employee(employee)
                        .criteriaFormId(criteriaForm.getCriteriaFormId())
                        .evaluationAnswers(new ArrayList<>())
                        .build());

        // Process assessment items and create EvaluationAnswers
        List<EvaluationAnswers> evaluationAnswersList = request.getAssessmentItems().stream()
                .map(item -> {
                    EvaluationQuestions question = evaluationQuestionsRepository.findById(item.getQuestionId())
                            .orElseThrow(() -> new AppException(ErrorCode.EVALUATION_QUESTION_NOT_FOUND));
                    if (item.getEmployeeScore() > question.getMaxScore()
                    || item.getManagerScore() > question.getMaxScore()
                            || item.getSupervisorScore() > question.getMaxScore()) {
                        throw new AppException(ErrorCode.INVALID_MAX_SCORE);
                    }
                    EvaluationAnswers answer = evaluationAnswersRepository
                            .findBySummaryAssessmentIdAndQuestionId(summaryAssessment.getSummaryAssessmentId(), item.getQuestionId())
                            .orElse(EvaluationAnswers.builder()
                                    .question(question)
                                    .summaryAssessment(summaryAssessment)
                                    .build());

                    // Set score based on assessor's role
                    if (isEmployeeAssessor(assessor, employee)) {
                        answer.setTotalScoreByEmployee(item.getEmployeeScore());
                    } else if (isManagerAssessor(assessor, employee)) {
                        answer.setTotalScoreByManager(item.getManagerScore());
                    } else if (isSupervisorAssessor(assessor, employee)) {
                        answer.setTotalScoreBySupervision(item.getSupervisorScore());
                    } else {
                        throw new AppException(ErrorCode.VALIDATION_ERROR);
                    }
                    answer.setTotalScore(answer.getAVGScore());
                    return answer;
                })
                .toList();

        // Add evaluation answers to summary assessment
        summaryAssessment.getEvaluationAnswers().addAll(evaluationAnswersList);
        // set score;
        summaryAssessment.setAverageScore(evaluationAnswersList.stream()
                .mapToDouble(EvaluationAnswers::getAVGScore)
                .sum());
        // sentiment
        SentimentResponse sentimentResponse =sentimentAnalysisClient.analyzeSentiment(
                SentimentRequest.builder()
                        .comment(request.getComment())
                        .build());
        summaryAssessment.setSentiment(sentimentResponse.getSentiment()
        );
        // Set comment if provided
        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            summaryAssessment.setComment(request.getComment());
        }

        // Save entities
        evaluationAnswersRepository.saveAll(evaluationAnswersList);
        summaryAssessmentRepository.save(summaryAssessment);

        // Map to DTO and return
        return summaryAssessmentMapper.toSummaryAssessmentResponse(summaryAssessment);
    }

    /**
     * Retrieves an existing assessment for a given employee and criteria form.
     *
     * @param formId     the ID of the criteria form
     * @param employeeId the ID of the employee being assessed
     * @return the summary assessment response if found
     */
    @Transactional(readOnly = true)
    public SummaryAssessmentResponse getAssessmentByFormAndEmployeeId(Long formId, Long employeeId) {
        log.info("Fetching assessment for employee ID: {} and form ID: {}", employeeId, formId);

        // Validate employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // Validate form existence (optional: comment out if you don't want to check)
        CriteriaForm criteriaForm = criteriaFormRepository.findById(formId)
                .orElseThrow(() -> new AppException(ErrorCode.CRITERIA_FORM_NOT_FOUND));

        // Find the summary assessment
        SummaryAssessment summaryAssessment = summaryAssessmentRepository
                .findByEmployeeAndCriteriaFormId(employee, formId)
                .orElseThrow(() -> {
                    log.warn("Assessment not found for employee ID {} and form ID {}", employeeId, formId);
                    return new AppException(ErrorCode.SUMMARY_ASSESSMENT_NOT_FOUND);
                });

        return summaryAssessmentMapper.toSummaryAssessmentResponse(summaryAssessment);
    }



    /**
     * Checks if the assessor is the same as the employee (self-assessment).
     */
    private boolean isEmployeeAssessor(Employee assessor, Employee employee) {
        return assessor.getCode().equals(employee.getCode());
    }

    /**
     * Checks if the assessor is the employee's manager.
     */
    private boolean isManagerAssessor(Employee assessor, Employee employee) {
        Department department = employee.getDepartment();
        return department != null && department.getManagerCode() != null
                && Objects.equals(department.getDepartmentId(), assessor.getDepartment().getDepartmentId())
                && assessor.getAccount().getRole().equals(RoleConstants.MANAGER);
    }

    /**
     * Checks if the assessor is a supervisor in the same department.
     */
    private boolean isSupervisorAssessor(Employee assessor, Employee employee) {
        return assessor.getAccount() != null
                && assessor.getAccount().getRole().equals(RoleConstants.SUPERVISOR)
                && Objects.equals(assessor.getDepartment().getDepartmentId(),
                employee.getDepartment().getDepartmentId());
    }
}