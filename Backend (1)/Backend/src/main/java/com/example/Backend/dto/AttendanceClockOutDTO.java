package com.example.Backend.dto;

import jakarta.validation.constraints.Size;

public class AttendanceClockOutDTO {

    private Long attendanceId;
    private Long employeeId;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public AttendanceClockOutDTO() {
    }

    // Getters and Setters
    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
