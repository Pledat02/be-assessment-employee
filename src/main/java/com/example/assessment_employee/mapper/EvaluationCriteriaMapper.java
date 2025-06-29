package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.EvaluationCriteriaCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationCriteriaResponse;
import com.example.assessment_employee.entity.EvaluationCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluationCriteriaMapper {
    
    /**
     * Convert EvaluationCriteriaCreateRequest to EvaluationCriteria entity
     */
    @Mapping(target = "evaluationCriteriaId", ignore = true)
    @Mapping(target = "evaluationQuestions", ignore = true)
    EvaluationCriteria toEntity(EvaluationCriteriaCreateRequest request);
    
    /**
     * Convert EvaluationCriteria entity to EvaluationCriteriaResponse
     */
    @Mapping(target = "evaluationQuestions", ignore = true) // Will be set manually
    EvaluationCriteriaResponse toResponse(EvaluationCriteria evaluationCriteria);
    
    /**
     * Convert list of EvaluationCriteria entities to list of EvaluationCriteriaResponse
     */
    List<EvaluationCriteriaResponse> toResponseList(List<EvaluationCriteria> evaluationCriteria);
    
    /**
     * Update existing EvaluationCriteria entity from EvaluationCriteriaCreateRequest
     */
    @Mapping(target = "evaluationCriteriaId", ignore = true)
    @Mapping(target = "evaluationQuestions", ignore = true)
    void updateEntity(EvaluationCriteriaCreateRequest request, @MappingTarget EvaluationCriteria evaluationCriteria);
    
    /**
     * Convert EvaluationCriteria to CriteriaFormResponse.EvaluationCriteriaInfo
     */
    @Mapping(target = "evaluationCriteriaId", source = "evaluationCriteriaId")
    @Mapping(target = "criteriaName", source = "criteriaName")
    @Mapping(target = "questions", ignore = true) // Will be set manually
    com.example.assessment_employee.dto.response.CriteriaFormResponse.EvaluationCriteriaInfo toEvaluationCriteriaInfo(EvaluationCriteria evaluationCriteria);

    /**
     * Convert EvaluationQuestions to EvaluationCriteriaResponse.QuestionInfo
     */
    @Mapping(target = "evaluationQuestionId", source = "evaluationQuestionId")
    @Mapping(target = "questionName", source = "questionName")
    @Mapping(target = "maxScore", source = "maxScore")
    com.example.assessment_employee.dto.response.EvaluationCriteriaResponse.QuestionInfo toQuestionInfo(com.example.assessment_employee.entity.EvaluationQuestions evaluationQuestion);

    /**
     * Convert list of EvaluationQuestions to list of EvaluationCriteriaResponse.QuestionInfo
     */
    java.util.List<com.example.assessment_employee.dto.response.EvaluationCriteriaResponse.QuestionInfo> toQuestionInfoList(java.util.List<com.example.assessment_employee.entity.EvaluationQuestions> evaluationQuestions);
    
    /**
     * Convert list of EvaluationCriteria to list of CriteriaFormResponse.EvaluationCriteriaInfo
     */
    List<com.example.assessment_employee.dto.response.CriteriaFormResponse.EvaluationCriteriaInfo> toEvaluationCriteriaInfoList(List<EvaluationCriteria> evaluationCriteria);
    
    /**
     * Convert EvaluationCriteria to EvaluationQuestionResponse.EvaluationCriteriaInfo
     */
    @Mapping(target = "evaluationCriteriaId", source = "evaluationCriteriaId")
    @Mapping(target = "criteriaName", source = "criteriaName")
    com.example.assessment_employee.dto.response.EvaluationQuestionResponse.EvaluationCriteriaInfo toCriteriaInfo(EvaluationCriteria evaluationCriteria);
}
