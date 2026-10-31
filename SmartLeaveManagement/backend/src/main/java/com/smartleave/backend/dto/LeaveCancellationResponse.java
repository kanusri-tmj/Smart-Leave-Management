package com.smartleave.backend.dto;

import com.smartleave.backend.enums.LeaveStatus;

public class LeaveCancellationResponse {

    private Long requestId;
    private LeaveStatus status;
    private String message;

    public LeaveCancellationResponse(Long requestId, LeaveStatus status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }

    public Long getRequestId() {
        return requestId;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
