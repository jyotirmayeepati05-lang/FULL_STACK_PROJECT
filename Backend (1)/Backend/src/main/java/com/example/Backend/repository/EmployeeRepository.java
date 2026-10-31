package com.example.Backend.repository;

import com.example.Backend.entity.Employee;
import com.example.Backend.entity.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Employee> findByStatus(EmployeeStatus status);

    List<Employee> findByDepartmentIgnoreCase(String department);

    long countByStatus(EmployeeStatus status);
}
