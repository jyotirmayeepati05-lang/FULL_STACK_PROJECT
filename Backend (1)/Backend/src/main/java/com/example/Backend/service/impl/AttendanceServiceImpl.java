package com.example.Backend.service.impl;

import com.example.Backend.dto.AttendanceClockInDTO;
import com.example.Backend.dto.AttendanceClockOutDTO;
import com.example.Backend.dto.AttendanceResponseDTO;
import com.example.Backend.dto.AttendanceSummaryDTO;
import com.example.Backend.entity.Attendance;
import com.example.Backend.entity.Employee;
import com.example.Backend.entity.Shift;
import com.example.Backend.entity.ShiftAssignment;
import com.example.Backend.entity.enums.AttendanceStatus;
import com.example.Backend.entity.enums.EmployeeStatus;
import com.example.Backend.exception.ConflictException;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.AttendanceRepository;
import com.example.Backend.repository.EmployeeRepository;
import com.example.Backend.repository.ShiftAssignmentRepository;
import com.example.Backend.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository,
                                 ShiftAssignmentRepository shiftAssignmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
    }

    @Override
    public AttendanceResponseDTO clockIn(AttendanceClockInDTO clockInDTO) {
        Employee employee = employeeRepository.findById(clockInDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + clockInDTO.getEmployeeId()));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new ConflictException("Cannot record attendance for an INACTIVE employee");
        }

        LocalDate today = LocalDate.now();

        // Check if employee already has an open clock-in without clock-out
        Optional<Attendance> openAttendance = attendanceRepository
                .findTopByEmployeeIdAndAttendanceDateAndCheckOutTimeIsNullOrderByCheckInTimeDesc(employee.getId(), today);

        if (openAttendance.isPresent()) {
            throw new ConflictException("Employee has already clocked in today at " +
                    openAttendance.get().getCheckInTime().toLocalTime() + " and has not clocked out yet");
        }

        LocalDateTime now = LocalDateTime.now();
        ShiftAssignment assignment = null;
        AttendanceStatus status = AttendanceStatus.PRESENT;

        if (clockInDTO.getShiftAssignmentId() != null) {
            assignment = shiftAssignmentRepository.findById(clockInDTO.getShiftAssignmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found with ID: " + clockInDTO.getShiftAssignmentId()));

            if (!assignment.getEmployee().getId().equals(employee.getId())) {
                throw new ConflictException("The provided shift assignment does not belong to this employee");
            }

            Shift shift = assignment.getShift();
            if (shift != null) {
                // If clock-in is more than 15 minutes after shift start time, mark as LATE
                LocalTime gracePeriodEnd = shift.getStartTime().plusMinutes(15);
                if (now.toLocalTime().isAfter(gracePeriodEnd)) {
                    status = AttendanceStatus.LATE;
                }
            }
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setShiftAssignment(assignment);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(now);
        attendance.setStatus(status);
        attendance.setRemarks(clockInDTO.getRemarks());

        Attendance saved = attendanceRepository.save(attendance);
        return AttendanceResponseDTO.fromEntity(saved);
    }

    @Override
    public AttendanceResponseDTO clockOut(AttendanceClockOutDTO clockOutDTO) {
        Attendance attendance;

        if (clockOutDTO.getAttendanceId() != null) {
            attendance = attendanceRepository.findById(clockOutDTO.getAttendanceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + clockOutDTO.getAttendanceId()));
        } else if (clockOutDTO.getEmployeeId() != null) {
            attendance = attendanceRepository
                    .findTopByEmployeeIdAndAttendanceDateAndCheckOutTimeIsNullOrderByCheckInTimeDesc(clockOutDTO.getEmployeeId(), LocalDate.now())
                    .orElseThrow(() -> new ResourceNotFoundException("No active clock-in found for employee ID: " + clockOutDTO.getEmployeeId()));
        } else {
            throw new IllegalArgumentException("Either attendanceId or employeeId must be provided to clock out");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new ConflictException("This attendance record has already been clocked out at: " + attendance.getCheckOutTime());
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setCheckOutTime(now);

        // Calculate hours worked
        long minutesWorked = Duration.between(attendance.getCheckInTime(), now).toMinutes();
        double hours = minutesWorked / 60.0;
        BigDecimal bd = BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP);
        attendance.setWorkHours(bd.doubleValue());

        // Mark as HALF_DAY if worked less than 4 hours and wasn't already marked late/absent
        if (hours < 4.0 && attendance.getStatus() == AttendanceStatus.PRESENT) {
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        }

        if (clockOutDTO.getRemarks() != null && !clockOutDTO.getRemarks().isBlank()) {
            attendance.setRemarks(clockOutDTO.getRemarks());
        }

        Attendance updated = attendanceRepository.save(attendance);
        return AttendanceResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceRecords(LocalDate date, LocalDate startDate, LocalDate endDate, Long employeeId) {
        List<Attendance> list;

        if (date != null) {
            list = attendanceRepository.findByAttendanceDate(date);
        } else if (startDate != null && endDate != null) {
            list = attendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateDesc(startDate, endDate);
        } else if (employeeId != null) {
            list = attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);
        } else {
            list = attendanceRepository.findAll();
        }

        return list.stream()
                .filter(a -> employeeId == null || a.getEmployee().getId().equals(employeeId))
                .map(AttendanceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + id));
        return AttendanceResponseDTO.fromEntity(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getEmployeeAttendanceSummary(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        List<Attendance> list = attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);

        long totalDaysWorked = list.size();
        long presentDays = list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long lateDays = list.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        long halfDays = list.stream().filter(a -> a.getStatus() == AttendanceStatus.HALF_DAY).count();
        double totalHoursWorked = list.stream()
                .filter(a -> a.getWorkHours() != null)
                .mapToDouble(Attendance::getWorkHours)
                .sum();

        BigDecimal bd = BigDecimal.valueOf(totalHoursWorked).setScale(2, RoundingMode.HALF_UP);

        return new AttendanceSummaryDTO(
                employee.getId(),
                employee.getFirstName() + " " + employee.getLastName(),
                totalDaysWorked,
                presentDays,
                lateDays,
                halfDays,
                bd.doubleValue()
        );
    }
}
