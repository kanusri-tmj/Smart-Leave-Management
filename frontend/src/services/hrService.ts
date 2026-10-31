import api from "./api";

export interface HRDashboardData {
    totalEmployees: number;
    activeEmployees: number;
    totalDepartments: number;
    activeLeaveTypes: number;
    pendingRequests: number;
    approvedRequests: number;
    rejectedRequests: number;
    cancelledRequests: number;
    recentRequests: {
        requestId: number;
        employeeName: string;
        employeeCode: string;
        departmentName: string | null;
        leaveType: string;
        startDate: string;
        endDate: string;
        totalDays: number;
        status: string;
        appliedAt: string;
    }[];
}

export async function getHRDashboard(): Promise<HRDashboardData> {
    const response = await api.get<HRDashboardData>("/hr/dashboard");
    return response.data;
}
