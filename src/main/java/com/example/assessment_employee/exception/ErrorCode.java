package com.example.assessment_employee.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
public enum ErrorCode {
    // Common errors (9000-9999)
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9998, "Invalid message key", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(9997, "Validation error", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(9996, "Resource not found", HttpStatus.NOT_FOUND),

    // Authentication & Authorization errors (1000-1099)
    UNAUTHENTICATED(1000, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1001, "You don't have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1002, "Invalid or expired token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1003, "Token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(1004, "Invalid username or password", HttpStatus.UNAUTHORIZED),

    // User & Account errors (1100-1199)
    USER_NOT_EXISTED(1100, "User not found", HttpStatus.NOT_FOUND),
    ASSESSOR_NOT_FOUND(1100, "ASSESSOR not found", HttpStatus.NOT_FOUND),
    USER_EXISTED(1101, "User already exists", HttpStatus.CONFLICT),
    USERNAME_EXISTED(1102, "Username already exists", HttpStatus.CONFLICT),
    ACCOUNT_INACTIVE(1103, "Account is inactive", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1104, "Account is locked", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(1105, "Invalid password format", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_WEAK(1106, "Password is too weak", HttpStatus.BAD_REQUEST),

    // Employee errors (1200-1299)
    EMPLOYEE_NOT_FOUND(1200, "Employee not found", HttpStatus.NOT_FOUND),
    EMPLOYEE_ALREADY_EXISTS(1201, "Employee already exists", HttpStatus.CONFLICT),
    EMPLOYEE_ACCOUNT_LINKED(1202, "Account is already linked to another employee", HttpStatus.CONFLICT),
    EMPLOYEE_HAS_EVALUATIONS(1203, "Cannot delete employee with existing evaluations", HttpStatus.CONFLICT),

    // Department errors (1300-1399)
    DEPARTMENT_NOT_FOUND(1300, "Department not found", HttpStatus.NOT_FOUND),
    DEPARTMENT_ALREADY_EXISTS(1301, "Department already exists", HttpStatus.CONFLICT),
    DEPARTMENT_NAME_EXISTED(1302, "Department name already exists", HttpStatus.CONFLICT),
    DEPARTMENT_HAS_EMPLOYEES(1303, "Cannot delete department with existing employees", HttpStatus.CONFLICT),
    DEPARTMENT_HAS_CYCLES(1304, "Cannot delete department with existing evaluation cycles", HttpStatus.CONFLICT),
    MANAGER_CODE_EXISTED(1305, "Manager code already exists", HttpStatus.CONFLICT),

    // Evaluation Cycle errors (1400-1499)
    EVALUATION_CYCLE_NOT_FOUND(1400, "Evaluation cycle not found", HttpStatus.NOT_FOUND),
    EVALUATION_CYCLE_ALREADY_EXISTS(1401, "Evaluation cycle already exists", HttpStatus.CONFLICT),
    EVALUATION_CYCLE_ACTIVE(1402, "Cannot modify active evaluation cycle", HttpStatus.CONFLICT),
    EVALUATION_CYCLE_COMPLETED(1403, "Cannot modify completed evaluation cycle", HttpStatus.CONFLICT),
    INVALID_CYCLE_STATUS(1404, "Invalid evaluation cycle status", HttpStatus.BAD_REQUEST),
    CYCLE_DATE_INVALID(1405, "Invalid cycle date range", HttpStatus.BAD_REQUEST),

    // Criteria Form errors (1500-1599)
    CRITERIA_FORM_NOT_FOUND(1500, "Criteria form not found", HttpStatus.NOT_FOUND),
    CRITERIA_FORM_ALREADY_EXISTS(1501, "Criteria form already exists", HttpStatus.CONFLICT),
    CRITERIA_FORM_NAME_EXISTED(1502, "Criteria form name already exists", HttpStatus.CONFLICT),
    CRITERIA_FORM_HAS_EVALUATIONS(1503, "Cannot delete criteria form with existing evaluations", HttpStatus.CONFLICT),
    CRITERIA_FORM_EMPTY(1504, "Criteria form must have at least one criteria", HttpStatus.BAD_REQUEST),

    // Evaluation Criteria errors (1600-1699)
    EVALUATION_CRITERIA_NOT_FOUND(1600, "Evaluation criteria not found", HttpStatus.NOT_FOUND),
    EVALUATION_CRITERIA_ALREADY_EXISTS(1601, "Evaluation criteria already exists", HttpStatus.CONFLICT),
    CRITERIA_NAME_EXISTED(1602, "Criteria name already exists", HttpStatus.CONFLICT),
    CRITERIA_HAS_QUESTIONS(1603, "Cannot delete criteria with existing questions", HttpStatus.CONFLICT),
    CRITERIA_IN_USE(1604, "Cannot delete criteria that is being used in forms", HttpStatus.CONFLICT),

    // Evaluation Question errors (1700-1799)
    EVALUATION_QUESTION_NOT_FOUND(1700, "Evaluation question not found", HttpStatus.NOT_FOUND),
    EVALUATION_QUESTION_ALREADY_EXISTS(1701, "Evaluation question already exists", HttpStatus.CONFLICT),
    QUESTION_NAME_EXISTED(1702, "Question name already exists for this criteria", HttpStatus.CONFLICT),
    QUESTION_HAS_ANSWERS(1703, "Cannot delete question with existing answers", HttpStatus.CONFLICT),
    INVALID_MAX_SCORE(1704, "Invalid maximum score value", HttpStatus.BAD_REQUEST),

    // Evaluation Process errors (1800-1899)
    EVALUATION_NOT_FOUND(1800, "Evaluation not found", HttpStatus.NOT_FOUND),
    SUMMARY_ASSESSMENT_NOT_FOUND(1800, "SUMMARY_ASSESSMENT not found", HttpStatus.NOT_FOUND),
    EVALUATION_ALREADY_EXISTS(1801, "Evaluation already exists for this employee and cycle", HttpStatus.CONFLICT),
    EVALUATION_ALREADY_COMPLETED(1802, "Evaluation is already completed", HttpStatus.CONFLICT),
    EVALUATION_NOT_STARTED(1803, "Evaluation has not been started", HttpStatus.BAD_REQUEST),
    EVALUATION_PERMISSION_DENIED(1804, "You don't have permission to access this evaluation", HttpStatus.FORBIDDEN),
    SELF_ASSESSMENT_COMPLETED(1805, "Self assessment is already completed", HttpStatus.CONFLICT),
    SUPERVISOR_REVIEW_COMPLETED(1806, "Supervisor review is already completed", HttpStatus.CONFLICT),
    MANAGER_REVIEW_COMPLETED(1807, "Manager review is already completed", HttpStatus.CONFLICT),
    INVALID_SCORE_VALUE(1808, "Invalid score value", HttpStatus.BAD_REQUEST),
    SCORE_EXCEEDS_MAXIMUM(1809, "Score exceeds maximum allowed value", HttpStatus.BAD_REQUEST),
    EVALUATION_STEP_NOT_ALLOWED(1810, "This evaluation step is not allowed at this time", HttpStatus.CONFLICT)
    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
