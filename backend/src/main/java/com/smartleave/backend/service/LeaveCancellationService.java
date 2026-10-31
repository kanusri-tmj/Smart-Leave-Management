package com.smartleave.backend.service;

import com.smartleave.backend.dto.LeaveCancellationResponse;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveBalance;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.repository.LeaveBalanceRepository;
import com.smartleave.backend.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class LeaveCancellationService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public LeaveCancellationService(
            LeaveRequestRepository leaveRequestRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            NotificationService notificationService,
            EmailService emailService) {

        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Transactional
    public LeaveCancellationResponse cancelLeave(Long requestId, User user) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave request not found"));

        if (!request.getEmployee().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot cancel another employee's leave");
        }

        if (request.getStatus() != LeaveStatus.PENDING &&
                request.getStatus() != LeaveStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This leave request cannot be cancelled");
        }

        if (!LocalDate.now().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Leave cannot be cancelled on or after its start date");
        }

        int year = request.getStartDate().getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(
                        request.getEmployee().getId(),
                        request.getLeaveType().getId(),
                        year)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave balance not found"));

        if (request.getStatus() == LeaveStatus.PENDING) {

            if (balance.getPendingDays() < request.getTotalDays()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Invalid pending leave balance");
            }

            balance.setPendingDays(
                    balance.getPendingDays() - request.getTotalDays());

        } else {

            if (balance.getUsedDays() < request.getTotalDays()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Invalid used leave balance");
            }

            balance.setUsedDays(
                    balance.getUsedDays() - request.getTotalDays());
        }

        request.setStatus(LeaveStatus.CANCELLED);

        leaveBalanceRepository.save(balance);
        LeaveRequest savedRequest = leaveRequestRepository.save(request);

        Employee manager = savedRequest.getEmployee().getManager();

        if (manager != null && manager.getUser() != null) {

            User managerUser = manager.getUser();

            String employeeName = savedRequest.getEmployee()
                    .getUser().getFullName();

            String leaveType = savedRequest.getLeaveType().getName();

            String startDate = savedRequest.getStartDate().toString();
            String endDate = savedRequest.getEndDate().toString();

            String message = employeeName
                    + " cancelled their "
                    + leaveType
                    + " leave application from "
                    + startDate
                    + " to "
                    + endDate + ".";

            notificationService.createNotification(
                    managerUser.getId(),
                    "Leave Application Cancelled",
                    message,
                    "LEAVE_CANCELLED"
            );

            String emailMessage = "Hello "
                    + managerUser.getFullName()
                    + ",\n\nAn employee has cancelled a leave application.\n\n"
                    + "Employee: " + employeeName
                    + "\nLeave Type: " + leaveType
                    + "\nStart Date: " + startDate
                    + "\nEnd Date: " + endDate
                    + "\nWorking Days: " + savedRequest.getTotalDays()
                    + "\n\nThe leave balance has been restored or the "
                    + "pending balance has been released.\n\n"
                    + "Regards,\nSmart Leave Management System";

            emailService.sendEmail(
                    managerUser.getEmail(),
                    "Leave Application Cancelled",
                    emailMessage
            );
        }

        return new LeaveCancellationResponse(
                savedRequest.getId(),
                LeaveStatus.CANCELLED,
                "Leave request cancelled successfully"
        );
    }
}
