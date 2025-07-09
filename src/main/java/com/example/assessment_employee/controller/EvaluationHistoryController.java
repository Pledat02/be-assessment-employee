package com.example.assessment_employee.controller;

import com.example.assessment_employee.dto.request.EvaluationHistoryFilterRequest;
import com.example.assessment_employee.dto.response.ApiResponse;
import com.example.assessment_employee.dto.response.CycleStatisticsResponse;
import com.example.assessment_employee.dto.response.EvaluationHistoryResponse;
import com.example.assessment_employee.service.EvaluationHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation-history")
@RequiredArgsConstructor
@Slf4j
public class EvaluationHistoryController {
    
    private final EvaluationHistoryService evaluationHistoryService;
    
    /**
     * Lấy lịch sử đánh giá với filter và phân trang
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EvaluationHistoryResponse>>> getEvaluationHistory(
            @RequestParam(required = false) String sentiment,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) String cycleName,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long cycleId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        log.info("Get evaluation history request with filters - sentiment: {}, status: {}, employeeName: {}, cycleName: {}, employeeId: {}", 
                sentiment, status, employeeName, cycleName, employeeId);
        
        EvaluationHistoryFilterRequest filter = EvaluationHistoryFilterRequest.builder()
                .sentiment(sentiment)
                .status(status)
                .employeeName(employeeName)
                .cycleName(cycleName)
                .employeeId(employeeId)
                .cycleId(cycleId)
                .page(page)
                .size(size)
                .sort(sort)
                .direction(direction)
                .build();
        
        Page<EvaluationHistoryResponse> result = evaluationHistoryService.getEvaluationHistory(filter);
        
        return ResponseEntity.ok(ApiResponse.<Page<EvaluationHistoryResponse>>builder()
                .code(200)
                .message("Evaluation history retrieved successfully")
                .result(result)
                .build());
    }
    
    /**
     * Test endpoint để kiểm tra controller hoạt động
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testEndpoint() {
        log.info("Test evaluation history endpoint called");

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("Evaluation history controller is working!")
                .result("Test successful")
                .build());
    }

    /**
     * Lấy danh sách chu kỳ có đánh giá
     */
    @GetMapping("/cycles")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableCycles() {
        log.info("Get available evaluation cycles request");

        try {
            List<String> cycles = evaluationHistoryService.getAvailableCycles();

            return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                    .code(200)
                    .message("Available cycles retrieved successfully")
                    .result(cycles)
                    .build());
        } catch (Exception e) {
            log.error("Error getting available cycles: ", e);
            return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                    .code(500)
                    .message("Error: " + e.getMessage())
                    .result(new ArrayList<>())
                    .build());
        }
    }
    
    /**
     * Lấy thống kê theo chu kỳ
     */
    @GetMapping("/statistics/{cycleName}")
    public ResponseEntity<ApiResponse<CycleStatisticsResponse>> getCycleStatistics(
            @PathVariable String cycleName) {
        log.info("Get cycle statistics request for cycle: {}", cycleName);
        
        CycleStatisticsResponse statistics = evaluationHistoryService.getCycleStatistics(cycleName);
        
        return ResponseEntity.ok(ApiResponse.<CycleStatisticsResponse>builder()
                .code(200)
                .message("Cycle statistics retrieved successfully")
                .result(statistics)
                .build());
    }
    
    /**
     * Lấy dữ liệu biểu đồ theo tiêu chí cho chu kỳ
     */
    @GetMapping("/criteria-chart/{cycleName}")
    public ResponseEntity<ApiResponse<List<CycleStatisticsResponse.CriteriaChartDataResponse>>> getCriteriaChartData(
            @PathVariable String cycleName) {
        log.info("Get criteria chart data request for cycle: {}", cycleName);
        
        List<CycleStatisticsResponse.CriteriaChartDataResponse> chartData = 
                evaluationHistoryService.getCriteriaChartData(cycleName);
        
        return ResponseEntity.ok(ApiResponse.<List<CycleStatisticsResponse.CriteriaChartDataResponse>>builder()
                .code(200)
                .message("Criteria chart data retrieved successfully")
                .result(chartData)
                .build());
    }
    
    /**
     * Lấy chi tiết đánh giá theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationHistoryResponse>> getEvaluationDetail(
            @PathVariable Long id) {
        log.info("Get evaluation detail request for ID: {}", id);
        
        // Tạm thời return empty response, sẽ implement sau
        EvaluationHistoryResponse detail = EvaluationHistoryResponse.builder()
                .id(id)
                .build();
        
        return ResponseEntity.ok(ApiResponse.<EvaluationHistoryResponse>builder()
                .code(200)
                .message("Evaluation detail retrieved successfully")
                .result(detail)
                .build());
    }
}
