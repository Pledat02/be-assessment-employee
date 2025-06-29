package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.EvaluationStartRequest;
import com.example.assessment_employee.dto.response.EvaluationResponse;
import com.example.assessment_employee.dto.response.EvaluationSummaryResponse;
import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.EvaluationAnswersDetail;
import com.example.assessment_employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", 
        uses = {EmployeeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluationMapper {
    
    /**
     * Convert EvaluationStartRequest to EvaluationAnswers entity
     */
    @Mapping(target = "evaluationAnswerId", ignore = true)
    @Mapping(target = "totalScore", ignore = true)
    @Mapping(target = "totalScoreManager", ignore = true)
    @Mapping(target = "criteriaFormId", source = "criteriaFormId")
    @Mapping(target = "employee", ignore = true)
    EvaluationAnswers toEntity(EvaluationStartRequest request);
    
    /**
     * Convert EvaluationAnswers entity to EvaluationResponse
     */
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "criteriaFormName", ignore = true) // Will be set manually
    @Mapping(target = "evaluationDetails", ignore = true) // Will be set manually
    EvaluationResponse toResponse(EvaluationAnswers evaluationAnswer);
    
    /**
     * Convert list of EvaluationAnswers entities to list of EvaluationResponse
     */
    List<EvaluationResponse> toResponseList(List<EvaluationAnswers> evaluationAnswers);
    
    /**
     * Set Employee reference to EvaluationAnswers
     */
    @Mapping(target = "employee", source = "employee")
    void setEmployee(Employee employee, @MappingTarget EvaluationAnswers evaluationAnswer);
    
    /**
     * Convert Employee to EvaluationResponse.EmployeeInfo
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "division", source = "division")
    @Mapping(target = "staffType", source = "staffType")
    @Mapping(target = "departmentName", source = "department.departmentName")
    EvaluationResponse.EmployeeInfo toEmployeeInfo(Employee employee);
    
    /**
     * Convert EvaluationAnswersDetail to EvaluationResponse.EvaluationDetail
     */
    @Mapping(target = "question.evaluationQuestionId", source = "evaluationQuestion.evaluationQuestionId")
    @Mapping(target = "question.questionName", source = "evaluationQuestion.questionName")
    @Mapping(target = "question.maxScore", source = "evaluationQuestion.maxScore")
    @Mapping(target = "question.criteriaName", source = "evaluationQuestion.evaluationCriteria.criteriaName")
    EvaluationResponse.EvaluationDetail toEvaluationDetail(EvaluationAnswersDetail evaluationAnswersDetail);
    
    /**
     * Convert list of EvaluationAnswersDetail to list of EvaluationResponse.EvaluationDetail
     */
    List<EvaluationResponse.EvaluationDetail> toEvaluationDetailList(List<EvaluationAnswersDetail> evaluationAnswersDetails);
    
    /**
     * Convert EvaluationAnswers to EvaluationSummaryResponse
     */
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "criteriaForm", ignore = true) // Will be set manually
    @Mapping(target = "scoreSummary", ignore = true) // Will be set manually
    @Mapping(target = "criteriaSummaries", ignore = true) // Will be set manually
    @Mapping(target = "evaluationStatus", ignore = true) // Will be set manually
    EvaluationSummaryResponse toSummaryResponse(EvaluationAnswers evaluationAnswer);
    
    /**
     * Convert Employee to EvaluationSummaryResponse.EmployeeInfo
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "division", source = "division")
    @Mapping(target = "staffType", source = "staffType")
    @Mapping(target = "departmentName", source = "department.departmentName")
    EvaluationSummaryResponse.EmployeeInfo toSummaryEmployeeInfo(Employee employee);
}
