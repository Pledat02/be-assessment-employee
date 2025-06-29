package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.EvaluationCycleCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationCycleResponse;
import com.example.assessment_employee.entity.EvaluationCycles;
import com.example.assessment_employee.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluationCycleMapper {
    
    /**
     * Convert EvaluationCycleCreateRequest to EvaluationCycles entity
     */
    @Mapping(target = "evaluationCycleId", ignore = true)
    @Mapping(target = "department", ignore = true)
    EvaluationCycles toEntity(EvaluationCycleCreateRequest request);
    
    /**
     * Convert EvaluationCycles entity to EvaluationCycleResponse
     */
    @Mapping(target = "department.departmentId", source = "department.departmentId")
    @Mapping(target = "department.departmentName", source = "department.departmentName")
    @Mapping(target = "department.managerCode", source = "department.managerCode")
    EvaluationCycleResponse toResponse(EvaluationCycles evaluationCycle);
    
    /**
     * Convert list of EvaluationCycles entities to list of EvaluationCycleResponse
     */
    List<EvaluationCycleResponse> toResponseList(List<EvaluationCycles> evaluationCycles);
    
    /**
     * Update existing EvaluationCycles entity from EvaluationCycleCreateRequest
     */
    @Mapping(target = "evaluationCycleId", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(EvaluationCycleCreateRequest request, @MappingTarget EvaluationCycles evaluationCycle);
    
    /**
     * Set Department reference to EvaluationCycles
     */
    @Mapping(target = "department", source = "department")
    void setDepartment(Department department, @MappingTarget EvaluationCycles evaluationCycle);
    
    /**
     * Convert EvaluationCycles to DepartmentResponse.EvaluationCycleInfo
     */
    @Mapping(target = "evaluationCycleId", source = "evaluationCycleId")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", source = "status")
    com.example.assessment_employee.dto.response.DepartmentResponse.EvaluationCycleInfo toEvaluationCycleInfo(EvaluationCycles evaluationCycle);
    
    /**
     * Convert list of EvaluationCycles to list of DepartmentResponse.EvaluationCycleInfo
     */
    List<com.example.assessment_employee.dto.response.DepartmentResponse.EvaluationCycleInfo> toEvaluationCycleInfoList(List<EvaluationCycles> evaluationCycles);
}
