import api from "./api";
import type { EmployeeDashboardData } from "./dashboardTypes";

export async function getEmployeeDashboard(
    year: number = new Date().getFullYear()
): Promise<EmployeeDashboardData> {
    const response = await api.get<EmployeeDashboardData>(
        "/employee/dashboard",
        {
            params: { year },
        }
    );

    return response.data;
}
