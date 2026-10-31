package com.smartleave.backend.dto;

import com.smartleave.backend.enums.LeaveStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LeaveDashboardResponse {

    private String employeeName;
    private String employeeCode;
    private String designation;
    private String departmentName;
    private Integer year;

    private int totalAllocatedDays;
    private int usedDays;
    private int pendingDays;
    private int availableDays;

    private int pendingRequests;
    private int approvedRequests;
    private int rejectedRequests;
    private int cancelledRequests;

    private List<BalanceSummary> leaveBalances;
    private List<RecentLeaveSummary> recentApplications;

    public LeaveDashboardResponse(
            String employeeName,
            String employeeCode,
            String designation,
            String departmentName,
            Integer year,
            int totalAllocatedDays,
            int usedDays,
            int pendingDays,
            int availableDays,
            int pendingRequests,
            int approvedRequests,
            int rejectedRequests,
            int cancelledRequests,
            List<BalanceSummary> leaveBalances,
            List<RecentLeaveSummary> recentApplications) {

        this.employeeName = employeeName;
        this.employeeCode = employeeCode;
        this.designation = designation;
        this.departmentName = departmentName;
        this.year = year;
        this.totalAllocatedDays = totalAllocatedDays;
        this.usedDays = usedDays;
        this.pendingDays = pendingDays;
        this.availableDays = availableDays;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.rejectedRequests = rejectedRequests;
        this.cancelledRequests = cancelledRequests;
        this.leaveBalances = leaveBalances;
        this.recentApplications = recentApplications;
    }

    public static class BalanceSummary {
        private String leaveType;
        private int allocatedDays;
        private int usedDays;
        private int pendingDays;
        private int availableDays;

        public BalanceSummary(String leaveType, int allocatedDays,
                int usedDays, int pendingDays, int availableDays) {
            this.leaveType = leaveType;
            this.allocatedDays = allocatedDays;
            this.usedDays = usedDays;
            this.pendingDays = pendingDays;
            this.availableDays = availableDays;
        }

        public String getLeaveType() { return leaveType; }
        public int getAllocatedDays() { return allocatedDays; }
        public int getUsedDays() { return usedDays; }
        public int getPendingDays() { return pendingDays; }
        public int getAvailableDays() { return availableDays; }
    }

    public static class RecentLeaveSummary {
        private Long requestId;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer totalDays;
        private LeaveStatus status;
        private LocalDateTime appliedAt;

        public RecentLeaveSummary(Long requestId, String leaveType,
                LocalDate startDate, LocalDate endDate, Integer totalDays,
                LeaveStatus status, LocalDateTime appliedAt) {
            this.requestId = requestId;
            this.leaveType = leaveType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalDays = totalDays;
            this.status = status;
            this.appliedAt = appliedAt;
        }

        public Long getRequestId() { return requestId; }
        public String getLeaveType() { return leaveType; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public Integer getTotalDays() { return totalDays; }
        public LeaveStatus getStatus() { return status; }
        public LocalDateTime getAppliedAt() { return appliedAt; }
    }

    public String getEmployeeName() { return employeeName; }
    public String getEmployeeCode() { return employeeCode; }
    public String getDesignation() { return designation; }
    public String getDepartmentName() { return departmentName; }
    public Integer getYear() { return year; }
    public int getTotalAllocatedDays() { return totalAllocatedDays; }
    public int getUsedDays() { return usedDays; }
    public int getPendingDays() { return pendingDays; }
    public int getAvailableDays() { return availableDays; }
    public int getPendingRequests() { return pendingRequests; }
    public int getApprovedRequests() { return approvedRequests; }
    public int getRejectedRequests() { return rejectedRequests; }
    public int getCancelledRequests() { return cancelledRequests; }
    public List<BalanceSummary> getLeaveBalances() { return leaveBalances; }
    public List<RecentLeaveSummary> getRecentApplications() { return recentApplications; }
}

