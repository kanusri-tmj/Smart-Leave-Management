package com.smartleave.backend.service;

import com.smartleave.backend.dto.DepartmentRequest;
import com.smartleave.backend.dto.DepartmentResponse;
import com.smartleave.backend.entity.Department;
import com.smartleave.backend.repository.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        String name = request.getName().trim();

        if (departmentRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Department already exists");
        }

        Department department = new Department();
        department.setName(name);
        department.setDescription(request.getDescription());

        return toResponse(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = findDepartment(id);
        return toResponse(department);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {

        Department department = findDepartment(id);
        String name = request.getName().trim();

        departmentRepository.findByName(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Department name already exists");
            }
        });

        department.setName(name);
        department.setDescription(request.getDescription());

        return toResponse(departmentRepository.save(department));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Department not found"));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.getCreatedAt()
        );
    }
}
