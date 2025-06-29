package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.EmployeeCreateRequest;
import com.example.assessment_employee.dto.response.EmployeeResponse;
import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.entity.Account;
import com.example.assessment_employee.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {
    
    /**
     * Convert EmployeeCreateRequest to Employee entity
     */
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);
    
    /**
     * Convert Employee entity to EmployeeResponse
     */
    @Mapping(target = "account.id", source = "account.id")
    @Mapping(target = "account.username", source = "account.username")
    @Mapping(target = "account.role", source = "account.role")
    @Mapping(target = "account.status", source = "account.status")
    @Mapping(target = "department.departmentId", source = "department.departmentId")
    @Mapping(target = "department.departmentName", source = "department.departmentName")
    @Mapping(target = "department.managerCode", source = "department.managerCode")
    EmployeeResponse toResponse(Employee employee);
    
    /**
     * Convert list of Employee entities to list of EmployeeResponse
     */
    List<EmployeeResponse> toResponseList(List<Employee> employees);
    
    /**
     * Update existing Employee entity from EmployeeCreateRequest
     */
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(EmployeeCreateRequest request, @MappingTarget Employee employee);
    
    /**
     * Convert Employee to DepartmentResponse.EmployeeInfo
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "division", source = "division")
    @Mapping(target = "staffType", source = "staffType")
    @Mapping(target = "type", source = "type")
    com.example.assessment_employee.dto.response.DepartmentResponse.EmployeeInfo toEmployeeInfo(Employee employee);
    
    /**
     * Convert list of Employee to list of DepartmentResponse.EmployeeInfo
     */
    List<com.example.assessment_employee.dto.response.DepartmentResponse.EmployeeInfo> toEmployeeInfoList(List<Employee> employees);
}
