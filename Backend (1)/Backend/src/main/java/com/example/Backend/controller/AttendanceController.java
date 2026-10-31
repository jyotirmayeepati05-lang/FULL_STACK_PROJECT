package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> clockIn(@Valid @RequestBody AttendanceClockInDTO clockInDTO) {
        AttendanceResponseDTO response = attendanceService.clockIn(clockInDTO);
        return new ResponseEntity<>(ApiResponse.success("Clocked in successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> clockOut(@RequestBody AttendanceClockOutDTO clockOutDTO) {
        AttendanceResponseDTO response = attendanceService.clockOut(clockOutDTO);
        return ResponseEntity.ok(ApiResponse.success("Clocked out successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId) {
        List<AttendanceResponseDTO> records = attendanceService.getAttendanceRecords(date, startDate, endDate, employeeId);
        return ResponseEntity.ok(ApiResponse.success("Attendance records retrieved successfully", records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> getAttendanceById(@PathVariable Long id) {
        AttendanceResponseDTO record = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record retrieved successfully", record));
    }

    @GetMapping("/summary/{employeeId}")
    public ResponseEntity<ApiResponse<AttendanceSummaryDTO>> getEmployeeSummary(@PathVariable Long employeeId) {
        AttendanceSummaryDTO summary = attendanceService.getEmployeeAttendanceSummary(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Attendance summary retrieved successfully", summary));
    }
}
