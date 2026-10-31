export interface LeaveBalance {
    leaveType: string;
    allocatedDays: number;
    usedDays: number;
    pendingDays: number;
    availableDays: number;
}

export interface RecentLeave {
    requestId: number;
    leaveType: string;
    startDate: string;
    endDate: string;
    totalDays: number;
    status: "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";
    appliedAt: string;
}

export interface EmployeeDashboardData {
    employeeName: string;
    employeeCode: string;
    designation: string;
    departmentName: string | null;
    year: number;

    totalAllocatedDays: number;
    usedDays: number;
    pendingDays: number;
    availableDays: number;

    pendingRequests: number;
    approvedRequests: number;
    rejectedRequests: number;
    cancelledRequests: number;

    leaveBalances: LeaveBalance[];
    recentApplications: RecentLeave[];
}
