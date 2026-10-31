package com.example.Backend.dto;

import com.example.Backend.entity.ShiftAssignment;
import com.example.Backend.entity.enums.AssignmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShiftAssignmentResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String department;

    private Long shiftId;
    private String shiftName;
    private String shiftStartTime;
    private String shiftEndTime;
    private String colorCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assignmentDate;

    private AssignmentStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public ShiftAssignmentResponseDTO() {
    }

    public static ShiftAssignmentResponseDTO fromEntity(ShiftAssignment assignment) {
        if (assignment == null) return null;
        ShiftAssignmentResponseDTO dto = new ShiftAssignmentResponseDTO();
        dto.setId(assignment.getId());

        if (assignment.getEmployee() != null) {
            dto.setEmployeeId(assignment.getEmployee().getId());
            dto.setEmployeeName(assignment.getEmployee().getFirstName() + " " + assignment.getEmployee().getLastName());
            dto.setEmployeeEmail(assignment.getEmployee().getEmail());
            dto.setDepartment(assignment.getEmployee().getDepartment());
        }

        if (assignment.getShift() != null) {
            dto.setShiftId(assignment.getShift().getId());
            dto.setShiftName(assignment.getShift().getName());
            dto.setShiftStartTime(assignment.getShift().getStartTime() != null ? assignment.getShift().getStartTime().toString() : null);
            dto.setShiftEndTime(assignment.getShift().getEndTime() != null ? assignment.getShift().getEndTime().toString() : null);
            dto.setColorCode(assignment.getShift().getColorCode());
        }

        dto.setAssignmentDate(assignment.getAssignmentDate());
        dto.setStatus(assignment.getStatus());
        dto.setNotes(assignment.getNotes());
        dto.setCreatedAt(assignment.getCreatedAt());

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
