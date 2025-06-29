package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.DepartmentCreateRequest;
import com.example.assessment_employee.dto.response.DepartmentResponse;
import com.example.assessment_employee.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EmployeeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {
    
    /**
     * Convert DepartmentCreateRequest to Department entity
     */
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "evaluationCycles", ignore = true)
    Department toEntity(DepartmentCreateRequest request);
    
    /**
     * Convert Department entity to DepartmentResponse
     */
    @Mapping(target = "employees", source = "employees")
    @Mapping(target = "evaluationCycles", ignore = true) // Will be set manually
    DepartmentResponse toResponse(Department department);
    
    /**
     * Convert list of Department entities to list of DepartmentResponse
     */
    List<DepartmentResponse> toResponseList(List<Department> departments);
    
    /**
     * Update existing Department entity from DepartmentCreateRequest
     */
    @Mapping(target = "departmentId", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "evaluationCycles", ignore = true)
    void updateEntity(DepartmentCreateRequest request, @MappingTarget Department department);
    
    /**
     * Convert Department to EvaluationCycleResponse.DepartmentInfo
     */
    @Mapping(target = "departmentId", source = "departmentId")
    @Mapping(target = "departmentName", source = "departmentName")
    @Mapping(target = "managerCode", source = "managerCode")
    com.example.assessment_employee.dto.response.EvaluationCycleResponse.DepartmentInfo toDepartmentInfo(Department department);

    /**
     * Convert EvaluationCycles to DepartmentResponse.EvaluationCycleInfo
     */
    @Mapping(target = "evaluationCycleId", source = "evaluationCycleId")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", source = "status")
    com.example.assessment_employee.dto.response.DepartmentResponse.EvaluationCycleInfo toEvaluationCycleInfo(com.example.assessment_employee.entity.EvaluationCycles evaluationCycle);

    /**
     * Convert list of EvaluationCycles to list of DepartmentResponse.EvaluationCycleInfo
     */
    java.util.List<com.example.assessment_employee.dto.response.DepartmentResponse.EvaluationCycleInfo> toEvaluationCycleInfoList(java.util.List<com.example.assessment_employee.entity.EvaluationCycles> evaluationCycles);
}
