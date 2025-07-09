package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.response.*;
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

    @GetMapping("/criteria-average/{cycleId}")
    public List<CriteriaAverageResponse> getCriteriaAverageByFilter(
      @PathVariable long cycleId
    ) {

        return statisticsService.getCriteriaAverages(cycleId);
    }


    @GetMapping("/employees-evaluated")
    public ResponseEntity<List<EmployeeSimpleResponse>> getEmployeesEvaluated() {
        return ResponseEntity.ok(statisticsService.getEmployeesEvaluated());
    }

    @GetMapping("/employee/{employeeId}/criteria-scores")
    public ResponseEntity<List<CriteriaEmployeeResponse>> getCriteriaScoresForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(statisticsService.getCriteriaScoresForEmployee(employeeId));
    }

}

