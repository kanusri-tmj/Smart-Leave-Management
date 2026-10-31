package com.smartleave.backend.controller;

import com.smartleave.backend.dto.EmployeeCreateRequest;
import com.smartleave.backend.dto.EmployeeResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/hr/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {
        return employeeService.createEmployee(request);
    }

    @GetMapping("/hr/employees")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/hr/employees/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/employee/profile")
    public EmployeeResponse getMyProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return employeeService.getEmployeeProfile(user.getId());
    }
}
