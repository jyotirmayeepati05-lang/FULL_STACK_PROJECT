package com.example.Backend.controller;

import com.example.Backend.dto.ApiResponse;
import com.example.Backend.dto.ShiftAssignmentRequestDTO;
import com.example.Backend.dto.ShiftAssignmentResponseDTO;
import com.example.Backend.entity.enums.AssignmentStatus;
import com.example.Backend.service.ShiftAssignmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class ShiftAssignmentController {

    private final ShiftAssignmentService assignmentService;

    public ShiftAssignmentController(ShiftAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShiftAssignmentResponseDTO>> createAssignment(
            @Valid @RequestBody ShiftAssignmentRequestDTO requestDTO) {
        ShiftAssignmentResponseDTO created = assignmentService.createAssignment(requestDTO);
        return new ResponseEntity<>(ApiResponse.success("Shift assigned successfully", created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftAssignmentResponseDTO>>> getAssignments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long shiftId) {
        List<ShiftAssignmentResponseDTO> assignments = assignmentService.getAssignments(date, startDate, endDate, employeeId, shiftId);
        return ResponseEntity.ok(ApiResponse.success("Assignments retrieved successfully", assignments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftAssignmentResponseDTO>> getAssignmentById(@PathVariable Long id) {
        ShiftAssignmentResponseDTO assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment retrieved successfully", assignment));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShiftAssignmentResponseDTO>> updateAssignmentStatus(
            @PathVariable Long id,
            @RequestParam AssignmentStatus status) {
        ShiftAssignmentResponseDTO updated = assignmentService.updateAssignmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Assignment status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.success("Assignment deleted successfully", null));
    }
}
