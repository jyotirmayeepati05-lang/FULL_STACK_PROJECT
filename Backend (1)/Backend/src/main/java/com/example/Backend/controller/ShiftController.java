package com.example.Backend.controller;

import com.example.Backend.dto.ApiResponse;
import com.example.Backend.dto.ShiftRequestDTO;
import com.example.Backend.dto.ShiftResponseDTO;
import com.example.Backend.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> createShift(@Valid @RequestBody ShiftRequestDTO requestDTO) {
        ShiftResponseDTO created = shiftService.createShift(requestDTO);
        return new ResponseEntity<>(ApiResponse.success("Shift created successfully", created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponseDTO>>> getAllShifts() {
        List<ShiftResponseDTO> shifts = shiftService.getAllShifts();
        return ResponseEntity.ok(ApiResponse.success("Shifts retrieved successfully", shifts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> getShiftById(@PathVariable Long id) {
        ShiftResponseDTO shift = shiftService.getShiftById(id);
        return ResponseEntity.ok(ApiResponse.success("Shift retrieved successfully", shift));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftResponseDTO>> updateShift(
            @PathVariable Long id,
            @Valid @RequestBody ShiftRequestDTO requestDTO) {
        ShiftResponseDTO updated = shiftService.updateShift(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Shift updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.ok(ApiResponse.success("Shift deleted successfully", null));
    }
}
