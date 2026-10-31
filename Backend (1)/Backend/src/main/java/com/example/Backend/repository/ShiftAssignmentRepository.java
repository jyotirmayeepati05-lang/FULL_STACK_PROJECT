package com.example.Backend.repository;

import com.example.Backend.entity.ShiftAssignment;
import com.example.Backend.entity.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {

    List<ShiftAssignment> findByAssignmentDate(LocalDate date);

    List<ShiftAssignment> findByAssignmentDateBetweenOrderByAssignmentDateAsc(LocalDate startDate, LocalDate endDate);

    List<ShiftAssignment> findByEmployeeIdOrderByAssignmentDateDesc(Long employeeId);

    List<ShiftAssignment> findByEmployeeIdAndAssignmentDate(Long employeeId, LocalDate date);

    List<ShiftAssignment> findByEmployeeIdAndAssignmentDateAndStatusNot(Long employeeId, LocalDate date, AssignmentStatus status);

    List<ShiftAssignment> findByShiftIdAndAssignmentDate(Long shiftId, LocalDate date);

    Optional<ShiftAssignment> findByEmployeeIdAndShiftIdAndAssignmentDate(Long employeeId, Long shiftId, LocalDate date);

    long countByShiftIdAndAssignmentDate(Long shiftId, LocalDate date);

    long countByAssignmentDate(LocalDate date);

    List<ShiftAssignment> findTop10ByOrderByAssignmentDateDescCreatedAtDesc();
}
