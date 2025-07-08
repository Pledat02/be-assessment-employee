package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.response.SummaryAssessmentResponse;
import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.SummaryAssessment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SummaryAssessmentMapper {

    /**
     * Ánh xạ từ SummaryAssessment sang SummaryAssessmentResponse.
     */
    @Mapping(target = "assessmentItems", source = "evaluationAnswers", qualifiedByName = "mapToAssessmentItems")
    @Mapping(target = "assessorId", source = "employee.code") // Giả định assessorId là employee.code
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "employeeId", source = "employee.code")
    @Mapping(target = "formId", source = "criteriaFormId")
    SummaryAssessmentResponse toSummaryAssessmentResponse(SummaryAssessment summaryAssessment);

    /**
     * Ánh xạ danh sách EvaluationAnswers sang danh sách SummaryAssessmentResponse.AssessmentItem.
     */
    @Named("mapToAssessmentItems")
    default List<SummaryAssessmentResponse.AssessmentItem> mapToAssessmentItems(List<EvaluationAnswers> evaluationAnswers) {
        if (evaluationAnswers == null || evaluationAnswers.isEmpty()) {
            return List.of();
        }
        return evaluationAnswers.stream()
                .map(answer -> {
                    // Vì không có EvaluationAnswersDetail, giả định score lấy từ totalScoreByEmployee
                    // hoặc một trong các score khác; questionId tạm đặt null
                    int score = answer.getTotalScoreByEmployee() != null ? answer.getTotalScoreByEmployee().intValue() :
                            answer.getTotalScoreByManager() != 0 ? answer.getTotalScoreByManager() :
                                    answer.getTotalScoreBySupervision();
                    return SummaryAssessmentResponse.AssessmentItem.builder()
                            .questionId(answer.getQuestion().getEvaluationQuestionId())
                            .score(score)
                            .build();
                })
                .collect(Collectors.toList());
    }
}