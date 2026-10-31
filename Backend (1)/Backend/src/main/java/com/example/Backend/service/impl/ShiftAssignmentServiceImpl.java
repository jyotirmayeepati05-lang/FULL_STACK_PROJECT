package com.example.Backend.service.impl;

import com.example.Backend.dto.ShiftAssignmentRequestDTO;
import com.example.Backend.dto.ShiftAssignmentResponseDTO;
import com.example.Backend.entity.Employee;
import com.example.Backend.entity.Shift;
import com.example.Backend.entity.ShiftAssignment;
import com.example.Backend.entity.enums.AssignmentStatus;
import com.example.Backend.entity.enums.EmployeeStatus;
import com.example.Backend.exception.ConflictException;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.EmployeeRepository;
import com.example.Backend.repository.ShiftAssignmentRepository;
import com.example.Backend.repository.ShiftRepository;
import com.example.Backend.service.ShiftAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private final ShiftAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepository assignmentRepository,
                                     EmployeeRepository employeeRepository,
                                     ShiftRepository shiftRepository) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    @Override
    public ShiftAssignmentResponseDTO createAssignment(ShiftAssignmentRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + requestDTO.getEmployeeId()));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new ConflictException("Cannot assign shifts to an INACTIVE employee (" + employee.getFirstName() + " " + employee.getLastName() + ")");
        }

        Shift shift = shiftRepository.findById(requestDTO.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with ID: " + requestDTO.getShiftId()));

        // Check if employee already has an assignment for the EXACT same shift on this date
        if (assignmentRepository.findByEmployeeIdAndShiftIdAndAssignmentDate(
                employee.getId(), shift.getId(), requestDTO.getAssignmentDate()).isPresent()) {
            throw new ConflictException("Employee is already assigned to this shift on " + requestDTO.getAssignmentDate());
        }

        // Optimization check: Detect overlapping shifts on the same day for this employee
        List<ShiftAssignment> existingAssignments = assignmentRepository
                .findByEmployeeIdAndAssignmentDateAndStatusNot(employee.getId(), requestDTO.getAssignmentDate(), AssignmentStatus.CANCELLED);

        for (ShiftAssignment existing : existingAssignments) {
            if (shiftsOverlap(shift, existing.getShift())) {
                throw new ConflictException(String.format(
                        "Schedule Conflict: Employee '%s' is already assigned to shift '%s' (%s - %s) on %s which overlaps with '%s' (%s - %s)",
                        employee.getFirstName() + " " + employee.getLastName(),
                        existing.getShift().getName(),
                        existing.getShift().getStartTime(),
                        existing.getShift().getEndTime(),
                        requestDTO.getAssignmentDate(),
                        shift.getName(),
                        shift.getStartTime(),
                        shift.getEndTime()
                ));
            }
        }

        ShiftAssignment assignment = new ShiftAssignment();
        assignment.setEmployee(employee);
        assignment.setShift(shift);
        assignment.setAssignmentDate(requestDTO.getAssignmentDate());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setNotes(requestDTO.getNotes() != null ? requestDTO.getNotes().trim() : null);

        ShiftAssignment saved = assignmentRepository.save(assignment);
        return ShiftAssignmentResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftAssignmentResponseDTO> getAssignments(LocalDate date, LocalDate startDate, LocalDate endDate,
                                                           Long employeeId, Long shiftId) {
        List<ShiftAssignment> list;

        if (date != null) {
            list = assignmentRepository.findByAssignmentDate(date);
        } else if (startDate != null && endDate != null) {
            list = assignmentRepository.findByAssignmentDateBetweenOrderByAssignmentDateAsc(startDate, endDate);
        } else if (employeeId != null) {
            list = assignmentRepository.findByEmployeeIdOrderByAssignmentDateDesc(employeeId);
        } else if (shiftId != null && date != null) {
            list = assignmentRepository.findByShiftIdAndAssignmentDate(shiftId, date);
        } else {
            list = assignmentRepository.findAll();
        }

        // Apply secondary in-memory filtering if multiple params were combined
        return list.stream()
                .filter(a -> employeeId == null || a.getEmployee().getId().equals(employeeId))
                .filter(a -> shiftId == null || a.getShift().getId().equals(shiftId))
                .map(ShiftAssignmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftAssignmentResponseDTO getAssignmentById(Long id) {
        ShiftAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found with ID: " + id));
        return ShiftAssignmentResponseDTO.fromEntity(assignment);
    }

    @Override
    public ShiftAssignmentResponseDTO updateAssignmentStatus(Long id, AssignmentStatus status) {
        ShiftAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift assignment not found with ID: " + id));

        assignment.setStatus(status);
        ShiftAssignment updated = assignmentRepository.save(assignment);
        return ShiftAssignmentResponseDTO.fromEntity(updated);
    }

    @Override
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shift assignment not found with ID: " + id);
        }
        assignmentRepository.deleteById(id);
    }

    /**
     * Helper method to determine if two shifts overlap in their operational hours.
     */
    private boolean shiftsOverlap(Shift s1, Shift s2) {
        LocalTime start1 = s1.getStartTime();
        LocalTime end1 = s1.getEndTime();
        LocalTime start2 = s2.getStartTime();
        LocalTime end2 = s2.getEndTime();

        // Standard daytime shifts (e.g. 08:00 to 16:00)
        boolean s1Overnight = end1.isBefore(start1);
        boolean s2Overnight = end2.isBefore(start2);

        if (!s1Overnight && !s2Overnight) {
            return start1.isBefore(end2) && start2.isBefore(end1);
        }

        // If one or both shifts cross midnight, treat midnight as a split point
        // [start1, 23:59:59] & [00:00, end1] vs s2
        return true; // Conservative safety fallback: overnight shifts overlap across midnight boundaries
    }
}
