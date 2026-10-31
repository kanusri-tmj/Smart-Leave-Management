import api from "./api";

export interface ManagerDashboardData {
    managerName: string;
    departmentName: string | null;
    totalTeamMembers: number;
    activeTeamMembers: number;
    pendingRequests: number;
    employeesOnLeaveToday: number;
    teamMembers: {
        employeeId: number;
        employeeName: string;
        employeeCode: string;
        designation: string;
        active: boolean;
    }[];
}

export interface PendingLeave {
    id: number;
    employeeId: number;
    employeeName: string;
    employeeCode: string;
    departmentName: string;
    leaveTypeId: number;
    leaveTypeName: string;
    leaveTypeCode: string;
    startDate: string;
    endDate: string;
    totalDays: number;
    reason: string;
    status: string;
    appliedAt: string;
    managerRemarks: string | null;
}

export async function getManagerDashboard() {
    const response = await api.get<ManagerDashboardData>("/manager/dashboard");
    return response.data;
}

export async function getPendingLeaves() {
    const response = await api.get<PendingLeave[]>("/manager/leave-applications/pending");
    return response.data;
}

export async function decideLeave(
    id: number,
    decision: "APPROVED" | "REJECTED",
    remarks: string
) {
    const response = await api.patch<PendingLeave>(
        `/manager/leave-applications/${id}/decision`,
        { decision, remarks }
    );
    return response.data;
}
