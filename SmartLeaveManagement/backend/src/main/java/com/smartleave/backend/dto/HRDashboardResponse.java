package com.smartleave.backend.dto;

import com.smartleave.backend.enums.LeaveStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HRDashboardResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long totalDepartments;
    private long activeLeaveTypes;

    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long cancelledRequests;

    private List<RecentRequest> recentRequests;

    public HRDashboardResponse(
            long totalEmployees,
            long activeEmployees,
            long totalDepartments,
            long activeLeaveTypes,
            long pendingRequests,
            long approvedRequests,
            long rejectedRequests,
            long cancelledRequests,
            List<RecentRequest> recentRequests) {

        this.totalEmployees = totalEmployees;
        this.activeEmployees = activeEmployees;
        this.totalDepartments = totalDepartments;
        this.activeLeaveTypes = activeLeaveTypes;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.rejectedRequests = rejectedRequests;
        this.cancelledRequests = cancelledRequests;
        this.recentRequests = recentRequests;
    }

    public static class RecentRequest {

        private Long requestId;
        private String employeeName;
        private String employeeCode;
        private String departmentName;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer totalDays;
        private LeaveStatus status;
        private LocalDateTime appliedAt;

        public RecentRequest(
                Long requestId,
                String employeeName,
                String employeeCode,
                String departmentName,
                String leaveType,
                LocalDate startDate,
                LocalDate endDate,
                Integer totalDays,
                LeaveStatus status,
                LocalDateTime appliedAt) {

            this.requestId = requestId;
            this.employeeName = employeeName;
            this.employeeCode = employeeCode;
            this.departmentName = departmentName;
            this.leaveType = leaveType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalDays = totalDays;
            this.status = status;
            this.appliedAt = appliedAt;
        }

        public Long getRequestId() { return requestId; }
        public String getEmployeeName() { return employeeName; }
        public String getEmployeeCode() { return employeeCode; }
        public String getDepartmentName() { return departmentName; }
        public String getLeaveType() { return leaveType; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public Integer getTotalDays() { return totalDays; }
        public LeaveStatus getStatus() { return status; }
        public LocalDateTime getAppliedAt() { return appliedAt; }
    }

    public long getTotalEmployees() { return totalEmployees; }
    public long getActiveEmployees() { return activeEmployees; }
    public long getTotalDepartments() { return totalDepartments; }
    public long getActiveLeaveTypes() { return activeLeaveTypes; }
    public long getPendingRequests() { return pendingRequests; }
    public long getApprovedRequests() { return approvedRequests; }
    public long getRejectedRequests() { return rejectedRequests; }
    public long getCancelledRequests() { return cancelledRequests; }
    public List<RecentRequest> getRecentRequests() { return recentRequests; }
}
