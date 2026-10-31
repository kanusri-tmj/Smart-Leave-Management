package com.smartleave.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class LeaveTypeRequest {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String code;

    @Size(max = 500)
    private String description;

    @NotNull
    @PositiveOrZero
    private Integer annualQuota;

    private boolean carryForward;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAnnualQuota() { return annualQuota; }
    public void setAnnualQuota(Integer annualQuota) { this.annualQuota = annualQuota; }

    public boolean isCarryForward() { return carryForward; }
    public void setCarryForward(boolean carryForward) { this.carryForward = carryForward; }
}
