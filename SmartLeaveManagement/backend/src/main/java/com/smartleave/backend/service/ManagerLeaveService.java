package com.smartleave.backend.service;

import com.smartleave.backend.dto.LeaveApplicationResponse;
import com.smartleave.backend.dto.LeaveDecisionRequest;
import com.smartleave.backend.entity.*;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.enums.UserRole;
import com.smartleave.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class ManagerLeaveService {

    private final LeaveRequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public ManagerLeaveService(
            LeaveRequestRepository requestRepository,
            EmployeeRepository employeeRepository,
            LeaveBalanceRepository balanceRepository,
            NotificationService notificationService,
            EmailService emailService) {

        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
        this.balanceRepository = balanceRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<LeaveApplicationResponse> getPendingRequests(User user) {

        List<LeaveRequest> requests;

        if (user.getRole() == UserRole.HR) {
            requests = requestRepository.findByStatusOrderByAppliedAtAsc(
                    LeaveStatus.PENDING);
        } else {
            Employee manager = employeeRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Manager employee profile not found"));

            requests = requestRepository
                    .findByEmployeeManagerIdAndStatusOrderByAppliedAtAsc(
                            manager.getId(), LeaveStatus.PENDING);
        }

        return requests.stream().map(this::toResponse).toList();
    }

    @Transactional
    public LeaveApplicationResponse decideRequest(
            Long requestId, LeaveDecisionRequest decision, User user) {

        String action = decision.getDecision()
                .trim().toUpperCase(Locale.ROOT);

        if (!action.equals("APPROVED") && !action.equals("REJECTED")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Decision must be APPROVED or REJECTED");
        }

        LeaveRequest leaveRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave request not found"));

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This leave request has already been processed");
        }

        if (user.getRole() != UserRole.HR) {
            Employee manager = employeeRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Manager employee profile not found"));

            Employee applicant = leaveRequest.getEmployee();

            if (applicant.getManager() == null
                    || !applicant.getManager().getId().equals(manager.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You are not the assigned manager for this employee");
            }
        }

        LeaveBalance balance = balanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(
                        leaveRequest.getEmployee().getId(),
                        leaveRequest.getLeaveType().getId(),
                        leaveRequest.getStartDate().getYear())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leave balance not found"));

        int days = leaveRequest.getTotalDays();

        if (balance.getPendingDays() < days) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pending leave balance is inconsistent. HR assistance is required");
        }

        balance.setPendingDays(balance.getPendingDays() - days);

        String title;
        String message;
        String emailSubject;
        String emailMessage;

        if (action.equals("APPROVED")) {

            balance.setUsedDays(balance.getUsedDays() + days);
            leaveRequest.setStatus(LeaveStatus.APPROVED);

            title = "Leave Application Approved";
            message = "Your " + leaveRequest.getLeaveType().getName()
                    + " application from "
                    + leaveRequest.getStartDate()
                    + " to " + leaveRequest.getEndDate()
                    + " has been approved.";

            emailSubject = "Your Leave Application Has Been Approved";

            emailMessage = "Hello "
                    + leaveRequest.getEmployee().getUser().getFullName()
                    + ",\n\nYour leave application has been approved.\n\n"
                    + "Leave Type: " + leaveRequest.getLeaveType().getName()
                    + "\nStart Date: " + leaveRequest.getStartDate()
                    + "\nEnd Date: " + leaveRequest.getEndDate()
                    + "\nWorking Days: " + days
                    + "\n\nRegards,\nSmart Leave Management System";

        } else {

            leaveRequest.setStatus(LeaveStatus.REJECTED);

            title = "Leave Application Rejected";
            message = "Your " + leaveRequest.getLeaveType().getName()
                    + " application from "
                    + leaveRequest.getStartDate()
                    + " to " + leaveRequest.getEndDate()
                    + " has been rejected.";

            if (decision.getRemarks() != null
                    && !decision.getRemarks().trim().isEmpty()) {
                message += " Remarks: " + decision.getRemarks().trim();
            }

            emailSubject = "Your Leave Application Has Been Rejected";

            emailMessage = "Hello "
                    + leaveRequest.getEmployee().getUser().getFullName()
                    + ",\n\nYour leave application has been rejected.\n\n"
                    + "Leave Type: " + leaveRequest.getLeaveType().getName()
                    + "\nStart Date: " + leaveRequest.getStartDate()
                    + "\nEnd Date: " + leaveRequest.getEndDate()
                    + "\nWorking Days: " + days
                    + "\nRemarks: "
                    + (decision.getRemarks() == null
                            ? "Not provided"
                            : decision.getRemarks().trim())
                    + "\n\nRegards,\nSmart Leave Management System";
        }

        leaveRequest.setManagerRemarks(
                decision.getRemarks() == null
                        ? null
                        : decision.getRemarks().trim());

        balanceRepository.save(balance);

        LeaveRequest savedRequest = requestRepository.save(leaveRequest);

        User employeeUser = savedRequest.getEmployee().getUser();

        notificationService.createNotification(
                employeeUser.getId(),
                title,
                message,
                "LEAVE_" + action
        );

        emailService.sendEmail(
                employeeUser.getEmail(),
                emailSubject,
                emailMessage
        );

        return toResponse(savedRequest);
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
