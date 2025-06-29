package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.EvaluationQuestionCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationQuestionResponse;
import com.example.assessment_employee.entity.EvaluationQuestions;
import com.example.assessment_employee.entity.EvaluationCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluationQuestionMapper {
    
    /**
     * Convert EvaluationQuestionCreateRequest to EvaluationQuestions entity
     */
    @Mapping(target = "evaluationQuestionId", ignore = true)
    @Mapping(target = "evaluationCriteria", ignore = true)
    EvaluationQuestions toEntity(EvaluationQuestionCreateRequest request);
    
    /**
     * Convert EvaluationQuestions entity to EvaluationQuestionResponse
     */
    @Mapping(target = "evaluationCriteria.evaluationCriteriaId", source = "evaluationCriteria.evaluationCriteriaId")
    @Mapping(target = "evaluationCriteria.criteriaName", source = "evaluationCriteria.criteriaName")
    EvaluationQuestionResponse toResponse(EvaluationQuestions evaluationQuestion);
    
    /**
     * Convert list of EvaluationQuestions entities to list of EvaluationQuestionResponse
     */
    List<EvaluationQuestionResponse> toResponseList(List<EvaluationQuestions> evaluationQuestions);
    
    /**
     * Update existing EvaluationQuestions entity from EvaluationQuestionCreateRequest
     */
    @Mapping(target = "evaluationQuestionId", ignore = true)
    @Mapping(target = "evaluationCriteria", ignore = true)
    void updateEntity(EvaluationQuestionCreateRequest request, @MappingTarget EvaluationQuestions evaluationQuestion);
    
    /**
     * Set EvaluationCriteria reference to EvaluationQuestions
     */
    @Mapping(target = "evaluationCriteria", source = "evaluationCriteria")
    void setEvaluationCriteria(EvaluationCriteria evaluationCriteria, @MappingTarget EvaluationQuestions evaluationQuestion);
    
    /**
     * Convert EvaluationQuestions to EvaluationCriteriaResponse.QuestionInfo
     */
    @Mapping(target = "evaluationQuestionId", source = "evaluationQuestionId")
    @Mapping(target = "questionName", source = "questionName")
    @Mapping(target = "maxScore", source = "maxScore")
    com.example.assessment_employee.dto.response.EvaluationCriteriaResponse.QuestionInfo toQuestionInfo(EvaluationQuestions evaluationQuestion);
    
    /**
     * Convert list of EvaluationQuestions to list of EvaluationCriteriaResponse.QuestionInfo
     */
    List<com.example.assessment_employee.dto.response.EvaluationCriteriaResponse.QuestionInfo> toQuestionInfoList(List<EvaluationQuestions> evaluationQuestions);
    
    /**
     * Convert EvaluationQuestions to CriteriaFormResponse.QuestionInfo
     */
    @Mapping(target = "evaluationQuestionId", source = "evaluationQuestionId")
    @Mapping(target = "questionName", source = "questionName")
    @Mapping(target = "maxScore", source = "maxScore")
    com.example.assessment_employee.dto.response.CriteriaFormResponse.QuestionInfo toCriteriaFormQuestionInfo(EvaluationQuestions evaluationQuestion);
    
    /**
     * Convert list of EvaluationQuestions to list of CriteriaFormResponse.QuestionInfo
     */
    List<com.example.assessment_employee.dto.response.CriteriaFormResponse.QuestionInfo> toCriteriaFormQuestionInfoList(List<EvaluationQuestions> evaluationQuestions);
}
