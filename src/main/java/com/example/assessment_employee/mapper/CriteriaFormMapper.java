package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.CriteriaFormCreateRequest;
import com.example.assessment_employee.dto.response.CriteriaFormResponse;
import com.example.assessment_employee.entity.CriteriaForm;
import com.example.assessment_employee.entity.EvaluationCriteria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CriteriaFormMapper {
    
    /**
     * Convert CriteriaFormCreateRequest to CriteriaForm entity
     */
    @Mapping(target = "criteriaFormId", ignore = true)
    @Mapping(target = "evaluationCriteria", ignore = true)
    CriteriaForm toEntity(CriteriaFormCreateRequest request);
    
    /**
     * Convert CriteriaForm entity to CriteriaFormResponse
     */
    @Mapping(target = "evaluationCriteria", ignore = true) // Will be set manually
    CriteriaFormResponse toResponse(CriteriaForm criteriaForm);
    
    /**
     * Convert list of CriteriaForm entities to list of CriteriaFormResponse
     */
    List<CriteriaFormResponse> toResponseList(List<CriteriaForm> criteriaForms);
    
    /**
     * Update existing CriteriaForm entity from CriteriaFormCreateRequest
     */
    @Mapping(target = "criteriaFormId", ignore = true)
    @Mapping(target = "evaluationCriteria", ignore = true)
    void updateEntity(CriteriaFormCreateRequest request, @MappingTarget CriteriaForm criteriaForm);
}
