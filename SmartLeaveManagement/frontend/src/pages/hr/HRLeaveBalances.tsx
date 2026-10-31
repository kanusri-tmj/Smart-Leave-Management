import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
    getEmployees,
    getBalances,
    createBalance,
    updateAllocation,
    type EmployeeOption,
    type Balance
} from "../../services/hrBalanceService";
import { getLeaveTypes, type LeaveType } from "../../services/hrPolicyService";

export default function HRLeaveBalances() {
    const currentYear = new Date().getFullYear();

    const [employees, setEmployees] = useState<EmployeeOption[]>([]);
    const [leaveTypes, setLeaveTypes] = useState<LeaveType[]>([]);
    const [balances, setBalances] = useState<Balance[]>([]);
    const [year, setYear] = useState(currentYear);
    const [employeeId, setEmployeeId] = useState("");
    const [leaveTypeId, setLeaveTypeId] = useState("");
    const [allocatedDays, setAllocatedDays] = useState("");
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editDays, setEditDays] = useState("");
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    async function loadData(selectedYear = year) {
        setLoading(true);
        setError("");

        try {
            const [employeeData, typeData, balanceData] = await Promise.all([
                getEmployees(),
                getLeaveTypes(),
                getBalances(selectedYear)
            ]);

            setEmployees(employeeData);
            setLeaveTypes(typeData.filter(type => type.active));
            setBalances(balanceData);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to load leave balance data.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadData(year);
    }, [year]);

    const totals = useMemo(() => ({
        allocated: balances.reduce((sum, item) => sum + item.allocatedDays, 0),
        used: balances.reduce((sum, item) => sum + item.usedDays, 0),
        pending: balances.reduce((sum, item) => sum + item.pendingDays, 0),
        available: balances.reduce((sum, item) => sum + item.availableDays, 0)
    }), [balances]);

    async function handleCreate(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!employeeId || !leaveTypeId || allocatedDays === "") {
            setError("Please fill in all allocation fields.");
            return;
        }

        const days = Number(allocatedDays);

        if (!Number.isFinite(days) || days < 0) {
            setError("Allocated days must be zero or greater.");
            return;
        }

        setSaving(true);

        try {
            await createBalance({
                employeeId: Number(employeeId),
                leaveTypeId: Number(leaveTypeId),
                year,
                allocatedDays: days
            });

            setEmployeeId("");
            setLeaveTypeId("");
            setAllocatedDays("");
            setSuccess("Leave balance allocated successfully.");
            await loadData(year);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to create leave allocation.");
        } finally {
            setSaving(false);
        }
    }

    async function handleUpdate(id: number) {
        const days = Number(editDays);

        if (!Number.isFinite(days) || days < 0 || editDays.trim() === "") {
            setError("Enter a valid allocation of zero or greater.");
            return;
        }

        setSaving(true);
        setError("");
        setSuccess("");

        try {
            await updateAllocation(id, days);
            setEditingId(null);
            setEditDays("");
            setSuccess("Allocation updated successfully.");
            await loadData(year);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update allocation.");
        } finally {
            setSaving(false);
        }
    }

    const activeEmployees = employees.filter(employee =>
        employee.active && employee.role === "EMPLOYEE"
    );

    return (
        <div className="min-h-screen bg-slate-950 text-slate-100 p-4 md:p-8">
            <div className="max-w-7xl mx-auto space-y-6">
                <header className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <p className="text-sm text-blue-400 font-medium">HR MANAGEMENT</p>
                        <h1 className="text-3xl font-bold mt-1">Leave Balance Management</h1>
                        <p className="text-slate-400 mt-2">
                            Allocate and manage employee leave balances.
                        </p>
                    </div>

                    <div className="flex gap-3">
                        <Link to="/hr/dashboard"
                            className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700">
                            Dashboard
                        </Link>
                        <Link to="/hr/policies"
                            className="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-500">
                            Leave Policies
                        </Link>
                        <Link to="/hr/analytics"
                            className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700">
                            Analytics
                        </Link>
                    </div>
                </header>

                {error && (
                    <div className="rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-red-300">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="rounded-lg border border-green-500/30 bg-green-500/10 p-3 text-green-300">
                        {success}
                    </div>
                )}

                <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                    {[
                        ["Allocated Days", totals.allocated],
                        ["Used Days", totals.used],
                        ["Pending Days", totals.pending],
                        ["Available Days", totals.available]
                    ].map(([label, value]) => (
                        <div key={label} className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                            <p className="text-sm text-slate-400">{label}</p>
                            <p className="text-3xl font-bold mt-2">{value}</p>
                        </div>
                    ))}
                </section>

                <section className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                    <h2 className="text-xl font-semibold mb-4">Allocate Leave</h2>

                    <form onSubmit={handleCreate}
                        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <label className="block text-sm text-slate-300 mb-2">Employee</label>
                            <select value={employeeId}
                                onChange={event => setEmployeeId(event.target.value)}
                                className="w-full rounded-lg bg-slate-800 border border-slate-700 p-3"
                                required>
                                <option value="">Select employee</option>
                                {activeEmployees.map(employee => (
                                    <option key={employee.id} value={employee.id}>
                                        {employee.fullName} ({employee.employeeCode})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm text-slate-300 mb-2">Leave Type</label>
                            <select value={leaveTypeId}
                                onChange={event => setLeaveTypeId(event.target.value)}
                                className="w-full rounded-lg bg-slate-800 border border-slate-700 p-3"
                                required>
                                <option value="">Select leave type</option>
                                {leaveTypes.map(type => (
                                    <option key={type.id} value={type.id}>
                                        {type.name} ({type.code})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm text-slate-300 mb-2">Allocated Days</label>
                            <input type="number" min="0" step="0.5"
                                value={allocatedDays}
                                onChange={event => setAllocatedDays(event.target.value)}
                                placeholder="e.g. 12"
                                className="w-full rounded-lg bg-slate-800 border border-slate-700 p-3"
                                required />
                        </div>

                        <div className="flex items-end">
                            <button type="submit" disabled={saving}
                                className="w-full rounded-lg bg-blue-600 hover:bg-blue-500 disabled:opacity-50 p-3 font-medium">
                                {saving ? "Saving..." : "Allocate Balance"}
                            </button>
                        </div>
                    </form>

                    <p className="text-xs text-slate-500 mt-3">
                        Allocation year: {year}. An employee can have a separate balance for each leave type and year.
                    </p>
                </section>

                <section className="rounded-xl border border-slate-800 bg-slate-900 p-5">
                    <div className="flex flex-wrap justify-between items-center gap-3 mb-5">
                        <h2 className="text-xl font-semibold">Employee Leave Balances</h2>

                        <select value={year}
                            onChange={event => setYear(Number(event.target.value))}
                            className="rounded-lg bg-slate-800 border border-slate-700 p-2">
                            {[currentYear - 1, currentYear, currentYear + 1].map(item => (
                                <option key={item} value={item}>{item}</option>
                            ))}
                        </select>
                    </div>

                    {loading ? (
                        <p className="text-slate-400 py-8 text-center">Loading leave balances...</p>
                    ) : balances.length === 0 ? (
                        <p className="text-slate-400 py-8 text-center">
                            No leave balances found for {year}.
                        </p>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm text-left">
                                <thead className="text-slate-400 border-b border-slate-700">
                                    <tr>
                                        <th className="p-3">Employee</th>
                                        <th className="p-3">Leave Type</th>
                                        <th className="p-3">Allocated</th>
                                        <th className="p-3">Used</th>
                                        <th className="p-3">Pending</th>
                                        <th className="p-3">Available</th>
                                        <th className="p-3">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {balances.map(balance => (
                                        <tr key={balance.id} className="border-b border-slate-800">
                                            <td className="p-3">
                                                <p className="font-medium">{balance.employeeName}</p>
                                                <p className="text-xs text-slate-500">{balance.employeeCode}</p>
                                            </td>
                                            <td className="p-3">
                                                {balance.leaveTypeName}
                                                <p className="text-xs text-slate-500">{balance.leaveTypeCode}</p>
                                            </td>
                                            <td className="p-3">
                                                {editingId === balance.id ? (
                                                    <input type="number" min="0" step="0.5"
                                                        value={editDays}
                                                        onChange={event => setEditDays(event.target.value)}
                                                        className="w-20 rounded bg-slate-800 border border-slate-600 p-2" />
                                                ) : balance.allocatedDays}
                                            </td>
                                            <td className="p-3">{balance.usedDays}</td>
                                            <td className="p-3">{balance.pendingDays}</td>
                                            <td className="p-3 font-semibold text-green-400">
                                                {balance.availableDays}
                                            </td>
                                            <td className="p-3">
                                                {editingId === balance.id ? (
                                                    <div className="flex gap-2">
                                                        <button onClick={() => handleUpdate(balance.id)}
                                                            disabled={saving}
                                                            className="text-green-400 hover:text-green-300">
                                                            Save
                                                        </button>
                                                        <button onClick={() => setEditingId(null)}
                                                            className="text-slate-400 hover:text-white">
                                                            Cancel
                                                        </button>
                                                    </div>
                                                ) : (
                                                    <button
                                                        onClick={() => {
                                                            setEditingId(balance.id);
                                                            setEditDays(String(balance.allocatedDays));
                                                        }}
                                                        className="text-blue-400 hover:text-blue-300">
                                                        Edit
                                                    </button>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}

