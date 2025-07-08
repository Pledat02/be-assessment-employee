package com.example.assessment_employee.repository;

import com.example.assessment_employee.entity.CriteriaForm;
import com.example.assessment_employee.entity.Employee;
import com.example.assessment_employee.entity.SummaryAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryAssessmentRepository extends JpaRepository<SummaryAssessment, Long> {
    /**
     * Tìm SummaryAssessment dựa trên employee và criteriaForm.
     * @param employee Nhân viên cần tìm.
     * @param criteriaFormId Biểu mẫu tiêu chí cần tìm.
     * @return Optional chứa SummaryAssessment nếu tồn tại, hoặc rỗng nếu không tìm thấy.
     */
    @Query("SELECT s FROM SummaryAssessment s WHERE s.employee = :employee AND s.criteriaFormId = :criteriaFormId")
    Optional<SummaryAssessment> findByEmployeeAndCriteriaFormId(
            @Param("employee") Employee employee,
            @Param("criteriaFormId") long criteriaFormId
    );



}