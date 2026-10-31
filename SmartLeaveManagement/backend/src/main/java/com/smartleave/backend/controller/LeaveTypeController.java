package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveTypeRequest;
import com.smartleave.backend.dto.LeaveTypeResponse;
import com.smartleave.backend.service.LeaveTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveTypeResponse createLeaveType(
            @Valid @RequestBody LeaveTypeRequest request) {
        return leaveTypeService.createLeaveType(request);
    }

    @GetMapping
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        return leaveTypeService.getAllLeaveTypes();
    }

    @GetMapping("/{id}")
    public LeaveTypeResponse getLeaveTypeById(@PathVariable Long id) {
        return leaveTypeService.getLeaveTypeById(id);
    }

    @PutMapping("/{id}")
    public LeaveTypeResponse updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeRequest request) {
        return leaveTypeService.updateLeaveType(id, request);
    }

    @PatchMapping("/{id}/status")
    public LeaveTypeResponse updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return leaveTypeService.updateActiveStatus(id, active);
    }
}
