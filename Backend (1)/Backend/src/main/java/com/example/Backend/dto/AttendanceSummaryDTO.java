package com.example.Backend.dto;

public class AttendanceSummaryDTO {

    private Long employeeId;
    private String employeeName;
    private long totalDaysWorked;
    private long presentDays;
    private long lateDays;
    private long halfDays;
    private double totalHoursWorked;

    public AttendanceSummaryDTO() {
    }

    public AttendanceSummaryDTO(Long employeeId, String employeeName, long totalDaysWorked,
                                long presentDays, long lateDays, long halfDays, double totalHoursWorked) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalDaysWorked = totalDaysWorked;
        this.presentDays = presentDays;
        this.lateDays = lateDays;
        this.halfDays = halfDays;
        this.totalHoursWorked = totalHoursWorked;
    }

    // Getters and Setters
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

    public long getTotalDaysWorked() {
        return totalDaysWorked;
    }

    public void setTotalDaysWorked(long totalDaysWorked) {
        this.totalDaysWorked = totalDaysWorked;
    }

    public long getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(long presentDays) {
        this.presentDays = presentDays;
    }

    public long getLateDays() {
        return lateDays;
    }

    public void setLateDays(long lateDays) {
        this.lateDays = lateDays;
    }

    public long getHalfDays() {
        return halfDays;
    }

    public void setHalfDays(long halfDays) {
        this.halfDays = halfDays;
    }

    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }
}
