package com.example.Backend.dto;

import java.util.List;

public class DashboardSummaryDTO {

    private long totalEmployees;
    private long activeEmployees;
    private long totalShifts;
    private long todayScheduledAssignments;
    private long todayPresentCount;
    private long todayLateCount;
    private long todayHalfDayCount;
    private long todayPendingCheckInCount;
    private List<ShiftAssignmentResponseDTO> recentAssignments;
    private List<AttendanceResponseDTO> recentAttendance;

    public DashboardSummaryDTO() {
    }

    // Getters and Setters
    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getTotalShifts() {
        return totalShifts;
    }

    public void setTotalShifts(long totalShifts) {
        this.totalShifts = totalShifts;
    }

    public long getTodayScheduledAssignments() {
        return todayScheduledAssignments;
    }

    public void setTodayScheduledAssignments(long todayScheduledAssignments) {
        this.todayScheduledAssignments = todayScheduledAssignments;
    }

    public long getTodayPresentCount() {
        return todayPresentCount;
    }

    public void setTodayPresentCount(long todayPresentCount) {
        this.todayPresentCount = todayPresentCount;
    }

    public long getTodayLateCount() {
        return todayLateCount;
    }

    public void setTodayLateCount(long todayLateCount) {
        this.todayLateCount = todayLateCount;
    }

    public long getTodayHalfDayCount() {
        return todayHalfDayCount;
    }

    public void setTodayHalfDayCount(long todayHalfDayCount) {
        this.todayHalfDayCount = todayHalfDayCount;
    }

    public long getTodayPendingCheckInCount() {
        return todayPendingCheckInCount;
    }

    public void setTodayPendingCheckInCount(long todayPendingCheckInCount) {
        this.todayPendingCheckInCount = todayPendingCheckInCount;
    }

    public List<ShiftAssignmentResponseDTO> getRecentAssignments() {
        return recentAssignments;
    }

    public void setRecentAssignments(List<ShiftAssignmentResponseDTO> recentAssignments) {
        this.recentAssignments = recentAssignments;
    }

    public List<AttendanceResponseDTO> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<AttendanceResponseDTO> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }
}
