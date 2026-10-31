import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
    getLeaveAnalytics,
    type LeaveAnalytics
} from "../../services/hrAnalyticsService";

const currentYear = new Date().getFullYear();

const monthNames = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];

function BarList({
    items,
    nameKey,
    valueKey
}: {
    items: Record<string, any>[];
    nameKey: string;
    valueKey: string;
}) {
    const max = Math.max(1, ...items.map(item => Number(item[valueKey] || 0)));

    if (items.length === 0) {
        return <p className="text-slate-500 text-sm">No data available.</p>;
    }

    return (
        <div className="space-y-4">
            {items.map((item, index) => {
                const value = Number(item[valueKey] || 0);
                const width = (value / max) * 100;

                return (
                    <div key={index}>
                        <div className="flex justify-between gap-3 text-sm mb-2">
                            <span className="text-slate-300">{item[nameKey]}</span>
                            <span className="font-semibold">{value}</span>
                        </div>
                        <div className="h-2 rounded-full bg-slate-800 overflow-hidden">
                            <div
                                className="h-full rounded-full bg-blue-500"
                                style={{ width: `${width}%` }}
                            />
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

export default function HRAnalytics() {
    const [year, setYear] = useState(currentYear);
    const [data, setData] = useState<LeaveAnalytics | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let cancelled = false;

        async function load() {
            setLoading(true);
            setError("");

            try {
                const result = await getLeaveAnalytics(year);
                if (!cancelled) setData(result);
            } catch (err: any) {
                if (!cancelled) {
                    setError(
                        err.response?.data?.message ||
                        "Unable to load leave analytics."
                    );
                    setData(null);
                }
            } finally {
                if (!cancelled) setLoading(false);
            }
        }

        load();

        return () => {
            cancelled = true;
        };
    }, [year]);

    const monthlyData = (data?.monthlyTrends || [])
        .slice()
        .sort((a, b) => a.month - b.month)
        .map(item => ({
            name: monthNames[item.month - 1] || `Month ${item.month}`,
            value: item.approvedDays
        }));

    const statusColors: Record<string, string> = {
        PENDING: "text-yellow-400",
        APPROVED: "text-green-400",
        REJECTED: "text-red-400",
        CANCELLED: "text-slate-400"
    };

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100 p-4 md:p-8">
            <div className="max-w-7xl mx-auto space-y-6">
                <header className="flex flex-wrap justify-between items-center gap-4">
                    <div>
                        <p className="text-sm text-blue-400 font-medium">HR REPORTS</p>
                        <h1 className="text-3xl font-bold mt-1">Leave Analytics</h1>
                        <p className="text-slate-400 mt-2">
                            Organisation-wide leave usage and trends.
                        </p>
                    </div>

                    <div className="flex flex-wrap gap-3">
                        <Link to="/hr/dashboard"
                            className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700">
                            Dashboard
                        </Link>
                        <Link to="/hr/leave-balances"
                            className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700">
                            Leave Balances
                        </Link>
                        <select
                            value={year}
                            onChange={event => setYear(Number(event.target.value))}
                            className="px-3 py-2 rounded-lg bg-slate-800 border border-slate-700">
                            {[currentYear - 1, currentYear, currentYear + 1].map(item => (
                                <option key={item} value={item}>{item}</option>
                            ))}
                        </select>
                    </div>
                </header>

                {error && (
                    <div className="p-4 rounded-lg border border-red-500/30 bg-red-500/10 text-red-300">
                        {error}
                    </div>
                )}

                {loading ? (
                    <div className="text-center text-slate-400 py-16">
                        Loading analytics...
                    </div>
                ) : data ? (
                    <>
                        <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
                            {[
                                ["Total Requests", data.totalRequests],
                                ["Pending", data.statusCounts.PENDING || 0],
                                ["Approved", data.statusCounts.APPROVED || 0],
                                ["Rejected", data.statusCounts.REJECTED || 0],
                                ["Cancelled", data.statusCounts.CANCELLED || 0]
                            ].map(([label, value]) => (
                                <div key={label} className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                                    <p className="text-sm text-slate-400">{label}</p>
                                    <p className="text-3xl font-bold mt-2">{value}</p>
                                </div>
                            ))}
                        </section>

                        <section className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                                <h2 className="text-lg font-semibold mb-5">
                                    Monthly Approved Leave Days
                                </h2>

                                {monthlyData.length === 0 ? (
                                    <p className="text-slate-500 text-sm">No monthly data available.</p>
                                ) : (
                                    <div className="flex items-end gap-2 h-56">
                                        {monthNames.map((month) => {
                                            const value = monthlyData.find(item =>
                                                item.name === month
                                            )?.value || 0;
                                            const max = Math.max(
                                                1,
                                                ...monthlyData.map(item => item.value)
                                            );
                                            const height = value === 0 ? 0 : Math.max(4, (value / max) * 100);

                                            return (
                                                <div key={month}
                                                    className="flex-1 h-full flex flex-col justify-end items-center gap-2">
                                                    <span className="text-xs text-slate-400">
                                                        {value || ""}
                                                    </span>
                                                    <div className="w-full flex items-end justify-center h-full">
                                                        <div
                                                            className="w-full max-w-8 rounded-t bg-blue-500"
                                                            style={{ height: `${height}%` }}
                                                            title={`${month}: ${value} approved days`}
                                                        />
                                                    </div>
                                                    <span className="text-xs text-slate-500">{month}</span>
                                                </div>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>

                            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                                <h2 className="text-lg font-semibold mb-5">
                                    Leave Usage by Department
                                </h2>
                                <BarList
                                    items={data.departmentUsage as unknown as Record<string, any>[]}
                                    nameKey="department"
                                    valueKey="approvedDays"
                                />
                            </div>

                            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                                <h2 className="text-lg font-semibold mb-5">
                                    Leave Usage by Leave Type
                                </h2>
                                <BarList
                                    items={data.leaveTypeUsage as unknown as Record<string, any>[]}
                                    nameKey="leaveType"
                                    valueKey="approvedDays"
                                />
                            </div>

                            <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                                <h2 className="text-lg font-semibold mb-5">
                                    Request Status Breakdown
                                </h2>

                                <div className="space-y-4">
                                    {Object.entries(data.statusCounts).map(([status, count]) => (
                                        <div key={status}
                                            className="flex justify-between items-center border-b border-slate-800 pb-3">
                                            <span className={statusColors[status] || "text-slate-300"}>
                                                {status}
                                            </span>
                                            <span className="font-semibold">{count}</span>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </section>

                        <section className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                            <h2 className="text-lg font-semibold mb-4">
                                Employee Absence Report
                            </h2>

                            {data.employeeAbsences.length === 0 ? (
                                <p className="text-slate-500 text-sm">No employee absence data available.</p>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-sm text-left">
                                        <thead className="text-slate-400 border-b border-slate-700">
                                            <tr>
                                                <th className="p-3">Employee</th>
                                                <th className="p-3">Employee Code</th>
                                                <th className="p-3">Department</th>
                                                <th className="p-3">Approved Leave Days</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {data.employeeAbsences
                                                .slice()
                                                .sort((a, b) => b.approvedDays - a.approvedDays)
                                                .map((employee, index) => (
                                                    <tr key={`${employee.employeeCode}-${index}`}
                                                        className="border-b border-slate-800">
                                                        <td className="p-3 font-medium">{employee.employeeName}</td>
                                                        <td className="p-3 text-slate-400">{employee.employeeCode}</td>
                                                        <td className="p-3">{employee.department}</td>
                                                        <td className="p-3 font-semibold text-blue-400">
                                                            {employee.approvedDays}
                                                        </td>
                                                    </tr>
                                                ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </section>
                    </>
                ) : (
                    <div className="rounded-xl border border-slate-800 bg-slate-900 p-8 text-center text-slate-400">
                        No analytics data available for {year}.
                    </div>
                )}
            </div>
        </div>
    );
}

