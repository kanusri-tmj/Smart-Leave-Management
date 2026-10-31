package com.smartleave.backend.service;

import com.smartleave.backend.dto.HRDashboardResponse;
import com.smartleave.backend.entity.Department;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.entity.LeaveType;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.repository.DepartmentRepository;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.LeaveRequestRepository;
import com.smartleave.backend.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HRDashboardService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public HRDashboardService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestRepository leaveRequestRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional(readOnly = true)
    public HRDashboardResponse getDashboard() {

        List<Employee> employees = employeeRepository.findAll();
        List<Department> departments = departmentRepository.findAll();
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        long totalEmployees = employees.size();

        long activeEmployees = employees.stream()
                .filter(Employee::isActive)
                .count();

        long activeLeaveTypes = leaveTypes.stream()
                .filter(LeaveType::isActive)
                .count();

        List<LeaveRequest> recentRequests =
                leaveRequestRepository.findTop10ByOrderByAppliedAtDesc();

        List<HRDashboardResponse.RecentRequest> recentSummaries =
                recentRequests.stream()
                        .map(request -> {
                            Employee employee = request.getEmployee();

                            String departmentName =
                                    employee.getDepartment() != null
                                            ? employee.getDepartment().getName()
                                            : null;

                            return new HRDashboardResponse.RecentRequest(
                                    request.getId(),
                                    employee.getUser().getFullName(),
                                    employee.getEmployeeCode(),
                                    departmentName,
                                    request.getLeaveType().getName(),
                                    request.getStartDate(),
                                    request.getEndDate(),
                                    request.getTotalDays(),
                                    request.getStatus(),
                                    request.getAppliedAt()
                            );
                        })
                        .toList();

        return new HRDashboardResponse(
                totalEmployees,
                activeEmployees,
                departments.size(),
                activeLeaveTypes,
                leaveRequestRepository.countByStatus(LeaveStatus.PENDING),
                leaveRequestRepository.countByStatus(LeaveStatus.APPROVED),
                leaveRequestRepository.countByStatus(LeaveStatus.REJECTED),
                leaveRequestRepository.countByStatus(LeaveStatus.CANCELLED),
                recentSummaries
        );
    }
}
