import api from "./api";

export interface MonthlyTrend {
    month: number;
    approvedRequests: number;
    approvedDays: number;
}

export interface DepartmentUsage {
    department: string;
    approvedRequests: number;
    approvedDays: number;
}

export interface LeaveTypeUsage {
    leaveType: string;
    approvedRequests: number;
    approvedDays: number;
}

export interface EmployeeAbsence {
    employeeName: string;
    employeeCode: string;
    department: string;
    approvedDays: number;
}

export interface LeaveAnalytics {
    year: number;
    totalRequests: number;
    statusCounts: Record<string, number>;
    monthlyTrends: MonthlyTrend[];
    departmentUsage: DepartmentUsage[];
    leaveTypeUsage: LeaveTypeUsage[];
    employeeAbsences: EmployeeAbsence[];
}

export async function getLeaveAnalytics(year: number) {
    const response = await api.get<LeaveAnalytics>(
        "/hr/reports/leave-analytics",
        { params: { year } }
    );
    return response.data;
}
