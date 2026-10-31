package com.smartleave.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "leave_balances",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"employee_id", "leave_type_id", "leave_year"}
    )
)
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "leave_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer allocatedDays;

    @Column(nullable = false)
    private Integer usedDays = 0;

    @Column(nullable = false)
    private Integer pendingDays = 0;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getAllocatedDays() { return allocatedDays; }
    public void setAllocatedDays(Integer allocatedDays) { this.allocatedDays = allocatedDays; }

    public Integer getUsedDays() { return usedDays; }
    public void setUsedDays(Integer usedDays) { this.usedDays = usedDays; }

    public Integer getPendingDays() { return pendingDays; }
    public void setPendingDays(Integer pendingDays) { this.pendingDays = pendingDays; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
