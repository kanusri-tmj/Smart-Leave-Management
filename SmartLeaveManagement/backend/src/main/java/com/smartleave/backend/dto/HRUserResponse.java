package com.smartleave.backend.dto;

import com.smartleave.backend.enums.UserRole;
import java.time.LocalDateTime;

public class HRUserResponse {

    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;

    public HRUserResponse(
            Long id,
            String fullName,
            String email,
            UserRole role,
            boolean active,
            LocalDateTime createdAt) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
