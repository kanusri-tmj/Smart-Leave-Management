import api from "./api";

export interface EmployeeOption {
    id: number;
    employeeCode: string;
    fullName: string;
    role: string;
    departmentName: string | null;
    active: boolean;
}

export interface Balance {
    id: number;
    employeeId: number;
    employeeName: string;
    employeeCode: string;
    leaveTypeId: number;
    leaveTypeName: string;
    leaveTypeCode: string;
    year: number;
    allocatedDays: number;
    usedDays: number;
    pendingDays: number;
    availableDays: number;
    updatedAt: string;
}

export interface BalancePayload {
    employeeId: number;
    leaveTypeId: number;
    year: number;
    allocatedDays: number;
}

export async function getEmployees() {
    const response = await api.get<EmployeeOption[]>("/hr/employees");
    return response.data;
}

export async function getBalances(year: number) {
    const response = await api.get<Balance[]>("/hr/leave-balances", {
        params: { year }
    });
    return response.data;
}

export async function createBalance(payload: BalancePayload) {
    const response = await api.post<Balance>("/hr/leave-balances", payload);
    return response.data;
}

export async function updateAllocation(id: number, allocatedDays: number) {
    const response = await api.patch<Balance>(
        `/hr/leave-balances/${id}/allocation`,
        null,
        { params: { allocatedDays } }
    );
    return response.data;
}

