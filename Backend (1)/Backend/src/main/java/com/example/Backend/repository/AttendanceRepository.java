package com.example.Backend.repository;

import com.example.Backend.entity.Attendance;
import com.example.Backend.entity.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByAttendanceDate(LocalDate date);

    List<Attendance> findByAttendanceDateBetweenOrderByAttendanceDateDesc(LocalDate startDate, LocalDate endDate);

    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);

    List<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);

    Optional<Attendance> findTopByEmployeeIdAndAttendanceDateAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Long employeeId, LocalDate date);

    long countByAttendanceDate(LocalDate date);

    long countByAttendanceDateAndStatus(LocalDate date, AttendanceStatus status);

    List<Attendance> findTop10ByOrderByAttendanceDateDescCheckInTimeDesc();
}
