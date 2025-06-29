package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.request.EvaluationCycleCreateRequest;
import com.example.assessment_employee.dto.response.EvaluationCycleResponse;
import com.example.assessment_employee.entity.Department;
import com.example.assessment_employee.entity.EvaluationCycles;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.mapper.EvaluationCycleMapper;
import com.example.assessment_employee.repository.DepartmentRepository;
import com.example.assessment_employee.repository.EvaluationCyclesRepository;
import com.example.assessment_employee.repository.EvaluationAnswersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationCycleService {
    
    private final EvaluationCyclesRepository evaluationCyclesRepository;
    private final DepartmentRepository departmentRepository;
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final EvaluationCycleMapper evaluationCycleMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Create new evaluation cycle
     */
    public EvaluationCycleResponse createEvaluationCycle(EvaluationCycleCreateRequest request) {
        log.info("Creating new evaluation cycle for department: {}", request.getDepartmentId());
        
        // Validate department exists
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> {
                    log.warn("Department not found with ID: {}", request.getDepartmentId());
                    return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                });
        
        // Validate date format and range
        validateDateRange(request.getStartDate(), request.getEndDate());
        
        // Check for overlapping cycles in the same department
        checkForOverlappingCycles(request.getDepartmentId(), request.getStartDate(), request.getEndDate(), null);
        
        // Convert request to entity
        EvaluationCycles evaluationCycle = evaluationCycleMapper.toEntity(request);
        evaluationCycle.setDepartment(department);
        
        // Save evaluation cycle
        EvaluationCycles savedCycle = evaluationCyclesRepository.save(evaluationCycle);
        
        log.info("Evaluation cycle created successfully with ID: {}", savedCycle.getEvaluationCycleId());
        
        return evaluationCycleMapper.toResponse(savedCycle);
    }
    
    /**
     * Get all evaluation cycles with pagination
     */
    @Transactional(readOnly = true)
    public Page<EvaluationCycleResponse> getAllEvaluationCycles(Pageable pageable) {
        log.info("Getting all evaluation cycles with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<EvaluationCycles> cyclePage = evaluationCyclesRepository.findAll(pageable);
        List<EvaluationCycleResponse> responses = evaluationCycleMapper.toResponseList(cyclePage.getContent());
        
        return new PageImpl<>(responses, pageable, cyclePage.getTotalElements());
    }
    
    /**
     * Get all evaluation cycles without pagination
     */
    @Transactional(readOnly = true)
    public List<EvaluationCycleResponse> getAllEvaluationCycles() {
        log.info("Getting all evaluation cycles");
        
        List<EvaluationCycles> cycles = evaluationCyclesRepository.findAllWithDepartment();
        return evaluationCycleMapper.toResponseList(cycles);
    }
    
    /**
     * Get evaluation cycle by ID
     */
    @Transactional(readOnly = true)
    public EvaluationCycleResponse getEvaluationCycleById(Long id) {
        log.info("Getting evaluation cycle by ID: {}", id);
        
        EvaluationCycles cycle = evaluationCyclesRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation cycle not found with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CYCLE_NOT_FOUND);
                });
        
        return evaluationCycleMapper.toResponse(cycle);
    }
    
    /**
     * Update evaluation cycle
     */
    public EvaluationCycleResponse updateEvaluationCycle(Long id, EvaluationCycleCreateRequest request) {
        log.info("Updating evaluation cycle with ID: {}", id);
        
        EvaluationCycles existingCycle = evaluationCyclesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation cycle not found for update with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CYCLE_NOT_FOUND);
                });
        
        // Check if cycle can be modified (not completed)
        if ("COMPLETED".equals(existingCycle.getStatus())) {
            log.warn("Cannot modify completed evaluation cycle: {}", id);
            throw new AppException(ErrorCode.EVALUATION_CYCLE_COMPLETED);
        }
        
        // Validate department exists if being changed
        if (!existingCycle.getDepartment().getDepartmentId().equals(request.getDepartmentId())) {
            Department newDepartment = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        log.warn("Department not found with ID: {}", request.getDepartmentId());
                        return new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
                    });
            existingCycle.setDepartment(newDepartment);
        }
        
        // Validate date format and range
        validateDateRange(request.getStartDate(), request.getEndDate());
        
        // Check for overlapping cycles (excluding current cycle)
        checkForOverlappingCycles(request.getDepartmentId(), request.getStartDate(), request.getEndDate(), id);
        
        // Update cycle fields
        evaluationCycleMapper.updateEntity(request, existingCycle);
        
        EvaluationCycles updatedCycle = evaluationCyclesRepository.save(existingCycle);
        
        log.info("Evaluation cycle updated successfully with ID: {}", updatedCycle.getEvaluationCycleId());
        
        return evaluationCycleMapper.toResponse(updatedCycle);
    }
    
    /**
     * Update evaluation cycle status
     */
    public EvaluationCycleResponse updateEvaluationCycleStatus(Long id, String status) {
        log.info("Updating evaluation cycle status for ID: {} to status: {}", id, status);
        
        // Validate status
        if (!isValidStatus(status)) {
            log.warn("Invalid evaluation cycle status: {}", status);
            throw new AppException(ErrorCode.INVALID_CYCLE_STATUS);
        }
        
        EvaluationCycles cycle = evaluationCyclesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation cycle not found for status update with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CYCLE_NOT_FOUND);
                });
        
        // Validate status transition
        validateStatusTransition(cycle.getStatus(), status);
        
        cycle.setStatus(status);
        EvaluationCycles updatedCycle = evaluationCyclesRepository.save(cycle);
        
        log.info("Evaluation cycle status updated successfully for ID: {}", id);
        
        return evaluationCycleMapper.toResponse(updatedCycle);
    }
    
    /**
     * Delete evaluation cycle
     */
    public void deleteEvaluationCycle(Long id) {
        log.info("Deleting evaluation cycle with ID: {}", id);
        
        EvaluationCycles cycle = evaluationCyclesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluation cycle not found for deletion with ID: {}", id);
                    return new AppException(ErrorCode.EVALUATION_CYCLE_NOT_FOUND);
                });
        
        // Check if cycle is active or completed
        if ("ACTIVE".equals(cycle.getStatus())) {
            log.warn("Cannot delete active evaluation cycle: {}", id);
            throw new AppException(ErrorCode.EVALUATION_CYCLE_ACTIVE);
        }
        
        if ("COMPLETED".equals(cycle.getStatus())) {
            log.warn("Cannot delete completed evaluation cycle: {}", id);
            throw new AppException(ErrorCode.EVALUATION_CYCLE_COMPLETED);
        }
        
        // Check if cycle has evaluations (through criteria forms)
        // This would require checking CriteriaForm and EvaluationAnswers
        // For now, we'll allow deletion of DRAFT cycles
        
        evaluationCyclesRepository.deleteById(id);
        
        log.info("Evaluation cycle deleted successfully with ID: {}", id);
    }
    
    /**
     * Get evaluation cycles by department
     */
    @Transactional(readOnly = true)
    public List<EvaluationCycleResponse> getEvaluationCyclesByDepartment(Long departmentId) {
        log.info("Getting evaluation cycles by department ID: {}", departmentId);

        List<EvaluationCycles> cycles = evaluationCyclesRepository.findByDepartmentId(departmentId);
        return evaluationCycleMapper.toResponseList(cycles);
    }
    
    /**
     * Get evaluation cycles by status
     */
    @Transactional(readOnly = true)
    public List<EvaluationCycleResponse> getEvaluationCyclesByStatus(String status) {
        log.info("Getting evaluation cycles by status: {}", status);
        
        List<EvaluationCycles> cycles = evaluationCyclesRepository.findByStatus(status);
        return evaluationCycleMapper.toResponseList(cycles);
    }
    
    /**
     * Get active evaluation cycles
     */
    @Transactional(readOnly = true)
    public List<EvaluationCycleResponse> getActiveEvaluationCycles() {
        log.info("Getting active evaluation cycles");
        
        List<EvaluationCycles> cycles = evaluationCyclesRepository.findActiveCycles();
        return evaluationCycleMapper.toResponseList(cycles);
    }
    
    /**
     * Get active cycles by department
     */
    @Transactional(readOnly = true)
    public List<EvaluationCycleResponse> getActiveCyclesByDepartment(Long departmentId) {
        log.info("Getting active evaluation cycles by department ID: {}", departmentId);
        
        List<EvaluationCycles> cycles = evaluationCyclesRepository.findActiveCyclesByDepartmentId(departmentId);
        return evaluationCycleMapper.toResponseList(cycles);
    }
    
    /**
     * Validate date range
     */
    private void validateDateRange(String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            
            if (start.isAfter(end)) {
                log.warn("Invalid date range: start date {} is after end date {}", startDate, endDate);
                throw new AppException(ErrorCode.CYCLE_DATE_INVALID);
            }
        } catch (DateTimeParseException e) {
            log.warn("Invalid date format: start={}, end={}", startDate, endDate);
            throw new AppException(ErrorCode.CYCLE_DATE_INVALID);
        }
    }
    
    /**
     * Check for overlapping cycles in the same department
     */
    private void checkForOverlappingCycles(Long departmentId, String startDate, String endDate, Long excludeCycleId) {
        List<EvaluationCycles> existingCycles = evaluationCyclesRepository.findByDepartmentId(departmentId);
        
        LocalDate newStart = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate newEnd = LocalDate.parse(endDate, DATE_FORMATTER);
        
        for (EvaluationCycles cycle : existingCycles) {
            if (excludeCycleId != null && cycle.getEvaluationCycleId().equals(excludeCycleId)) {
                continue;
            }
            
            LocalDate existingStart = LocalDate.parse(cycle.getStartDate(), DATE_FORMATTER);
            LocalDate existingEnd = LocalDate.parse(cycle.getEndDate(), DATE_FORMATTER);
            
            // Check for overlap
            if (!(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd))) {
                log.warn("Overlapping evaluation cycle found for department {}: existing cycle {} ({} to {}), new cycle ({} to {})",
                        departmentId, cycle.getEvaluationCycleId(), cycle.getStartDate(), cycle.getEndDate(), startDate, endDate);
                throw new AppException(ErrorCode.EVALUATION_CYCLE_ALREADY_EXISTS);
            }
        }
    }
    
    /**
     * Validate status value
     */
    private boolean isValidStatus(String status) {
        return "DRAFT".equals(status) || "ACTIVE".equals(status) || "COMPLETED".equals(status);
    }
    
    /**
     * Validate status transition
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        // DRAFT -> ACTIVE -> COMPLETED (one way only)
        if ("COMPLETED".equals(currentStatus)) {
            throw new AppException(ErrorCode.EVALUATION_CYCLE_COMPLETED);
        }
        
        if ("ACTIVE".equals(currentStatus) && "DRAFT".equals(newStatus)) {
            throw new AppException(ErrorCode.EVALUATION_CYCLE_ACTIVE);
        }
    }
}
