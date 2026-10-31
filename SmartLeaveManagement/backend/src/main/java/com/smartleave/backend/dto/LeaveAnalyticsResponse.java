package com.smartleave.backend.dto;

import java.util.List;
import java.util.Map;

public record LeaveAnalyticsResponse(
        Integer year,
        long totalRequests,
        Map<String, Long> statusCounts,
        List<MonthlyTrend> monthlyTrends,
        List<DepartmentUsage> departmentUsage,
        List<LeaveTypeUsage> leaveTypeUsage,
        List<EmployeeAbsence> employeeAbsences
) {
    public record MonthlyTrend(
            String month,
            long approvedRequests,
            long approvedDays
    ) {}

    public record DepartmentUsage(
            String department,
            long approvedRequests,
            long approvedDays
    ) {}

    public record LeaveTypeUsage(
            String leaveType,
            long approvedRequests,
            long approvedDays
    ) {}

    public record EmployeeAbsence(
            String employeeName,
            String employeeCode,
            String department,
            long approvedDays
    ) {}
}
