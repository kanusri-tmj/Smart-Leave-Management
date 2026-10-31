import api from "./api";

export interface LeaveType {
    id: number;
    name: string;
    code: string;
    description: string | null;
    annualQuota: number;
    carryForward: boolean;
    active: boolean;
}

export interface LeaveApplication {
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
    status: "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";
    appliedAt: string;
    managerRemarks: string | null;
}

export interface LeaveApplicationPayload {
    leaveTypeId: number;
    startDate: string;
    endDate: string;
    reason: string;
}

export async function getLeaveTypes(): Promise<LeaveType[]> {
    const response = await api.get<LeaveType[]>("/employee/leave-types");
    return response.data;
}

export async function submitLeaveApplication(
    payload: LeaveApplicationPayload
): Promise<LeaveApplication> {
    const response = await api.post<LeaveApplication>(
        "/employee/leave-applications",
        payload
    );
    return response.data;
}

export async function getMyLeaveApplications(): Promise<LeaveApplication[]> {
    const response = await api.get<LeaveApplication[]>(
        "/employee/leave-applications"
    );
    return response.data;
}
