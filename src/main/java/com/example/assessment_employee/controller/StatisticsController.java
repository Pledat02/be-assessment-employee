package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.response.CriteriaAverageResponse;
import com.example.assessment_employee.dto.response.EmployeeSimpleResponse;
import com.example.assessment_employee.dto.response.EvaluationStatisticsResponse;
import com.example.assessment_employee.dto.response.TopEmployeeResponse;
import com.example.assessment_employee.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public ResponseEntity<EvaluationStatisticsResponse> getStatisticsOverview() {
        return ResponseEntity.ok(statisticsService.getStatisticsOverview());
    }

    @GetMapping("/top-employees")
    public ResponseEntity<List<TopEmployeeResponse>> getTopEmployees() {
        List<TopEmployeeResponse> topEmployees = statisticsService.getTopEmployees();
        return ResponseEntity.ok(topEmployees);
    }

    @GetMapping("/criteria-average")
    public List<CriteriaAverageResponse> getCriteriaAverageByFilter(
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long departmentId
    ) {
        if (year == null) {
            year = LocalDate.now().getYear(); // hoặc throw lỗi hợp lệ
        }
        return statisticsService.getCriteriaAverages(quarter, year, departmentId);
    }


    @GetMapping("/employees-evaluated")
    public ResponseEntity<List<EmployeeSimpleResponse>> getEmployeesEvaluated() {
        return ResponseEntity.ok(statisticsService.getEmployeesEvaluated());
    }

    @GetMapping("/employee/{employeeId}/criteria-scores")
    public ResponseEntity<List<CriteriaAverageResponse>> getCriteriaScoresForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(statisticsService.getCriteriaScoresForEmployee(employeeId));
    }

}

