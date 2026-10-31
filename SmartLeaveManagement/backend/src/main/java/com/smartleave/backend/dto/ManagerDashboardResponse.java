package com.smartleave.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ManagerDashboardResponse {

    private String managerName;
    private String departmentName;
    private int totalTeamMembers;
    private int activeTeamMembers;
    private int pendingRequests;
    private int employeesOnLeaveToday;

    private List<TeamMemberSummary> teamMembers;
    private List<PendingLeaveSummary> pendingApplications;

    public ManagerDashboardResponse(
            String managerName,
            String departmentName,
            int totalTeamMembers,
            int activeTeamMembers,
            int pendingRequests,
            int employeesOnLeaveToday,
            List<TeamMemberSummary> teamMembers,
            List<PendingLeaveSummary> pendingApplications) {

        this.managerName = managerName;
        this.departmentName = departmentName;
        this.totalTeamMembers = totalTeamMembers;
        this.activeTeamMembers = activeTeamMembers;
        this.pendingRequests = pendingRequests;
        this.employeesOnLeaveToday = employeesOnLeaveToday;
        this.teamMembers = teamMembers;
        this.pendingApplications = pendingApplications;
    }

    public static class TeamMemberSummary {

        private Long employeeId;
        private String employeeName;
        private String employeeCode;
        private String designation;
        private boolean active;

        public TeamMemberSummary(
                Long employeeId,
                String employeeName,
                String employeeCode,
                String designation,
                boolean active) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.employeeCode = employeeCode;
            this.designation = designation;
            this.active = active;
        }

        public Long getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getEmployeeCode() { return employeeCode; }
        public String getDesignation() { return designation; }
        public boolean isActive() { return active; }
    }

    public static class PendingLeaveSummary {

        private Long requestId;
        private String employeeName;
        private String employeeCode;
        private String leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer totalDays;
        private String reason;
        private LocalDateTime appliedAt;

        public PendingLeaveSummary(
                Long requestId,
                String employeeName,
                String employeeCode,
                String leaveType,
                LocalDate startDate,
                LocalDate endDate,
                Integer totalDays,
                String reason,
                LocalDateTime appliedAt) {

            this.requestId = requestId;
            this.employeeName = employeeName;
            this.employeeCode = employeeCode;
            this.leaveType = leaveType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalDays = totalDays;
            this.reason = reason;
            this.appliedAt = appliedAt;
        }

        public Long getRequestId() { return requestId; }
        public String getEmployeeName() { return employeeName; }
        public String getEmployeeCode() { return employeeCode; }
        public String getLeaveType() { return leaveType; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public Integer getTotalDays() { return totalDays; }
        public String getReason() { return reason; }
        public LocalDateTime getAppliedAt() { return appliedAt; }
    }

    public String getManagerName() { return managerName; }
    public String getDepartmentName() { return departmentName; }
    public int getTotalTeamMembers() { return totalTeamMembers; }
    public int getActiveTeamMembers() { return activeTeamMembers; }
    public int getPendingRequests() { return pendingRequests; }
    public int getEmployeesOnLeaveToday() { return employeesOnLeaveToday; }
    public List<TeamMemberSummary> getTeamMembers() { return teamMembers; }
    public List<PendingLeaveSummary> getPendingApplications() { return pendingApplications; }
}
