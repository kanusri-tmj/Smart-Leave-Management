import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
    getLeaveTypes, saveLeaveType, setLeaveTypeStatus,
    getHolidays, saveHoliday, deleteHoliday,
    type LeaveType, type Holiday
} from "../../services/hrPolicyService";

type Tab = "leave-types" | "holidays";

const emptyLeaveType = {
    name: "",
    code: "",
    description: "",
    annualQuota: 0,
    carryForward: false
};

const emptyHoliday = {
    date: "",
    name: "",
    description: ""
};

export default function HRPolicies() {
    const [tab, setTab] = useState<Tab>("leave-types");
    const [leaveTypes, setLeaveTypes] = useState<LeaveType[]>([]);
    const [holidays, setHolidays] = useState<Holiday[]>([]);
    const [leaveForm, setLeaveForm] = useState(emptyLeaveType);
    const [holidayForm, setHolidayForm] = useState(emptyHoliday);
    const [leaveEditingId, setLeaveEditingId] = useState<number | null>(null);
    const [holidayEditingId, setHolidayEditingId] = useState<number | null>(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    async function loadData() {
        setLoading(true);
        setError("");
        try {
            const [types, holidayList] = await Promise.all([
                getLeaveTypes(),
                getHolidays()
            ]);
            setLeaveTypes(types);
            setHolidays(holidayList);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to load HR policy data.");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadData();
    }, []);

    function editLeaveType(item: LeaveType) {
        setLeaveEditingId(item.id);
        setLeaveForm({
            name: item.name,
            code: item.code,
            description: item.description || "",
            annualQuota: item.annualQuota,
            carryForward: item.carryForward
        });
        setTab("leave-types");
    }

    async function submitLeaveType(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSaving(true);
        setError("");
        setMessage("");
        try {
            await saveLeaveType(leaveEditingId, leaveForm);
            setLeaveForm(emptyLeaveType);
            setLeaveEditingId(null);
            setMessage("Leave type saved successfully.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to save leave type.");
        } finally {
            setSaving(false);
        }
    }

    async function toggleLeaveType(item: LeaveType) {
        setError("");
        try {
            await setLeaveTypeStatus(item.id, !item.active);
            setMessage("Leave type status updated.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update leave type status.");
        }
    }

    function editHoliday(item: Holiday) {
        setHolidayEditingId(item.id);
        setHolidayForm({
            date: item.date,
            name: item.name,
            description: item.description || ""
        });
        setTab("holidays");
    }

    async function submitHoliday(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSaving(true);
        setError("");
        setMessage("");
        try {
            await saveHoliday(holidayEditingId, holidayForm);
            setHolidayForm(emptyHoliday);
            setHolidayEditingId(null);
            setMessage("Holiday saved successfully.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to save holiday.");
        } finally {
            setSaving(false);
        }
    }

    async function removeHoliday(id: number) {
        if (!window.confirm("Delete this holiday?")) return;
        setError("");
        try {
            await deleteHoliday(id);
            setMessage("Holiday deleted.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to delete holiday.");
        }
    }

    return (
        <div className="min-h-screen bg-slate-100">
            <header className="flex flex-wrap items-center justify-between gap-3 bg-white px-6 py-5 shadow-sm">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Leave Types & Holidays</h1>
                    <p className="text-sm text-slate-500">Configure leave policies and company holidays</p>
                </div>
                <div className="flex gap-3">
                    <Link to="/hr/dashboard" className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700">Dashboard</Link>
                    <Link to="/hr/management" className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700">HR Management</Link>
                    <button onClick={loadData} className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Refresh</button>
                </div>
            </header>

            <main className="mx-auto max-w-7xl space-y-6 p-6">
                <div className="flex flex-wrap gap-2">
                    <button onClick={() => setTab("leave-types")} className={`rounded-lg px-5 py-3 text-sm font-semibold ${tab === "leave-types" ? "bg-blue-700 text-white" : "bg-white text-slate-700"}`}>Leave Types</button>
                    <button onClick={() => setTab("holidays")} className={`rounded-lg px-5 py-3 text-sm font-semibold ${tab === "holidays" ? "bg-blue-700 text-white" : "bg-white text-slate-700"}`}>Holidays</button>
                </div>

                {error && <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">{error}</div>}
                {message && <div className="rounded-lg border border-green-200 bg-green-50 p-4 text-green-700">{message}</div>}

                {loading ? <p className="text-slate-600">Loading...</p> : (
                    <>
                        {tab === "leave-types" && (
                            <div className="grid gap-6 lg:grid-cols-[350px_1fr]">
                                <form onSubmit={submitLeaveType} className="h-fit space-y-4 rounded-xl border bg-white p-5 shadow-sm">
                                    <h2 className="text-lg font-bold text-slate-800">{leaveEditingId ? "Edit Leave Type" : "Add Leave Type"}</h2>
                                    <input required maxLength={50} placeholder="Leave name" value={leaveForm.name} onChange={e => setLeaveForm({...leaveForm, name: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <input required maxLength={20} placeholder="Code (e.g. CL)" value={leaveForm.code} onChange={e => setLeaveForm({...leaveForm, code: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <textarea maxLength={500} placeholder="Description" rows={3} value={leaveForm.description} onChange={e => setLeaveForm({...leaveForm, description: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <div>
                                        <label className="mb-1 block text-sm text-slate-600">Annual quota (days)</label>
                                        <input type="number" min="0" required value={leaveForm.annualQuota} onChange={e => setLeaveForm({...leaveForm, annualQuota: Number(e.target.value)})} className="w-full rounded-lg border p-3" />
                                    </div>
                                    <label className="flex items-center gap-2 text-sm text-slate-700">
                                        <input type="checkbox" checked={leaveForm.carryForward} onChange={e => setLeaveForm({...leaveForm, carryForward: e.target.checked})} />
                                        Allow carry forward
                                    </label>
                                    <div className="flex gap-2">
                                        <button disabled={saving} className="rounded-lg bg-blue-700 px-4 py-2 font-semibold text-white disabled:opacity-50">{saving ? "Saving..." : leaveEditingId ? "Update" : "Create"}</button>
                                        {leaveEditingId !== null && <button type="button" onClick={() => {setLeaveEditingId(null); setLeaveForm(emptyLeaveType);}} className="rounded-lg border px-4 py-2">Cancel</button>}
                                    </div>
                                </form>

                                <section className="overflow-hidden rounded-xl border bg-white shadow-sm">
                                    <div className="border-b p-5"><h2 className="text-lg font-bold text-slate-800">Leave Types ({leaveTypes.length})</h2></div>
                                    <div className="overflow-x-auto">
                                        <table className="w-full text-left text-sm">
                                            <thead className="bg-slate-50 text-slate-600"><tr><th className="p-4">Name</th><th className="p-4">Code</th><th className="p-4">Quota</th><th className="p-4">Carry Forward</th><th className="p-4">Status</th><th className="p-4">Actions</th></tr></thead>
                                            <tbody>
                                                {leaveTypes.map(item => (
                                                    <tr key={item.id} className="border-t">
                                                        <td className="p-4 font-semibold">{item.name}<p className="text-xs font-normal text-slate-500">{item.description}</p></td>
                                                        <td className="p-4">{item.code}</td>
                                                        <td className="p-4">{item.annualQuota} days</td>
                                                        <td className="p-4">{item.carryForward ? "Yes" : "No"}</td>
                                                        <td className="p-4">{item.active ? "Active" : "Inactive"}</td>
                                                        <td className="space-x-2 p-4">
                                                            <button onClick={() => editLeaveType(item)} className="font-semibold text-blue-700">Edit</button>
                                                            <button onClick={() => toggleLeaveType(item)} className="font-semibold text-slate-700">{item.active ? "Deactivate" : "Activate"}</button>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                        {leaveTypes.length === 0 && <p className="p-6 text-center text-slate-500">No leave types found.</p>}
                                    </div>
                                </section>
                            </div>
                        )}

                        {tab === "holidays" && (
                            <div className="grid gap-6 lg:grid-cols-[350px_1fr]">
                                <form onSubmit={submitHoliday} className="h-fit space-y-4 rounded-xl border bg-white p-5 shadow-sm">
                                    <h2 className="text-lg font-bold text-slate-800">{holidayEditingId ? "Edit Holiday" : "Add Holiday"}</h2>
                                    <input required type="date" value={holidayForm.date} onChange={e => setHolidayForm({...holidayForm, date: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <input required maxLength={150} placeholder="Holiday name" value={holidayForm.name} onChange={e => setHolidayForm({...holidayForm, name: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <textarea maxLength={300} rows={3} placeholder="Description" value={holidayForm.description} onChange={e => setHolidayForm({...holidayForm, description: e.target.value})} className="w-full rounded-lg border p-3" />
                                    <div className="flex gap-2">
                                        <button disabled={saving} className="rounded-lg bg-blue-700 px-4 py-2 font-semibold text-white disabled:opacity-50">{saving ? "Saving..." : holidayEditingId ? "Update" : "Create"}</button>
                                        {holidayEditingId !== null && <button type="button" onClick={() => {setHolidayEditingId(null); setHolidayForm(emptyHoliday);}} className="rounded-lg border px-4 py-2">Cancel</button>}
                                    </div>
                                </form>

                                <section className="overflow-hidden rounded-xl border bg-white shadow-sm">
                                    <div className="border-b p-5"><h2 className="text-lg font-bold text-slate-800">Holidays ({holidays.length})</h2></div>
                                    <div className="overflow-x-auto">
                                        <table className="w-full text-left text-sm">
                                            <thead className="bg-slate-50 text-slate-600"><tr><th className="p-4">Date</th><th className="p-4">Holiday</th><th className="p-4">Description</th><th className="p-4">Actions</th></tr></thead>
                                            <tbody>
                                                {[...holidays].sort((a,b) => a.date.localeCompare(b.date)).map(item => (
                                                    <tr key={item.id} className="border-t">
                                                        <td className="p-4">{item.date}</td>
                                                        <td className="p-4 font-semibold">{item.name}</td>
                                                        <td className="p-4 text-slate-600">{item.description || "—"}</td>
                                                        <td className="space-x-3 p-4">
                                                            <button onClick={() => editHoliday(item)} className="font-semibold text-blue-700">Edit</button>
                                                            <button onClick={() => removeHoliday(item.id)} className="font-semibold text-red-600">Delete</button>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                        {holidays.length === 0 && <p className="p-6 text-center text-slate-500">No holidays found.</p>}
                                    </div>
                                </section>
                            </div>
                        )}
                    </>
                )}
            </main>
        </div>
    );
}
