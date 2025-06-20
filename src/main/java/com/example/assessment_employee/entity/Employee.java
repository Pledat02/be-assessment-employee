package com.example.assessment_employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;
    private String fullname;
    private String division;
    private String basic;
    private String staffType;
    private String startDate;
    private String type;

    @OneToOne
    @JoinColumn(name = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}
