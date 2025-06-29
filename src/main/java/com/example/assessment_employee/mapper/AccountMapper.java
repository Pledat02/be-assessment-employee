package com.example.assessment_employee.mapper;

import com.example.assessment_employee.dto.request.AccountCreateRequest;
import com.example.assessment_employee.dto.response.AccountResponse;
import com.example.assessment_employee.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {
    
    /**
     * Convert AccountCreateRequest to Account entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    Account toEntity(AccountCreateRequest request);
    
    /**
     * Convert Account entity to AccountResponse
     */
    @Mapping(target = "employee.code", source = "employee.code")
    @Mapping(target = "employee.fullName", source = "employee.fullName")
    @Mapping(target = "employee.division", source = "employee.division")
    @Mapping(target = "employee.basic", source = "employee.basic")
    @Mapping(target = "employee.staffType", source = "employee.staffType")
    @Mapping(target = "employee.startDate", source = "employee.startDate")
    @Mapping(target = "employee.type", source = "employee.type")
    @Mapping(target = "employee.departmentName", source = "employee.department.departmentName")
    AccountResponse toResponse(Account account);
    
    /**
     * Convert list of Account entities to list of AccountResponse
     */
    List<AccountResponse> toResponseList(List<Account> accounts);
    
    /**
     * Update existing Account entity from AccountCreateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    void updateEntity(AccountCreateRequest request, @MappingTarget Account account);
    
    /**
     * Convert Account to LoginResponse.UserInfo
     */
    @Mapping(target = "employee.code", source = "employee.code")
    @Mapping(target = "employee.fullName", source = "employee.fullName")
    @Mapping(target = "employee.division", source = "employee.division")
    @Mapping(target = "employee.staffType", source = "employee.staffType")
    @Mapping(target = "employee.departmentName", source = "employee.department.departmentName")
    com.example.assessment_employee.dto.response.LoginResponse.UserInfo toUserInfo(Account account);
}
