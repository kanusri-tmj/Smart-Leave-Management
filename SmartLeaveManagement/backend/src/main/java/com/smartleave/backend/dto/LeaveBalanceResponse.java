package com.smartleave.backend.dto;

import java.time.LocalDateTime;

public class LeaveBalanceResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private Integer year;
    private Integer allocatedDays;
    private Integer usedDays;
    private Integer pendingDays;
    private Integer availableDays;
    private LocalDateTime updatedAt;

    public LeaveBalanceResponse(Long id, Long employeeId, String employeeName,
            String employeeCode, Long leaveTypeId, String leaveTypeName,
            String leaveTypeCode, Integer year, Integer allocatedDays,
            Integer usedDays, Integer pendingDays, Integer availableDays,
            LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
        this.leaveTypeCode = leaveTypeCode;
        this.year = year;
        this.allocatedDays = allocatedDays;
        this.usedDays = usedDays;
        this.pendingDays = pendingDays;
        this.availableDays = availableDays;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeCode() { return employeeCode; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public String getLeaveTypeCode() { return leaveTypeCode; }
    public Integer getYear() { return year; }
    public Integer getAllocatedDays() { return allocatedDays; }
    public Integer getUsedDays() { return usedDays; }
    public Integer getPendingDays() { return pendingDays; }
    public Integer getAvailableDays() { return availableDays; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
