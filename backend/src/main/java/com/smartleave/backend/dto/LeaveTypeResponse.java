package com.smartleave.backend.dto;

import java.time.LocalDateTime;

public class LeaveTypeResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer annualQuota;
    private boolean carryForward;
    private boolean active;
    private LocalDateTime createdAt;

    public LeaveTypeResponse(Long id, String name, String code,
            String description, Integer annualQuota, boolean carryForward,
            boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.annualQuota = annualQuota;
        this.carryForward = carryForward;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Integer getAnnualQuota() { return annualQuota; }
    public boolean isCarryForward() { return carryForward; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
