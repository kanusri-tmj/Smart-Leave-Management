package com.smartleave.backend.service;

import com.smartleave.backend.dto.EmployeeCreateRequest;
import com.smartleave.backend.dto.EmployeeResponse;
import com.smartleave.backend.entity.Department;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.UserRole;
import com.smartleave.backend.repository.DepartmentRepository;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
            UserRepository userRepository,
            DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == UserRole.HR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "HR users cannot be assigned employee profiles");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot assign an inactive user");
        }

        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee code already exists");
        }

        if (employeeRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee profile already exists for this user");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found"));

        Employee manager = null;

        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Manager not found"));

            if (manager.getUser().getRole() != UserRole.MANAGER) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Selected employee is not a manager");
            }

            if (manager.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "An employee cannot be their own manager");
            }
        }

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setEmployeeCode(request.getEmployeeCode().trim());
        employee.setPhone(request.getPhone().trim());
        employee.setDesignation(request.getDesignation().trim());
        employee.setDepartment(department);
        employee.setManager(manager);
        employee.setJoiningDate(request.getJoiningDate());
        employee.setActive(true);

        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        return toResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeProfile(Long userId) {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee profile not found"));

        return toResponse(employee);
    }

    private EmployeeResponse toResponse(Employee employee) {

        User user = employee.getUser();
        Department department = employee.getDepartment();
        Employee manager = employee.getManager();

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                employee.getPhone(),
                employee.getDesignation(),
                department.getId(),
                department.getName(),
                manager != null ? manager.getId() : null,
                manager != null ? manager.getUser().getFullName() : null,
                employee.getJoiningDate(),
                employee.isActive()
        );
    }
}

