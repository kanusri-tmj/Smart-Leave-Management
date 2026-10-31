package com.smartleave.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LeaveDecisionRequest {

    @NotBlank
    private String decision;

    @Size(max = 1000)
    private String remarks;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
