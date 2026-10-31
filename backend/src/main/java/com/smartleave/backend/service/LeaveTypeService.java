package com.smartleave.backend.service;

import com.smartleave.backend.dto.LeaveTypeRequest;
import com.smartleave.backend.dto.LeaveTypeResponse;
import com.smartleave.backend.entity.LeaveType;
import com.smartleave.backend.repository.LeaveTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Transactional
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {

        String name = request.getName().trim();
        String code = request.getCode().trim().toUpperCase();

        if (leaveTypeRepository.existsByName(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Leave type name already exists");
        }

        if (leaveTypeRepository.existsByCode(code)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Leave type code already exists");
        }

        LeaveType leaveType = new LeaveType();
        leaveType.setName(name);
        leaveType.setCode(code);
        leaveType.setDescription(request.getDescription());
        leaveType.setAnnualQuota(request.getAnnualQuota());
        leaveType.setCarryForward(request.isCarryForward());
        leaveType.setActive(true);

        return toResponse(leaveTypeRepository.save(leaveType));
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        return leaveTypeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveTypeById(Long id) {
        return toResponse(findLeaveType(id));
    }

    @Transactional
    public LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request) {

        LeaveType leaveType = findLeaveType(id);
        String name = request.getName().trim();
        String code = request.getCode().trim().toUpperCase();

        leaveTypeRepository.findByName(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Leave type name already exists");
            }
        });

        leaveTypeRepository.findByCode(code).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Leave type code already exists");
            }
        });

        leaveType.setName(name);
        leaveType.setCode(code);
        leaveType.setDescription(request.getDescription());
        leaveType.setAnnualQuota(request.getAnnualQuota());
        leaveType.setCarryForward(request.isCarryForward());

        return toResponse(leaveTypeRepository.save(leaveType));
    }

    @Transactional
    public LeaveTypeResponse updateActiveStatus(Long id, boolean active) {
        LeaveType leaveType = findLeaveType(id);
        leaveType.setActive(active);
        return toResponse(leaveTypeRepository.save(leaveType));
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getActiveLeaveTypes() {
        return leaveTypeRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private LeaveType findLeaveType(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave type not found"));
    }

    private LeaveTypeResponse toResponse(LeaveType leaveType) {
        return new LeaveTypeResponse(
                leaveType.getId(),
                leaveType.getName(),
                leaveType.getCode(),
                leaveType.getDescription(),
                leaveType.getAnnualQuota(),
                leaveType.isCarryForward(),
                leaveType.isActive(),
                leaveType.getCreatedAt()
        );
    }
}

