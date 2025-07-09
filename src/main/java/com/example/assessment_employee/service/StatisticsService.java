package com.example.assessment_employee.service;

import com.example.assessment_employee.dto.response.*;
import com.example.assessment_employee.entity.EvaluationCycles;
import com.example.assessment_employee.exception.AppException;
import com.example.assessment_employee.exception.ErrorCode;
import com.example.assessment_employee.repository.EmployeeRepository;
import com.example.assessment_employee.repository.EvaluationAnswersRepository;
import com.example.assessment_employee.repository.EvaluationCyclesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final EmployeeRepository employeeRepository;
    private final EvaluationAnswersRepository evaluationAnswersRepository;
    private final EvaluationCyclesRepository evaluationCyclesRepository;
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
            String sentiment = (String) row[3];
            double avgScore = ((Number) row[4]).doubleValue();

            response.add(TopEmployeeResponse.builder()
                    .rank(rank++)
                    .fullName(fullName)
                    .position(position)
                    .department(department)
                    .averageScore(Math.round(avgScore * 10.0) / 10.0)
                    .classification(sentiment)
                    .scoreDiffFromLast(0.0) // mặc định
                    .avatarUrl("https://via.placeholder.com/100")
                    .build());
        }

        return response;
    }


    public List<CriteriaAverageResponse> getCriteriaAverages(Long cycleId) {
        Optional<EvaluationCycles> evaluationCycles = evaluationCyclesRepository.findById(cycleId);
        if(evaluationCycles.isEmpty()){
            throw new AppException(ErrorCode.EVALUATION_CYCLE_NOT_FOUND);
        }
        EvaluationCycles evaluationCycle = evaluationCycles.get();
        List<Object[]> rawResults = evaluationAnswersRepository.getAverageScoreByCriteria(evaluationCycle.getStartDate(), evaluationCycle.getEndDate());

        return rawResults.stream()
                .map(obj -> new CriteriaAverageResponse(
                        (String) obj[0],
                        obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
                ))
                .collect(Collectors.toList());
    }


    public List<EmployeeSimpleResponse> getEmployeesEvaluated() {
        return employeeRepository.findDistinctEvaluatedEmployees().stream()
                .map(e -> new EmployeeSimpleResponse(e.getAccount().getId(), e.getFullName()))
                .collect(Collectors.toList());
    }

    public List<CriteriaEmployeeResponse> getCriteriaScoresForEmployee(Long employeeId) {
        return evaluationAnswersRepository.fetchAverageScoresByCriteriaForEmployee(employeeId);
    }


}
