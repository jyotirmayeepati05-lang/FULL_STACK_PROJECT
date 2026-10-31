package com.example.Backend.service;

import com.example.Backend.dto.EmployeeRequestDTO;
import com.example.Backend.dto.EmployeeResponseDTO;
import com.example.Backend.entity.enums.EmployeeStatus;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);

    List<EmployeeResponseDTO> getAllEmployees(EmployeeStatus status, String department);

    EmployeeResponseDTO getEmployeeById(Long id);

    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO);

    EmployeeResponseDTO updateEmployeeStatus(Long id, EmployeeStatus status);

    void deleteEmployee(Long id);
}
