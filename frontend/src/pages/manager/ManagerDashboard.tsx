import { getUnreadCount } from "../../services/notificationService";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import {
    decideLeave,
    getManagerDashboard,
    getPendingLeaves,
    type ManagerDashboardData,
    type PendingLeave
} from "../../services/managerService";

export default function ManagerDashboard() {
    const [unreadCount, setUnreadCount] = useState(0);
    useEffect(() => { getUnreadCount().then(setUnreadCount).catch(() => {}); }, []);
    const { user, logout } = useAuth();

    const [dashboard, setDashboard] = useState<ManagerDashboardData | null>(null);
    const [requests, setRequests] = useState<PendingLeave[]>([]);
    const [remarks, setRemarks] = useState<Record<number, string>>({});
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState<number | null>(null);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const loadDashboard = useCallback(async () => {
        try {
            setError("");
            const [dashboardData, pendingData] = await Promise.all([
                getManagerDashboard(),
                getPendingLeaves()
            ]);

            setDashboard(dashboardData);
            setRequests(pendingData);
        } catch (err: any) {
            setError(err.response?.data?.message || err.response?.data?.detail || "Unable to load manager dashboard.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    async function handleDecision(id: number, decision: "APPROVED" | "REJECTED") {
        try {
            setProcessing(id);
            setError("");
            setMessage("");

            await decideLeave(id, decision, remarks[id] || "");

            setMessage(`Leave application ${decision.toLowerCase()} successfully.`);
            await loadDashboard();
        } catch (err: any) {
            setError(err.response?.data?.message || err.response?.data?.detail || "Unable to process leave application.");
        } finally {
            setProcessing(null);
        }
    }

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-slate-50">
                <p className="text-slate-600">Loading manager dashboard...</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-slate-50">
            <header className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-200 bg-white px-6 py-5">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Manager Dashboard</h1>
                    <p className="mt-1 text-sm text-slate-500">
                        {dashboard?.managerName || user?.fullName} Ã‚Â· {dashboard?.departmentName || "Department"}
                    </p>
                </div>

                <div className="flex gap-3">
                    <a href="/notifications" className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white">Notifications {unreadCount > 0 && <span className="ml-1 rounded-full bg-red-600 px-2 text-white">{unreadCount}</span>}</a>
                    <button
                        onClick={loadDashboard}
                        className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
                    >
                        Refresh
                    </button>
                    <button
                        onClick={logout}
                        className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-700"
                    >
                        Sign Out
                    </button>
                </div>
            </header>

            <main className="mx-auto max-w-7xl space-y-8 p-6">
                {error && (
                    <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                        {error}
                    </div>
                )}

                {message && (
                    <div className="rounded-xl border border-green-200 bg-green-50 p-4 text-sm text-green-700">
                        {message}
                    </div>
                )}

                <section>
                    <h2 className="mb-4 text-lg font-bold text-slate-900">Team Overview</h2>

                    <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                        {[
                            { label: "Total Team Members", value: dashboard?.totalTeamMembers ?? 0 },
                            { label: "Active Employees", value: dashboard?.activeTeamMembers ?? 0 },
                            { label: "Pending Requests", value: dashboard?.pendingRequests ?? 0 },
                            { label: "On Leave Today", value: dashboard?.employeesOnLeaveToday ?? 0 }
                        ].map((item) => (
                            <div key={item.label} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                                <p className="text-sm font-medium text-slate-500">{item.label}</p>
                                <p className="mt-3 text-3xl font-bold text-slate-900">{item.value}</p>
                            </div>
                        ))}
                    </div>
                </section>

                <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                    <div className="border-b border-slate-200 p-5">
                        <h2 className="text-lg font-bold text-slate-900">Pending Leave Applications</h2>
                        <p className="mt-1 text-sm text-slate-500">Review and process your team's leave requests.</p>
                    </div>

                    {requests.length === 0 ? (
                        <div className="p-10 text-center text-sm text-slate-500">
                            No pending leave applications.
                        </div>
                    ) : (
                        <div className="divide-y divide-slate-200">
                            {requests.map((request) => (
                                <article key={request.id} className="space-y-4 p-5">
                                    <div className="flex flex-wrap items-start justify-between gap-3">
                                        <div>
                                            <h3 className="font-semibold text-slate-900">{request.employeeName}</h3>
                                            <p className="text-sm text-slate-500">
                                                {request.employeeCode} Ã‚Â· {request.departmentName}
                                            </p>
                                        </div>
                                        <span className="rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-800">
                                            Pending
                                        </span>
                                    </div>

                                    <div className="grid gap-3 text-sm sm:grid-cols-2 lg:grid-cols-4">
                                        <div>
                                            <p className="text-slate-500">Leave Type</p>
                                            <p className="font-medium text-slate-900">{request.leaveTypeName}</p>
                                        </div>
                                        <div>
                                            <p className="text-slate-500">Start Date</p>
                                            <p className="font-medium text-slate-900">{request.startDate}</p>
                                        </div>
                                        <div>
                                            <p className="text-slate-500">End Date</p>
                                            <p className="font-medium text-slate-900">{request.endDate}</p>
                                        </div>
                                        <div>
                                            <p className="text-slate-500">Total Days</p>
                                            <p className="font-medium text-slate-900">{request.totalDays}</p>
                                        </div>
                                    </div>

                                    <div>
                                        <p className="text-sm text-slate-500">Reason</p>
                                        <p className="mt-1 text-sm text-slate-800">{request.reason}</p>
                                    </div>

                                    <div>
                                        <label className="mb-1 block text-sm font-medium text-slate-700">
                                            Manager Remarks
                                        </label>
                                        <textarea
                                            value={remarks[request.id] || ""}
                                            onChange={(e) => setRemarks(previous => ({
                                                ...previous,
                                                [request.id]: e.target.value
                                            }))}
                                            maxLength={1000}
                                            rows={2}
                                            placeholder="Add remarks (optional)"
                                            className="w-full rounded-xl border border-slate-300 p-3 text-sm text-slate-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                                        />
                                    </div>

                                    <div className="flex flex-wrap gap-3">
                                        <button
                                            disabled={processing !== null}
                                            onClick={() => handleDecision(request.id, "APPROVED")}
                                            className="rounded-xl bg-green-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-green-700 disabled:opacity-50"
                                        >
                                            {processing === request.id ? "Processing..." : "Approve"}
                                        </button>

                                        <button
                                            disabled={processing !== null}
                                            onClick={() => handleDecision(request.id, "REJECTED")}
                                            className="rounded-xl bg-red-600 px-5 py-2.5 text-sm font-semibold text-white hover:bg-red-700 disabled:opacity-50"
                                        >
                                            {processing === request.id ? "Processing..." : "Reject"}
                                        </button>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}
                </section>

                <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                    <div className="border-b border-slate-200 p-5">
                        <h2 className="text-lg font-bold text-slate-900">Team Members</h2>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-left text-sm">
                            <thead className="bg-slate-50 text-slate-500">
                                <tr>
                                    <th className="px-5 py-3">Employee</th>
                                    <th className="px-5 py-3">Employee Code</th>
                                    <th className="px-5 py-3">Designation</th>
                                    <th className="px-5 py-3">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {dashboard?.teamMembers.map(member => (
                                    <tr key={member.employeeId}>
                                        <td className="px-5 py-4 font-medium text-slate-900">{member.employeeName}</td>
                                        <td className="px-5 py-4 text-slate-600">{member.employeeCode}</td>
                                        <td className="px-5 py-4 text-slate-600">{member.designation || "-"}</td>
                                        <td className="px-5 py-4">
                                            <span className={`rounded-full px-3 py-1 text-xs font-semibold ${
                                                member.active
                                                    ? "bg-green-100 text-green-700"
                                                    : "bg-slate-100 text-slate-600"
                                            }`}>
                                                {member.active ? "Active" : "Inactive"}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        {(!dashboard?.teamMembers || dashboard.teamMembers.length === 0) && (
                            <p className="p-8 text-center text-sm text-slate-500">No team members found.</p>
                        )}
                    </div>
                </section>
            </main>
        </div>
    );
}

