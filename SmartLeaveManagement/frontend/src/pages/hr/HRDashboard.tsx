import { getUnreadCount } from "../../services/notificationService";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { getHRDashboard, type HRDashboardData } from "../../services/hrService";

export default function HRDashboard() {
    const [unreadCount, setUnreadCount] = useState(0);
    useEffect(() => { getUnreadCount().then(setUnreadCount).catch(() => {}); }, []);
    const { logout } = useAuth();
    const [data, setData] = useState<HRDashboardData | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadDashboard = useCallback(async () => {
        try {
            setError("");
            const result = await getHRDashboard();
            setData(result);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to load HR dashboard.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    const stats = [
        { label: "Total Employees", value: data?.totalEmployees ?? 0 },
        { label: "Active Employees", value: data?.activeEmployees ?? 0 },
        { label: "Departments", value: data?.totalDepartments ?? 0 },
        { label: "Active Leave Types", value: data?.activeLeaveTypes ?? 0 },
        { label: "Pending Requests", value: data?.pendingRequests ?? 0 },
        { label: "Approved Requests", value: data?.approvedRequests ?? 0 },
        { label: "Rejected Requests", value: data?.rejectedRequests ?? 0 },
        { label: "Cancelled Requests", value: data?.cancelledRequests ?? 0 },
    ];

    return (
        <div className="min-h-screen bg-slate-100">
            <header className="flex flex-wrap items-center justify-between gap-4 bg-white px-6 py-5 shadow-sm">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">HR Dashboard</h1>
                    <p className="text-sm text-slate-500">Human Resource Management</p>
                </div>
                <div className="flex gap-3">
                    <a href="/notifications" className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white">Notifications {unreadCount > 0 && <span className="ml-1 rounded-full bg-red-600 px-2 text-white">{unreadCount}</span>}</a>
                    <a href="/hr/management" className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800">HR Management</a>
                    <button onClick={loadDashboard}
                        className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold hover:bg-slate-100">
                        Refresh
                    </button>
                    <button onClick={logout}
                        className="rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700">
                        Sign Out
                    </button>
                </div>
            </header>

            <main className="mx-auto max-w-7xl space-y-6 p-6">
                {loading && <p className="text-slate-600">Loading dashboard...</p>}

                {error && (
                    <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
                        {error}
                        <a href="/hr/management" className="rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-800">HR Management</a>
                    <button onClick={loadDashboard} className="ml-3 font-semibold underline">
                            Retry
                        </button>
                    </div>
                )}

                {data && (
                    <>
                        <section>
                            <h2 className="mb-4 text-lg font-bold text-slate-800">Overview</h2>
                            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                                {stats.map((item) => (
                                    <div key={item.label} className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                                        <p className="text-sm text-slate-500">{item.label}</p>
                                        <p className="mt-2 text-3xl font-bold text-slate-900">{item.value}</p>
                                    </div>
                                ))}
                            </div>
                        </section>

                        <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                            <div className="border-b border-slate-200 p-5">
                                <h2 className="text-lg font-bold text-slate-800">Recent Leave Requests</h2>
                                <p className="text-sm text-slate-500">Latest 10 leave applications</p>
                            </div>

                            {data.recentRequests.length === 0 ? (
                                <p className="p-6 text-center text-slate-500">No leave requests found.</p>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm">
                                        <thead className="bg-slate-50 text-slate-600">
                                            <tr>
                                                <th className="p-4">Employee</th>
                                                <th className="p-4">Department</th>
                                                <th className="p-4">Leave Type</th>
                                                <th className="p-4">Dates</th>
                                                <th className="p-4">Days</th>
                                                <th className="p-4">Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {data.recentRequests.map((request) => (
                                                <tr key={request.requestId} className="border-t border-slate-100">
                                                    <td className="p-4">
                                                        <p className="font-semibold text-slate-800">{request.employeeName}</p>
                                                        <p className="text-xs text-slate-500">{request.employeeCode}</p>
                                                    </td>
                                                    <td className="p-4 text-slate-600">{request.departmentName || "Unassigned"}</td>
                                                    <td className="p-4 text-slate-600">{request.leaveType}</td>
                                                    <td className="p-4 text-slate-600">{request.startDate} to {request.endDate}</td>
                                                    <td className="p-4 text-slate-600">{request.totalDays}</td>
                                                    <td className="p-4">
                                                        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${
                                                            request.status === "APPROVED" ? "bg-green-100 text-green-700" :
                                                            request.status === "REJECTED" ? "bg-red-100 text-red-700" :
                                                            request.status === "CANCELLED" ? "bg-slate-200 text-slate-700" :
                                                            "bg-amber-100 text-amber-700"
                                                        }`}>
                                                            {request.status}
                                                        </span>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </section>
                    </>
                )}
            </main>
        </div>
    );
}


