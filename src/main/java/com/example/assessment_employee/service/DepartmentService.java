package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.DepartmentCreateRequest;
import com.example.assessment_employee.dto.response.DepartmentResponse;
import com.example.assessment_employee.entity.Department;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.DepartmentMapper;
import com.example.assessment_employee.repository.DepartmentRepository;
import com.example.assessment_employee.repository.EmployeeRepository;
import com.example.assessment_employee.repository.EvaluationCyclesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final EvaluationCyclesRepository evaluationCyclesRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * Helper method to manually map Department to DepartmentResponse
     */
    private DepartmentResponse mapToResponse(Department department) {
        DepartmentResponse response = departmentMapper.toResponse(department);

        if (department.getEvaluationCycles() != null && !department.getEvaluationCycles().isEmpty()) {
            response.setEvaluationCycles(departmentMapper.toEvaluationCycleInfoList(department.getEvaluationCycles()));
        }

        return response;
    }

    /**
     * Helper method to manually map list of Department to list of DepartmentResponse
     */
    private List<DepartmentResponse> mapToResponseList(List<Department> departments) {
        return departments.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Create new department
     */
    public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
        log.info("Creating new department: {}", request.getDepartmentName());
        
        // Check if department name already exists
        if (departmentRepository.existsByDepartmentName(request.getDepartmentName())) {
            log.warn("Department creation failed: Department name already exists: {}", request.getDepartmentName());
            throw new AppException(ErrorCode.DEPARTMENT_NAME_EXISTED);
        }
        
        // Check if manager code already exists (if provided)
        if (request.getManagerCode() != null && !request.getManagerCode().isEmpty()) {
            if (departmentRepository.existsByManagerCode(request.getManagerCode())) {
                log.warn("Department creation failed: Manager code already exists: {}", request.getManagerCode());
                throw new AppException(ErrorCode.MANAGER_CODE_EXISTED);
            }
        }
        
        // Convert request to entity
        Department department = departmentMapper.toEntity(request);
        
        // Save department
        Department savedDepartment = departmentRepository.save(department);
        
        log.info("Department created successfully with ID: {}", savedDepartment.getDepartmentId());
        
        return mapToResponse(savedDepartment);
    }
    
    /**
     * Get all departments with pagination
     */
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        log.info("Getting all departments with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Department> departmentPage = departmentRepository.findAll(pageable);
        List<DepartmentResponse> responses = mapToResponseList(departmentPage.getContent());
        
        return new PageImpl<>(responses, pageable, departmentPage.getTotalElements());
    }
    
    /**
     * Get all departments without pagination
     */
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        log.info("Getting all departments");
        
        List<Department> departments = departmentRepository.findAllWithFullInfo();
        return mapToResponseList(departments);
    }
    
    /**
     * Get department by ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        log.info("Getting department by ID: {}", id);
        
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", id);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        return mapToResponse(department);
    }
    
    /**
     * Get department by name
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByName(String departmentName) {
        log.info("Getting department by name: {}", departmentName);
        
        Department department = departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> {
                    log.warn("Department not found with name: {}", departmentName);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        return mapToResponse(department);
    }
    
    /**
     * Get department by manager code
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByManagerCode(String managerCode) {
        log.info("Getting department by manager code: {}", managerCode);
        
        Department department = departmentRepository.findByManagerCode(managerCode)
                .orElseThrow(() -> {
                    log.warn("Department not found with manager code: {}", managerCode);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        return mapToResponse(department);
    }
    
    /**
     * Update department
     */
    public DepartmentResponse updateDepartment(Long id, DepartmentCreateRequest request) {
        log.info("Updating department with ID: {}", id);
        
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Department not found for update with ID: {}", id);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        // Check if department name is being changed and if new name already exists
        if (!existingDepartment.getDepartmentName().equals(request.getDepartmentName()) &&
            departmentRepository.existsByDepartmentName(request.getDepartmentName())) {
            log.warn("Department update failed: Department name already exists: {}", request.getDepartmentName());
            throw new AppException(ErrorCode.DEPARTMENT_NAME_EXISTED);
        }
        
        // Check if manager code is being changed and if new manager code already exists
        if (request.getManagerCode() != null && !request.getManagerCode().isEmpty() &&
            !request.getManagerCode().equals(existingDepartment.getManagerCode()) &&
            departmentRepository.existsByManagerCode(request.getManagerCode())) {
            log.warn("Department update failed: Manager code already exists: {}", request.getManagerCode());
            throw new AppException(ErrorCode.MANAGER_CODE_EXISTED);
        }
        
        // Update department fields
        departmentMapper.updateEntity(request, existingDepartment);
        
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        
        log.info("Department updated successfully with ID: {}", updatedDepartment.getDepartmentId());
        
        return mapToResponse(updatedDepartment);
    }
    
    /**
     * Delete department
     */
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Department not found for deletion with ID: {}", id);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        // Check if department has employees
        if (!employeeRepository.findByDepartmentId(id).isEmpty()) {
            log.warn("Cannot delete department with existing employees: {}", id);
            throw new AppException(ErrorCode.DEPARTMENT_HAS_EMPLOYEES);
        }
        
        // Check if department has evaluation cycles
        if (!evaluationCyclesRepository.findByDepartmentId(id).isEmpty()) {
            log.warn("Cannot delete department with existing evaluation cycles: {}", id);
            throw new AppException(ErrorCode.DEPARTMENT_HAS_CYCLES);
        }
        
        departmentRepository.deleteById(id);
        
        log.info("Department deleted successfully with ID: {}", id);
    }
    
    /**
     * Search departments by name
     */
    @Transactional(readOnly = true)
    public List<DepartmentResponse> searchDepartmentsByName(String keyword) {
        log.info("Searching departments by name keyword: {}", keyword);
        
        List<Department> departments = departmentRepository.searchByDepartmentName(keyword);
        return mapToResponseList(departments);
    }
    
    /**
     * Get department with employees
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentWithEmployees(Long id) {
        log.info("Getting department with employees for ID: {}", id);
        
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", id);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        return mapToResponse(department);
    }
    
    /**
     * Get department with evaluation cycles
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentWithEvaluationCycles(Long id) {
        log.info("Getting department with evaluation cycles for ID: {}", id);
        
        Department department = departmentRepository.findByIdWithEvaluationCycles(id)
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", id);
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        return mapToResponse(department);
    }
    
    /**
     * Check if department exists
     */
    @Transactional(readOnly = true)
    public boolean departmentExists(Long id) {
        return departmentRepository.existsById(id);
    }
    
    /**
     * Check if department name exists
     */
    @Transactional(readOnly = true)
    public boolean departmentNameExists(String departmentName) {
        return departmentRepository.existsByDepartmentName(departmentName);
    }
}
