package com.smartleave.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartleave.backend.dto.LeaveApplicationRequest;
import com.smartleave.backend.dto.LeaveApplicationResponse;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.Holiday;
import com.smartleave.backend.entity.LeaveBalance;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.entity.LeaveType;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.HolidayRepository;
import com.smartleave.backend.repository.LeaveBalanceRepository;
import com.smartleave.backend.repository.LeaveRequestRepository;
import com.smartleave.backend.repository.LeaveTypeRepository;

@Service
public class LeaveApplicationService {

    private final LeaveRequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final HolidayRepository holidayRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public LeaveApplicationService(
            LeaveRequestRepository requestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveBalanceRepository balanceRepository,
            HolidayRepository holidayRepository,
            NotificationService notificationService,
            EmailService emailService) {

        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.balanceRepository = balanceRepository;
        this.holidayRepository = holidayRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Transactional
    public LeaveApplicationResponse applyForLeave(
            Long userId, LeaveApplicationRequest request) {

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date cannot be before start date");
        }

        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Leave cannot start in the past");
        }

        if (request.getStartDate().getYear()
                != request.getEndDate().getYear()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A leave application cannot span two calendar years");
        }

        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inactive employees cannot apply for leave");
        }

        LeaveType leaveType = leaveTypeRepository
                .findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave type not found"));

        if (!leaveType.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This leave type is inactive");
        }

        int totalDays = calculateWorkingDays(
                request.getStartDate(),
                request.getEndDate());

        if (totalDays <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected dates contain no working days");
        }

        int year = request.getStartDate().getYear();

        LeaveBalance balance = balanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(
                        employee.getId(), leaveType.getId(), year)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No leave balance exists for this leave type and year"));

        int availableDays = balance.getAllocatedDays()
                - balance.getUsedDays()
                - balance.getPendingDays();

        if (totalDays > availableDays) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient leave balance");
        }

        boolean overlapping = requestRepository
                .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employee.getId(),
                        List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                        request.getEndDate(),
                        request.getStartDate());

        if (overlapping) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have a pending or approved leave request during these dates");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setTotalDays(totalDays);
        leaveRequest.setReason(request.getReason().trim());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        balance.setPendingDays(
                balance.getPendingDays() + totalDays);

        balanceRepository.save(balance);

        LeaveRequest savedRequest = requestRepository.save(leaveRequest);

        Employee manager = employee.getManager();

        if (manager != null && manager.getUser() != null) {

            User managerUser = manager.getUser();

            String title = "New Leave Application";

            String message = employee.getUser().getFullName()
                    + " submitted a "
                    + leaveType.getName()
                    + " leave application from "
                    + request.getStartDate()
                    + " to "
                    + request.getEndDate()
                    + " (" + totalDays + " working days).";

            notificationService.createNotification(
                    managerUser.getId(),
                    title,
                    message,
                    "LEAVE_SUBMITTED"
            );

            String emailSubject = "New Leave Application Submitted";

            String emailMessage = "Hello "
                    + managerUser.getFullName()
                    + ",\n\nA new leave application has been submitted.\n\n"
                    + "Employee: " + employee.getUser().getFullName()
                    + "\nEmployee Code: " + employee.getEmployeeCode()
                    + "\nLeave Type: " + leaveType.getName()
                    + "\nStart Date: " + request.getStartDate()
                    + "\nEnd Date: " + request.getEndDate()
                    + "\nWorking Days: " + totalDays
                    + "\nReason: " + request.getReason()
                    + "\n\nPlease log in to Smart Leave Management System "
                    + "to review the application.";

            emailService.sendEmail(
                    managerUser.getEmail(),
                    emailSubject,
                    emailMessage
            );
        }

        return toResponse(savedRequest);
    }

    private int calculateWorkingDays(
            LocalDate startDate, LocalDate endDate) {

        Set<LocalDate> holidays = holidayRepository
                .findByDateBetweenOrderByDateAsc(startDate, endDate)
                .stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        int workingDays = 0;

        for (LocalDate date = startDate;
             !date.isAfter(endDate);
             date = date.plusDays(1)) {

            DayOfWeek day = date.getDayOfWeek();

            boolean weekend = day == DayOfWeek.SATURDAY
                    || day == DayOfWeek.SUNDAY;

            if (!weekend && !holidays.contains(date)) {
                workingDays++;
            }
        }

        return workingDays;
    }

    @Transactional(readOnly = true)
    public List<LeaveApplicationResponse> getMyApplications(Long userId) {

        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        return requestRepository
                .findByEmployeeIdOrderByAppliedAtDesc(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveApplicationResponse getMyApplication(
            Long userId, Long requestId) {

        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        LeaveRequest leaveRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave request not found"));

        if (!leaveRequest.getEmployee().getId()
                .equals(employee.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot view another employee's leave request");
        }

        return toResponse(leaveRequest);
    }

    private LeaveApplicationResponse toResponse(LeaveRequest request) {

        Employee employee = request.getEmployee();
        LeaveType leaveType = request.getLeaveType();

        return new LeaveApplicationResponse(
                request.getId(),
                employee.getId(),
                employee.getUser().getFullName(),
                employee.getEmployeeCode(),
                employee.getDepartment().getName(),
                leaveType.getId(),
                leaveType.getName(),
                leaveType.getCode(),
                request.getStartDate(),
                request.getEndDate(),
                request.getTotalDays(),
                request.getReason(),
                request.getStatus(),
                request.getAppliedAt(),
                request.getManagerRemarks()
        );
    }
}
