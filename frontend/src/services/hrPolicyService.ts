import api from "./api";

export interface LeaveType {
    id: number;
    name: string;
    code: string;
    description: string | null;
    annualQuota: number;
    carryForward: boolean;
    active: boolean;
    createdAt: string;
}

export interface Holiday {
    id: number;
    date: string;
    name: string;
    description: string | null;
}

export interface LeaveTypePayload {
    name: string;
    code: string;
    description: string;
    annualQuota: number;
    carryForward: boolean;
}

export interface HolidayPayload {
    date: string;
    name: string;
    description: string;
}

export async function getLeaveTypes() {
    const response = await api.get<LeaveType[]>("/hr/leave-types");
    return response.data;
}

export async function saveLeaveType(id: number | null, payload: LeaveTypePayload) {
    const response = id === null
        ? await api.post<LeaveType>("/hr/leave-types", payload)
        : await api.put<LeaveType>(`/hr/leave-types/${id}`, payload);
    return response.data;
}

export async function setLeaveTypeStatus(id: number, active: boolean) {
    const response = await api.patch<LeaveType>(
        `/hr/leave-types/${id}/status`,
        null,
        { params: { active } }
    );
    return response.data;
}

export async function getHolidays() {
    const response = await api.get<Holiday[]>("/hr/holidays");
    return response.data;
}

export async function saveHoliday(id: number | null, payload: HolidayPayload) {
    const response = id === null
        ? await api.post<Holiday>("/hr/holidays", payload)
        : await api.put<Holiday>(`/hr/holidays/${id}`, payload);
    return response.data;
}

export async function deleteHoliday(id: number) {
    await api.delete(`/hr/holidays/${id}`);
}
