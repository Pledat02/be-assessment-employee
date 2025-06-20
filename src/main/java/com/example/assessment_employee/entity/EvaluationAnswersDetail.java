package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EvaluationAnswersDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationQuestionDetailId;
    private Long employeeScore;
    private String employeeComment;
    private Long supervisorScore;
    private String supervisorComment;
    private Long managerScore;
    private String managerComment;

    @ManyToOne
    @JoinColumn(name = "evaluation_question_id")
    private EvaluationQuestions evaluationQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_answer_id")
    private EvaluationAnswers evaluationAnswer;
}