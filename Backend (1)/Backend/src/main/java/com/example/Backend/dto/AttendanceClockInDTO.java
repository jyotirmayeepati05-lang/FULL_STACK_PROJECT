package com.example.Backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AttendanceClockInDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private Long shiftAssignmentId;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public AttendanceClockInDTO() {
    }

    // Getters and Setters
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getShiftAssignmentId() {
        return shiftAssignmentId;
    }

    public void setShiftAssignmentId(Long shiftAssignmentId) {
        this.shiftAssignmentId = shiftAssignmentId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
