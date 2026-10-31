package com.smartleave.backend.service;

import com.smartleave.backend.dto.LeaveAnalyticsResponse;
import com.smartleave.backend.entity.Department;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class LeaveAnalyticsService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveAnalyticsService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    public LeaveAnalyticsResponse getLeaveAnalytics(Integer year) {

        List<LeaveRequest> requests = leaveRequestRepository.findAll()
                .stream()
                .filter(request -> request.getStartDate() != null
                        && request.getStartDate().getYear() == year)
                .toList();

        Map<String, Long> statusCounts = new LinkedHashMap<>();

        for (LeaveStatus status : LeaveStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }

        Map<Month, MutableStats> monthly = new EnumMap<>(Month.class);
        Map<String, MutableStats> departments = new TreeMap<>();
        Map<String, MutableStats> leaveTypes = new TreeMap<>();
        Map<Long, EmployeeStats> employees = new LinkedHashMap<>();

        for (LeaveRequest request : requests) {

            String status = request.getStatus().name();
            statusCounts.merge(status, 1L, Long::sum);

            if (request.getStatus() != LeaveStatus.APPROVED) {
                continue;
            }

            long days = request.getTotalDays() == null
                    ? 0L : request.getTotalDays().longValue();

            Month month = request.getStartDate().getMonth();

            monthly.computeIfAbsent(month, key -> new MutableStats())
                    .add(days);

            Employee employee = request.getEmployee();

            Department department = employee.getDepartment();

            String departmentName = department == null
                    ? "Unassigned" : department.getName();

            departments.computeIfAbsent(
                    departmentName, key -> new MutableStats()
            ).add(days);

            String leaveTypeName = request.getLeaveType().getName();

            leaveTypes.computeIfAbsent(
                    leaveTypeName, key -> new MutableStats()
            ).add(days);

            EmployeeStats employeeStats = employees.computeIfAbsent(
                    employee.getId(),
                    key -> new EmployeeStats(
                            employee.getUser().getFullName(),
                            employee.getEmployeeCode(),
                            departmentName
                    )
            );

            employeeStats.add(days);
        }

        List<LeaveAnalyticsResponse.MonthlyTrend> monthlyTrends =
                new ArrayList<>();

        for (Month month : Month.values()) {

            MutableStats stats = monthly.getOrDefault(
                    month, new MutableStats()
            );

            monthlyTrends.add(
                    new LeaveAnalyticsResponse.MonthlyTrend(
                            month.name(),
                            stats.requests,
                            stats.days
                    )
            );
        }

        List<LeaveAnalyticsResponse.DepartmentUsage> departmentUsage =
                departments.entrySet()
                        .stream()
                        .map(entry ->
                                new LeaveAnalyticsResponse.DepartmentUsage(
                                        entry.getKey(),
                                        entry.getValue().requests,
                                        entry.getValue().days
                                ))
                        .sorted(Comparator.comparingLong(
                                LeaveAnalyticsResponse.DepartmentUsage::approvedDays
                        ).reversed())
                        .toList();

        List<LeaveAnalyticsResponse.LeaveTypeUsage> leaveTypeUsage =
                leaveTypes.entrySet()
                        .stream()
                        .map(entry ->
                                new LeaveAnalyticsResponse.LeaveTypeUsage(
                                        entry.getKey(),
                                        entry.getValue().requests,
                                        entry.getValue().days
                                ))
                        .sorted(Comparator.comparingLong(
                                LeaveAnalyticsResponse.LeaveTypeUsage::approvedDays
                        ).reversed())
                        .toList();

        List<LeaveAnalyticsResponse.EmployeeAbsence> employeeAbsences =
                employees.values()
                        .stream()
                        .map(employee ->
                                new LeaveAnalyticsResponse.EmployeeAbsence(
                                        employee.name,
                                        employee.employeeCode,
                                        employee.department,
                                        employee.days
                                ))
                        .sorted(Comparator.comparingLong(
                                LeaveAnalyticsResponse.EmployeeAbsence::approvedDays
                        ).reversed())
                        .limit(10)
                        .toList();

        return new LeaveAnalyticsResponse(
                year,
                requests.size(),
                statusCounts,
                monthlyTrends,
                departmentUsage,
                leaveTypeUsage,
                employeeAbsences
        );
    }

    private static class MutableStats {
        private long requests;
        private long days;

        private void add(long approvedDays) {
            requests++;
            days += approvedDays;
        }
    }

    private static class EmployeeStats {
        private final String name;
        private final String employeeCode;
        private final String department;
        private long days;

        private EmployeeStats(
                String name,
                String employeeCode,
                String department) {
            this.name = name;
            this.employeeCode = employeeCode;
            this.department = department;
        }

        private void add(long approvedDays) {
            days += approvedDays;
        }
    }
}
