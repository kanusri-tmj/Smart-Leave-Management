import api from "./api";

export interface AuthUser {
    userId: number;
    fullName: string;
    email: string;
    role: "EMPLOYEE" | "MANAGER" | "HR";
}

export interface AuthResponse {
    token: string;
    tokenType: string;
    userId: number;
    fullName: string;
    email: string;
    role: "EMPLOYEE" | "MANAGER" | "HR";
}

export interface RegisterData {
    fullName: string;
    email: string;
    password: string;
}

export async function loginUser(email: string, password: string) {
    const response = await api.post<AuthResponse>("/auth/login", {
        email,
        password,
    });

    return response.data;
}

export async function registerUser(data: RegisterData) {
    const response = await api.post<AuthResponse>("/auth/register", data);
    return response.data;
}
