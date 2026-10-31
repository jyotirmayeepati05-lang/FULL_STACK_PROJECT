package com.example.Backend.service;

import com.example.Backend.dto.AttendanceClockInDTO;
import com.example.Backend.dto.AttendanceClockOutDTO;
import com.example.Backend.dto.AttendanceResponseDTO;
import com.example.Backend.dto.AttendanceSummaryDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponseDTO clockIn(AttendanceClockInDTO clockInDTO);

    AttendanceResponseDTO clockOut(AttendanceClockOutDTO clockOutDTO);

    List<AttendanceResponseDTO> getAttendanceRecords(LocalDate date, LocalDate startDate, LocalDate endDate, Long employeeId);

    AttendanceResponseDTO getAttendanceById(Long id);

    AttendanceSummaryDTO getEmployeeAttendanceSummary(Long employeeId);
}
