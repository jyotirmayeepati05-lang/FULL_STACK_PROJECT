package com.example.Backend.service.impl;

import com.example.Backend.dto.AttendanceResponseDTO;
import com.example.Backend.dto.DashboardSummaryDTO;
import com.example.Backend.dto.ShiftAssignmentResponseDTO;
import com.example.Backend.entity.enums.AttendanceStatus;
import com.example.Backend.entity.enums.EmployeeStatus;
import com.example.Backend.repository.AttendanceRepository;
import com.example.Backend.repository.EmployeeRepository;
import com.example.Backend.repository.ShiftAssignmentRepository;
import com.example.Backend.repository.ShiftRepository;
import com.example.Backend.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final AttendanceRepository attendanceRepository;

    public DashboardServiceImpl(EmployeeRepository employeeRepository,
                                ShiftRepository shiftRepository,
                                ShiftAssignmentRepository shiftAssignmentRepository,
                                AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary() {
        LocalDate today = LocalDate.now();

        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus(EmployeeStatus.ACTIVE);
        long totalShifts = shiftRepository.count();

        long todayScheduledAssignments = shiftAssignmentRepository.countByAssignmentDate(today);
        long todayPresentCount = attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.PRESENT);
        long todayLateCount = attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.LATE);
        long todayHalfDayCount = attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.HALF_DAY);

        long todayClockedIn = todayPresentCount + todayLateCount + todayHalfDayCount;
        long todayPendingCheckIn = Math.max(0, todayScheduledAssignments - todayClockedIn);

        List<ShiftAssignmentResponseDTO> recentAssignments = shiftAssignmentRepository
                .findTop10ByOrderByAssignmentDateDescCreatedAtDesc().stream()
                .map(ShiftAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());

        List<AttendanceResponseDTO> recentAttendance = attendanceRepository
                .findTop10ByOrderByAttendanceDateDescCheckInTimeDesc().stream()
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());

        DashboardSummaryDTO dto = new DashboardSummaryDTO();
        dto.setTotalEmployees(totalEmployees);
        dto.setActiveEmployees(activeEmployees);
        dto.setTotalShifts(totalShifts);
        dto.setTodayScheduledAssignments(todayScheduledAssignments);
        dto.setTodayPresentCount(todayPresentCount);
        dto.setTodayLateCount(todayLateCount);
        dto.setTodayHalfDayCount(todayHalfDayCount);
        dto.setTodayPendingCheckInCount(todayPendingCheckIn);
        dto.setRecentAssignments(recentAssignments);
        dto.setRecentAttendance(recentAttendance);

        return dto;
    }
}
