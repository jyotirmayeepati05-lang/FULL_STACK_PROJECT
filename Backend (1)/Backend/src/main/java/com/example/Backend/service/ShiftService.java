package com.example.Backend.service;

import com.example.Backend.dto.ShiftRequestDTO;
import com.example.Backend.dto.ShiftResponseDTO;

import java.util.List;

public interface ShiftService {

    ShiftResponseDTO createShift(ShiftRequestDTO requestDTO);

    List<ShiftResponseDTO> getAllShifts();

    ShiftResponseDTO getShiftById(Long id);

    ShiftResponseDTO updateShift(Long id, ShiftRequestDTO requestDTO);

    void deleteShift(Long id);
}
