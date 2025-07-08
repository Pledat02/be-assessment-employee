package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.response.CriteriaAverageResponse;
import com.example.assessment_employee.dto.response.EmployeeSimpleResponse;
import com.example.assessment_employee.dto.response.EvaluationStatisticsResponse;
import com.example.assessment_employee.dto.response.TopEmployeeResponse;
import com.example.assessment_employee.repository.EmployeeRepository;
import com.example.assessment_employee.repository.EvaluationAnswersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final EmployeeRepository employeeRepository;
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    public EvaluationStatisticsResponse getStatisticsOverview() {
        long total = employeeRepository.count();
        long evaluated = evaluationAnswersRepository.countEvaluatedEmployees();
        Double avg = evaluationAnswersRepository.averageTotalScore();
        long excellent = evaluationAnswersRepository.countExcellentEmployees();

        return EvaluationStatisticsResponse.builder()
                .totalEmployees(total)
                .evaluatedEmployees(evaluated)
                .averageScore(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                .excellentEmployees(excellent)
                .build();
    }

    public List<TopEmployeeResponse> getTopEmployees() {
        List<Object[]> result = evaluationAnswersRepository.findTop5Employees();

        List<TopEmployeeResponse> response = new ArrayList<>();
        if (result.isEmpty()) return response;

        int rank = 1;
        for (Object[] row : result) {
            String fullName = (String) row[0];
            String position = (String) row[1];
            String department = (String) row[2];
            double avgScore = ((Number) row[3]).doubleValue();

            response.add(TopEmployeeResponse.builder()
                    .rank(rank++)
                    .fullName(fullName)
                    .position(position)
                    .department(department)
                    .averageScore(Math.round(avgScore * 10.0) / 10.0)
                    .classification(classify(avgScore))
                    .scoreDiffFromLast(0.0) // mặc định
                    .avatarUrl("https://via.placeholder.com/100")
                    .build());
        }

        return response;
    }

    private String classify(double score) {
        if (score >= 4.5) return "Xuất sắc";
        if (score >= 3.5) return "Tốt";
        if (score >= 2.5) return "Trung bình";
        return "Yếu";
    }

    public List<CriteriaAverageResponse> getCriteriaAverages(Integer quarter, Integer year, Long departmentId) {
        LocalDate start = getQuarterStartDate(quarter, year);
        LocalDate end = getQuarterEndDate(quarter, year);

        List<Object[]> rawResults = evaluationAnswersRepository.getAverageScoreByCriteria(start.toString(), end.toString(), departmentId);

        return rawResults.stream()
                .map(obj -> new CriteriaAverageResponse(
                        (String) obj[0],
                        obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
                ))
                .collect(Collectors.toList());
    }


    private LocalDate getQuarterStartDate(Integer quarter, Integer year) {
        if (quarter == null || year == null) return LocalDate.of(year, 1, 1);
        return switch (quarter) {
            case 1 -> LocalDate.of(year, 1, 1);
            case 2 -> LocalDate.of(year, 4, 1);
            case 3 -> LocalDate.of(year, 7, 1);
            case 4 -> LocalDate.of(year, 10, 1);
            default -> throw new IllegalArgumentException("Invalid quarter: " + quarter);
        };
    }

    private LocalDate getQuarterEndDate(Integer quarter, Integer year) {
        if (quarter == null || year == null) return LocalDate.of(year, 12, 31);
        return switch (quarter) {
            case 1 -> LocalDate.of(year, 3, 31);
            case 2 -> LocalDate.of(year, 6, 30);
            case 3 -> LocalDate.of(year, 9, 30);
            case 4 -> LocalDate.of(year, 12, 31);
            default -> throw new IllegalArgumentException("Invalid quarter: " + quarter);
        };
    }

    public List<EmployeeSimpleResponse> getEmployeesEvaluated() {
        return employeeRepository.findDistinctEvaluatedEmployees().stream()
                .map(e -> new EmployeeSimpleResponse(e.getAccount().getId(), e.getFullName()))
                .collect(Collectors.toList());
    }

    public List<CriteriaAverageResponse> getCriteriaScoresForEmployee(Long employeeId) {
        return evaluationAnswersRepository.fetchAverageScoresByCriteriaForEmployee(employeeId);
    }


}
