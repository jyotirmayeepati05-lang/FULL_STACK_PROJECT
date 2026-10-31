package com.example.Backend.service;

import com.example.Backend.dto.ShiftAssignmentRequestDTO;
import com.example.Backend.dto.ShiftAssignmentResponseDTO;
import com.example.Backend.entity.enums.AssignmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentService {

    ShiftAssignmentResponseDTO createAssignment(ShiftAssignmentRequestDTO requestDTO);

    List<ShiftAssignmentResponseDTO> getAssignments(LocalDate date, LocalDate startDate, LocalDate endDate,
                                                    Long employeeId, Long shiftId);

    ShiftAssignmentResponseDTO getAssignmentById(Long id);

    ShiftAssignmentResponseDTO updateAssignmentStatus(Long id, AssignmentStatus status);

    void deleteAssignment(Long id);
}
