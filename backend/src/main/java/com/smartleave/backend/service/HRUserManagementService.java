package com.smartleave.backend.service;

import com.smartleave.backend.dto.HRUserResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.UserRole;
import com.smartleave.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HRUserManagementService {

    private final UserRepository userRepository;

    public HRUserManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<HRUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public HRUserResponse updateRole(
            Long userId, UserRole newRole, User currentUser) {

        User target = getUser(userId);
        preventSelfManagement(target, currentUser);

        if (target.isActive()
                && target.getRole() == UserRole.HR
                && newRole != UserRole.HR
                && userRepository.countByRoleAndActiveTrue(UserRole.HR) <= 1) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot remove the last active HR account");
        }

        target.setRole(newRole);

        return toResponse(userRepository.save(target));
    }

    @Transactional
    public HRUserResponse updateStatus(
            Long userId, boolean active, User currentUser) {

        User target = getUser(userId);
        preventSelfManagement(target, currentUser);

        if (!active
                && target.isActive()
                && target.getRole() == UserRole.HR
                && userRepository.countByRoleAndActiveTrue(UserRole.HR) <= 1) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot deactivate the last active HR account");
        }

        target.setActive(active);

        return toResponse(userRepository.save(target));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User account not found"));
    }

    private void preventSelfManagement(User target, User currentUser) {
        if (target.getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot change your own role or account status");
        }
    }

    private HRUserResponse toResponse(User user) {
        return new HRUserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
