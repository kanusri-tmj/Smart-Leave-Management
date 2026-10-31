import api from "./api";

export interface HRUser {
    id: number;
    fullName: string;
    email: string;
    role: "EMPLOYEE" | "MANAGER" | "HR";
    active: boolean;
    createdAt: string;
}

export interface Department {
    id: number;
    name: string;
    description: string | null;
    createdAt: string;
}

export interface Employee {
    id: number;
    userId: number;
    employeeCode: string;
    fullName: string;
    email: string;
    role: "EMPLOYEE" | "MANAGER" | "HR";
    phone: string;
    designation: string;
    departmentId: number;
    departmentName: string;
    managerId: number | null;
    managerName: string | null;
    joiningDate: string;
    active: boolean;
}

export interface EmployeeCreateData {
    userId: number;
    employeeCode: string;
    phone: string;
    designation: string;
    departmentId: number;
    managerId: number | null;
    joiningDate: string;
}

export async function getHRUsers() {
    const response = await api.get<HRUser[]>("/hr/users");
    return response.data;
}

export async function updateUserRole(id: number, role: HRUser["role"]) {
    const response = await api.patch<HRUser>(`/hr/users/${id}/role`, { role });
    return response.data;
}

export async function updateUserStatus(id: number, active: boolean) {
    const response = await api.patch<HRUser>(`/hr/users/${id}/status`, { active });
    return response.data;
}

export async function getDepartments() {
    const response = await api.get<Department[]>("/hr/departments");
    return response.data;
}

export async function createDepartment(name: string, description: string) {
    const response = await api.post<Department>("/hr/departments", { name, description });
    return response.data;
}

export async function updateDepartment(id: number, name: string, description: string) {
    const response = await api.put<Department>(`/hr/departments/${id}`, { name, description });
    return response.data;
}

export async function getHREmployees() {
    const response = await api.get<Employee[]>("/hr/employees");
    return response.data;
}

export async function createEmployee(data: EmployeeCreateData) {
    const response = await api.post<Employee>("/hr/employees", data);
    return response.data;
}
