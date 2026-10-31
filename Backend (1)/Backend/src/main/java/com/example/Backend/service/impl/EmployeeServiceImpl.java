package com.example.Backend.service.impl;

import com.example.Backend.dto.EmployeeRequestDTO;
import com.example.Backend.dto.EmployeeResponseDTO;
import com.example.Backend.entity.Employee;
import com.example.Backend.entity.enums.EmployeeStatus;
import com.example.Backend.exception.ConflictException;
import com.example.Backend.exception.ResourceNotFoundException;
import com.example.Backend.repository.EmployeeRepository;
import com.example.Backend.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ConflictException("An employee with email '" + requestDTO.getEmail() + "' already exists");
        }

        Employee employee = new Employee();
        mapDtoToEntity(requestDTO, employee);

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees(EmployeeStatus status, String department) {
        List<Employee> list;
        if (status != null) {
            list = employeeRepository.findByStatus(status);
        } else if (department != null && !department.isBlank()) {
            list = employeeRepository.findByDepartmentIgnoreCase(department.trim());
        } else {
            list = employeeRepository.findAll();
        }

        return list.stream()
                .map(EmployeeResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        return EmployeeResponseDTO.fromEntity(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        if (employeeRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new ConflictException("Email '" + requestDTO.getEmail() + "' is already in use by another employee");
        }

        mapDtoToEntity(requestDTO, employee);
        Employee updated = employeeRepository.save(employee);
        return EmployeeResponseDTO.fromEntity(updated);
    }

    @Override
    public EmployeeResponseDTO updateEmployeeStatus(Long id, EmployeeStatus status) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.setStatus(status);
        Employee updated = employeeRepository.save(employee);
        return EmployeeResponseDTO.fromEntity(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private void mapDtoToEntity(EmployeeRequestDTO dto, Employee entity) {
        entity.setFirstName(dto.getFirstName().trim());
        entity.setLastName(dto.getLastName().trim());
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : null);
        entity.setDepartment(dto.getDepartment() != null ? dto.getDepartment().trim() : null);
        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setHourlyRate(dto.getHourlyRate());
    }
}
