package com.smartleave.backend.dto;

import java.time.LocalDate;

public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String phone;
    private String designation;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerName;
    private LocalDate joiningDate;
    private boolean active;

    public EmployeeResponse(Long id, String employeeCode, Long userId,
            String fullName, String email, String role, String phone,
            String designation, Long departmentId, String departmentName,
            Long managerId, String managerName, LocalDate joiningDate,
            boolean active) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.designation = designation;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.managerId = managerId;
        this.managerName = managerName;
        this.joiningDate = joiningDate;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getEmployeeCode() { return employeeCode; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getDesignation() { return designation; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public Long getManagerId() { return managerId; }
    public String getManagerName() { return managerName; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public boolean isActive() { return active; }
}
