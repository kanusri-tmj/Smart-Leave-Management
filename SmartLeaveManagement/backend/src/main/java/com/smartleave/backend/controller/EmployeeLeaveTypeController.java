package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveTypeResponse;
import com.smartleave.backend.service.LeaveTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/leave-types")
public class EmployeeLeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public EmployeeLeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @GetMapping
    public List<LeaveTypeResponse> getActiveLeaveTypes() {
        return leaveTypeService.getActiveLeaveTypes();
    }
}
