package com.example.Backend.service.impl;

import com.example.Backend.dto.ShiftRequestDTO;
import com.example.Backend.dto.ShiftResponseDTO;
import com.example.Backend.entity.Shift;
import com.example.Backend.exception.ConflictException;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.ShiftRepository;
import com.example.Backend.service.ShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public ShiftResponseDTO createShift(ShiftRequestDTO requestDTO) {
        if (shiftRepository.existsByNameIgnoreCase(requestDTO.getName().trim())) {
            throw new ConflictException("A shift with name '" + requestDTO.getName() + "' already exists");
        }

        validateShiftTimes(requestDTO);

        Shift shift = new Shift();
        mapDtoToEntity(requestDTO, shift);

        Shift saved = shiftRepository.save(shift);
        return ShiftResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponseDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponseDTO getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with ID: " + id));
        return ShiftResponseDTO.fromEntity(shift);
    }

    @Override
    public ShiftResponseDTO updateShift(Long id, ShiftRequestDTO requestDTO) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with ID: " + id));

        if (shiftRepository.existsByNameIgnoreCaseAndIdNot(requestDTO.getName().trim(), id)) {
            throw new ConflictException("A shift with name '" + requestDTO.getName() + "' already exists");
        }

        validateShiftTimes(requestDTO);

        mapDtoToEntity(requestDTO, shift);
        Shift updated = shiftRepository.save(shift);
        return ShiftResponseDTO.fromEntity(updated);
    }

    @Override
    public void deleteShift(Long id) {
        if (!shiftRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shift not found with ID: " + id);
        }
        shiftRepository.deleteById(id);
    }

    private void validateShiftTimes(ShiftRequestDTO dto) {
        if (dto.getStartTime().equals(dto.getEndTime())) {
            throw new IllegalArgumentException("Shift start time and end time cannot be identical");
        }
    }

    private void mapDtoToEntity(ShiftRequestDTO dto, Shift entity) {
        entity.setName(dto.getName().trim());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRequiredHeadcount(dto.getRequiredHeadcount());
        entity.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        if (dto.getColorCode() != null && !dto.getColorCode().isBlank()) {
            entity.setColorCode(dto.getColorCode().trim());
        }
    }
}
