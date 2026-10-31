package com.smartleave.backend.dto;

import com.smartleave.backend.enums.LeaveStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveApplicationResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private String departmentName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime appliedAt;
    private String managerRemarks;

    public LeaveApplicationResponse(Long id, Long employeeId,
            String employeeName, String employeeCode, String departmentName,
            Long leaveTypeId, String leaveTypeName, String leaveTypeCode,
            LocalDate startDate, LocalDate endDate, Integer totalDays,
            String reason, LeaveStatus status, LocalDateTime appliedAt,
            String managerRemarks) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.departmentName = departmentName;
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
        this.leaveTypeCode = leaveTypeCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.reason = reason;
        this.status = status;
        this.appliedAt = appliedAt;
        this.managerRemarks = managerRemarks;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeCode() { return employeeCode; }
    public String getDepartmentName() { return departmentName; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public String getLeaveTypeCode() { return leaveTypeCode; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Integer getTotalDays() { return totalDays; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public String getManagerRemarks() { return managerRemarks; }
}
