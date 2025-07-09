package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EvaluationHistoryFilterRequest;
import com.example.assessment_employee.dto.response.CycleStatisticsResponse;
import com.example.assessment_employee.dto.response.EvaluationHistoryResponse;
import com.example.assessment_employee.entity.*;
import com.example.assessment_employee.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationHistoryService {
    
    private final SummaryAssessmentRepository summaryAssessmentRepository;
    private final EvaluationCyclesRepository evaluationCyclesRepository;
    private final CriteriaFormRepository criteriaFormRepository;
    private final EvaluationCriteriaRepository evaluationCriteriaRepository;
    private final EvaluationQuestionsRepository evaluationQuestionsRepository;
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final EmployeeRepository employeeRepository;
    
    /**
     * Lấy lịch sử đánh giá với filter và phân trang
     */
    public Page<EvaluationHistoryResponse> getEvaluationHistory(EvaluationHistoryFilterRequest filter) {
        log.info("Getting evaluation history with filter: {}", filter);

        // Validate và fix sort field
        String sortField = validateSortField(filter.getSort());

        // Tạo Pageable
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getDirection()), sortField);
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Lấy dữ liệu từ repository với filter
        Page<SummaryAssessment> summaryAssessments;

        if (hasFilters(filter)) {
            summaryAssessments = findWithFilters(filter, pageable);
        } else {
            summaryAssessments = summaryAssessmentRepository.findAll(pageable);
        }

        // Convert sang response
        return summaryAssessments.map(this::convertToHistoryResponse);
    }
    
    /**
     * Lấy danh sách chu kỳ có đánh giá
     */
    public List<String> getAvailableCycles() {
        log.info("Getting available evaluation cycles");

        // Lấy tất cả chu kỳ từ EvaluationCycles
        List<EvaluationCycles> cycles = evaluationCyclesRepository.findAll();
        log.info("Found {} evaluation cycles", cycles.size());

        return cycles.stream()
                .map(cycle -> cycle.getStartDate() + " - " + cycle.getEndDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thống kê theo chu kỳ
     */
    public CycleStatisticsResponse getCycleStatistics(String cycleName) {
        log.info("Getting cycle statistics for: {}", cycleName);

        // Tìm các đánh giá trong chu kỳ này
        List<SummaryAssessment> cycleAssessments = findAssessmentsByCycleName(cycleName);

        // Tính thống kê
        int totalEvaluations = cycleAssessments.size();
        int completedEvaluations = (int) cycleAssessments.stream()
                .filter(assessment -> "COMPLETED".equals(getAssessmentStatus(assessment)))
                .count();

        double averageScore = cycleAssessments.stream()
                .mapToDouble(this::calculateAverageScore)
                .average()
                .orElse(0.0);

        return CycleStatisticsResponse.builder()
                .cycleName(cycleName)
                .totalEvaluations(totalEvaluations)
                .completedEvaluations(completedEvaluations)
                .averageScore(Math.round(averageScore * 10.0) / 10.0)
                .build();
    }
    
    /**
     * Lấy dữ liệu biểu đồ theo tiêu chí cho chu kỳ
     */
    public List<CycleStatisticsResponse.CriteriaChartDataResponse> getCriteriaChartData(String cycleName) {
        log.info("Getting criteria chart data for cycle: {}", cycleName);

        List<SummaryAssessment> cycleAssessments = findAssessmentsByCycleName(cycleName);
        return generateCriteriaChartData(cycleAssessments);
    }
    
    // Helper methods
    private String validateSortField(String sortField) {
        // Map frontend sort fields to actual entity fields
        switch (sortField) {
            case "createdAt":
                return "createdAt";
            case "updatedAt":
                return "updatedAt";
            case "employeeName":
                return "employee.fullName";
            case "averageScore":
                return "averageScore";
            default:
                return "createdAt"; // Default to createdAt
        }
    }

    private boolean hasFilters(EvaluationHistoryFilterRequest filter) {
        return filter.getSentiment() != null ||
               filter.getStatus() != null ||
               filter.getEmployeeName() != null ||
               filter.getCycleName() != null ||
               filter.getEmployeeId() != null ||
               filter.getCycleId() != null;
    }

    private Page<SummaryAssessment> findWithFilters(EvaluationHistoryFilterRequest filter, Pageable pageable) {
        // TODO: Implement custom query with filters
        // For now, use basic findAll
        return summaryAssessmentRepository.findAll(pageable);
    }

    private EvaluationHistoryResponse convertToHistoryResponse(SummaryAssessment assessment) {
        // Tính toán các thông tin cần thiết
        double averageScore = calculateAverageScore(assessment);
        String status = getAssessmentStatus(assessment);

        return EvaluationHistoryResponse.builder()
                .id(assessment.getSummaryAssessmentId())
                .employeeId(assessment.getEmployee().getCode())
                .employeeName(assessment.getEmployee().getFullName())
                .departmentName(assessment.getEmployee().getDepartment().getDepartmentName())
                .formId(assessment.getCriteriaFormId())
                .formName(getCriteriaFormName(assessment.getCriteriaFormId()))
                .cycleName(getCycleNameById(assessment.getCriteriaFormId()))
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt())
                .status(status)
                .comment(assessment.getComment())
                .averageScore(averageScore)
                .totalQuestions(getTotalQuestions(assessment))
                .completedQuestions(getCompletedQuestions(assessment))
                .sentiment(null)  // Bỏ qua logic sentiment
                .sentimentLabel(null)  // Bỏ qua logic sentiment
                .sentimentColor(null)  // Bỏ qua logic sentiment
                .assessmentItems(new ArrayList<>()) // Bỏ chi tiết đánh giá
                .build();
    }
    
    private List<SummaryAssessment> findAssessmentsByCycleName(String cycleName) {
        // Tìm assessments theo cycle name
        List<SummaryAssessment> allAssessments = summaryAssessmentRepository.findAll();
        return allAssessments.stream()
                .filter(assessment -> cycleName.equals(getCycleNameById(assessment.getCriteriaFormId())))
                .collect(Collectors.toList());
    }

    private String getAssessmentStatus(SummaryAssessment assessment) {
        // Kiểm tra status dựa trên số câu hỏi đã hoàn thành
        int totalQuestions = getTotalQuestions(assessment);
        int completedQuestions = getCompletedQuestions(assessment);

        if (completedQuestions == totalQuestions) {
            return "COMPLETED";
        } else if (completedQuestions > 0) {
            return "IN_PROGRESS";
        } else {
            return "PENDING";
        }
    }

    private double calculateAverageScore(SummaryAssessment assessment) {
        // Sử dụng averageScore đã có trong SummaryAssessment nếu có
        if (assessment.getAverageScore() != null && assessment.getAverageScore() > 0) {
            return Math.round(assessment.getAverageScore() * 10.0) / 10.0;
        }

        // Nếu không có, tính từ EvaluationAnswers sử dụng logic getAVGScore()
        List<EvaluationAnswers> answers = evaluationAnswersRepository
                .findBySummaryAssessmentId(assessment.getSummaryAssessmentId());

        if (answers.isEmpty()) {
            return 0.0;
        }

        // Sử dụng method getAVGScore() của entity để tính điểm chính xác
        double totalScore = answers.stream()
                .mapToDouble(EvaluationAnswers::getAVGScore)
                .average()
                .orElse(0.0);

        return Math.round(totalScore * 10.0) / 10.0;
    }

    private String getCriteriaFormName(Long criteriaFormId) {
        try {
            Optional<CriteriaForm> formOpt = criteriaFormRepository.findById(criteriaFormId);
            return formOpt.map(CriteriaForm::getCriteriaFormName).orElse("Unknown Form");
        } catch (Exception e) {
            log.warn("Error getting criteria form name for ID: {}", criteriaFormId);
            return "Unknown Form";
        }
    }

    private String getCycleNameById(Long criteriaFormId) {
        try {
            Optional<CriteriaForm> formOpt = criteriaFormRepository.findById(criteriaFormId);
            if (formOpt.isPresent()) {
                return getCycleName(formOpt.get());
            }
        } catch (Exception e) {
            log.warn("Error getting cycle name for criteria form ID: {}", criteriaFormId);
        }
        return "Unknown Cycle";
    }

    private String getCycleName(CriteriaForm criteriaForm) {
        if (criteriaForm != null && criteriaForm.getEvaluationCycleId() != null) {
            // Tìm EvaluationCycles bằng evaluationCycleId
            try {
                Long cycleId = Long.parseLong(criteriaForm.getEvaluationCycleId());
                Optional<EvaluationCycles> cycleOpt = evaluationCyclesRepository.findById(cycleId);
                if (cycleOpt.isPresent()) {
                    EvaluationCycles cycle = cycleOpt.get();
                    return cycle.getStartDate() + " - " + cycle.getEndDate();
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid evaluation cycle ID format: {}", criteriaForm.getEvaluationCycleId());
            }
        }
        return "Unknown Cycle";
    }

    private int getTotalQuestions(SummaryAssessment assessment) {
        // Đếm tổng số câu hỏi trong form
        return evaluationQuestionsRepository
                .countByCriteriaFormId(assessment.getCriteriaFormId());
    }

    private int getCompletedQuestions(SummaryAssessment assessment) {
        // Đếm số câu hỏi đã trả lời
        return evaluationAnswersRepository
                .countBySummaryAssessmentId(assessment.getSummaryAssessmentId());
    }





    private List<CycleStatisticsResponse.CriteriaChartDataResponse> generateCriteriaChartData(
            List<SummaryAssessment> assessments) {

        if (assessments.isEmpty()) {
            return new ArrayList<>();
        }

        // Group by criteria and calculate average scores
        Map<String, List<EvaluationAnswers>> criteriaAnswers = new HashMap<>();

        for (SummaryAssessment assessment : assessments) {
            List<EvaluationAnswers> answers = evaluationAnswersRepository
                    .findBySummaryAssessmentId(assessment.getSummaryAssessmentId());

            for (EvaluationAnswers answer : answers) {
                String criteriaName = answer.getQuestion().getEvaluationCriteria().getCriteriaName();
                criteriaAnswers.computeIfAbsent(criteriaName, k -> new ArrayList<>()).add(answer);
            }
        }

        // Calculate averages for each criteria
        return criteriaAnswers.entrySet().stream()
                .map(entry -> {
                    String criteriaName = entry.getKey();
                    List<EvaluationAnswers> answers = entry.getValue();

                    // Tính điểm trung bình cho từng role
                    double avgEmployeeScore = answers.stream()
                            .mapToDouble(EvaluationAnswers::getTotalScoreByEmployee)
                            .filter(score -> score > 0) // Chỉ tính những điểm > 0
                            .average().orElse(0.0);

                    double avgSupervisorScore = answers.stream()
                            .mapToDouble(EvaluationAnswers::getTotalScoreBySupervision)
                            .filter(score -> score > 0) // Chỉ tính những điểm > 0
                            .average().orElse(0.0);

                    double avgManagerScore = answers.stream()
                            .mapToDouble(EvaluationAnswers::getTotalScoreByManager)
                            .filter(score -> score > 0) // Chỉ tính những điểm > 0
                            .average().orElse(0.0);

                    // Sử dụng logic getAVGScore() để tính điểm tổng thể chính xác
                    double overallAverage = answers.stream()
                            .mapToDouble(EvaluationAnswers::getAVGScore)
                            .average().orElse(0.0);

                    return CycleStatisticsResponse.CriteriaChartDataResponse.builder()
                            .criteriaName(criteriaName)
                            .averageScore(Math.round(overallAverage * 10.0) / 10.0)
                            .employeeScore(Math.round(avgEmployeeScore * 10.0) / 10.0)
                            .supervisorScore(Math.round(avgSupervisorScore * 10.0) / 10.0)
                            .managerScore(Math.round(avgManagerScore * 10.0) / 10.0)
                            .color("#" + String.format("%06x", Math.abs(criteriaName.hashCode()) % 0xFFFFFF))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
