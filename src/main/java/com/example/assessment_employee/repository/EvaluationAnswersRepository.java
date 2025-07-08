package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.CriteriaForm;
import com.example.assessment_employee.entity.EvaluationAnswers;
import com.example.assessment_employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationAnswersRepository extends JpaRepository<EvaluationAnswers, Long> {
    

}
