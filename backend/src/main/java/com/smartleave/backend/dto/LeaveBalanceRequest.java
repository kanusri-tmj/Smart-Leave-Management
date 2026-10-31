package com.smartleave.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class LeaveBalanceRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    private Integer year;

    @NotNull
    @PositiveOrZero
    private Integer allocatedDays;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getAllocatedDays() { return allocatedDays; }
    public void setAllocatedDays(Integer allocatedDays) { this.allocatedDays = allocatedDays; }
}
