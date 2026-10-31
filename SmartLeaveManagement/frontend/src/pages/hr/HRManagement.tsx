import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { registerUser } from "../../services/authService";
import {
    getHRUsers, updateUserRole, updateUserStatus,
    getDepartments, createDepartment, updateDepartment,
    getHREmployees, createEmployee,
    type HRUser, type Department, type Employee
} from "../../services/hrManagementService";

type Tab = "users" | "departments" | "onboarding";

export default function HRManagement() {
    const [tab, setTab] = useState<Tab>("users");
    const [users, setUsers] = useState<HRUser[]>([]);
    const [departments, setDepartments] = useState<Department[]>([]);
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [saving, setSaving] = useState(false);

    const [editingId, setEditingId] = useState<number | null>(null);
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");

    const [accountType, setAccountType] = useState<"existing" | "new">("existing");
    const [selectedUserId, setSelectedUserId] = useState("");
    const [newFullName, setNewFullName] = useState("");
    const [newEmail, setNewEmail] = useState("");
    const [newPassword, setNewPassword] = useState("");

    const [employeeCode, setEmployeeCode] = useState("");
    const [phone, setPhone] = useState("");
    const [designation, setDesignation] = useState("");
    const [departmentId, setDepartmentId] = useState("");
    const [managerId, setManagerId] = useState("");
    const [joiningDate, setJoiningDate] = useState(
        new Date().toISOString().slice(0, 10)
    );

    async function loadData() {
        setLoading(true);
        setError("");

        const results = await Promise.allSettled([
            getHRUsers(),
            getDepartments(),
            getHREmployees()
        ]);

        if (results[0].status === "fulfilled") {
            setUsers(results[0].value);
        }

        if (results[1].status === "fulfilled") {
            setDepartments(results[1].value);
        }

        if (results[2].status === "fulfilled") {
            setEmployees(results[2].value);
        }

        const errors = results
            .filter(result => result.status === "rejected");

        if (errors.length > 0) {
            setError("Some HR data could not load. Check the backend API and refresh.");
            console.error("HR data loading errors:", results);
        }

        setLoading(false);
    }

    useEffect(() => {
        loadData();
    }, []);

    const assignedUserIds = useMemo(
        () => new Set(employees.map(employee => employee.userId)),
        [employees]
    );

    const availableUsers = users.filter(
        user => user.role !== "HR" &&
            user.active &&
            !assignedUserIds.has(user.id)
    );

    const managers = employees.filter(
        employee => employee.role === "MANAGER" && employee.active
    );

    async function changeRole(id: number, role: HRUser["role"]) {
        setError("");
        setSuccess("");

        try {
            await updateUserRole(id, role);
            setSuccess("User role updated successfully.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update user role.");
        }
    }

    async function changeStatus(user: HRUser) {
        setError("");
        setSuccess("");

        try {
            await updateUserStatus(user.id, !user.active);
            setSuccess("User status updated successfully.");
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update user status.");
        }
    }

    function startEdit(department: Department) {
        setEditingId(department.id);
        setName(department.name);
        setDescription(department.description || "");
    }

    function resetDepartmentForm() {
        setEditingId(null);
        setName("");
        setDescription("");
    }

    async function saveDepartment(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setSaving(true);
        setError("");
        setSuccess("");

        try {
            if (editingId !== null) {
                await updateDepartment(editingId, name.trim(), description.trim());
                setSuccess("Department updated successfully.");
            } else {
                await createDepartment(name.trim(), description.trim());
                setSuccess("Department created successfully.");
            }

            resetDepartmentForm();
            await loadData();
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to save department.");
        } finally {
            setSaving(false);
        }
    }

    function resetOnboardingForm() {
        setSelectedUserId("");
        setNewFullName("");
        setNewEmail("");
        setNewPassword("");
        setEmployeeCode("");
        setPhone("");
        setDesignation("");
        setDepartmentId("");
        setManagerId("");
        setJoiningDate(new Date().toISOString().slice(0, 10));
    }

    async function handleOnboarding(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!departmentId) {
            setError("Please select a department.");
            return;
        }

        if (accountType === "existing" && !selectedUserId) {
            setError("Please select an existing user account.");
            return;
        }

        if (accountType === "new" && newPassword.length < 8) {
            setError("Password must contain at least 8 characters.");
            return;
        }

        setSaving(true);

        try {
            let userId: number;

            if (accountType === "new") {
                const account = await registerUser({
                    fullName: newFullName.trim(),
                    email: newEmail.trim(),
                    password: newPassword
                });

                userId = account.userId;
            } else {
                userId = Number(selectedUserId);
            }

            await createEmployee({
                userId,
                employeeCode: employeeCode.trim(),
                phone: phone.trim(),
                designation: designation.trim(),
                departmentId: Number(departmentId),
                managerId: managerId ? Number(managerId) : null,
                joiningDate
            });

            resetOnboardingForm();
            setSuccess(
                accountType === "new"
                    ? "New account created and employee profile assigned successfully."
                    : "Employee profile assigned successfully."
            );

            await loadData();
        } catch (err: any) {
            setError(
                err.response?.data?.message ||
                err.response?.data?.error ||
                "Unable to complete employee onboarding."
            );
            await loadData();
        } finally {
            setSaving(false);
        }
    }

    return (
        <div className="min-h-screen bg-slate-100">
            <header className="flex flex-wrap items-center justify-between gap-3 bg-white px-6 py-5 shadow-sm">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">HR Management</h1>
                    <p className="text-sm text-slate-500">
                        Manage users, departments and employee onboarding
                    </p>
                </div>

                <div className="flex gap-3">
                    <Link
                        to="/hr/dashboard"
                        className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
                    >
                        Dashboard
                    </Link>
                    <button
                        onClick={loadData}
                        className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-700"
                    >
                        Refresh
                    </button>
                </div>
            </header>

            <main className="mx-auto max-w-7xl space-y-6 p-6">
                <div className="flex flex-wrap gap-2">
                    {([
                        ["users", "User Management"],
                        ["onboarding", "Employee Onboarding"],
                        ["departments", "Departments"]
                    ] as const).map(([value, label]) => (
                        <button
                            key={value}
                            onClick={() => {
                                setTab(value);
                                setError("");
                                setSuccess("");
                            }}
                            className={`rounded-lg px-5 py-3 text-sm font-semibold ${
                                tab === value
                                    ? "bg-blue-700 text-white"
                                    : "bg-white text-slate-700"
                            }`}
                        >
                            {label}
                        </button>
                    ))}
                </div>

                {error && (
                    <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
                        {error}
                    </div>
                )}

                {success && (
                    <div className="rounded-lg border border-green-200 bg-green-50 p-4 text-green-700">
                        {success}
                    </div>
                )}

                {loading ? (
                    <p className="text-slate-600">Loading...</p>
                ) : (
                    <>
                        {tab === "users" && (
                            <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                                <div className="border-b p-5">
                                    <h2 className="text-lg font-bold text-slate-800">
                                        Users ({users.length})
                                    </h2>
                                </div>

                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-sm">
                                        <thead className="bg-slate-50 text-slate-600">
                                            <tr>
                                                <th className="p-4">Name</th>
                                                <th className="p-4">Email</th>
                                                <th className="p-4">Role</th>
                                                <th className="p-4">Status</th>
                                                <th className="p-4">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {users.map(user => (
                                                <tr key={user.id} className="border-t">
                                                    <td className="p-4 font-semibold text-slate-800">
                                                        {user.fullName}
                                                    </td>
                                                    <td className="p-4 text-slate-600">{user.email}</td>
                                                    <td className="p-4">
                                                        <select
                                                            value={user.role}
                                                            onChange={e =>
                                                                changeRole(
                                                                    user.id,
                                                                    e.target.value as HRUser["role"]
                                                                )
                                                            }
                                                            className="rounded-lg border border-slate-300 bg-white px-3 py-2"
                                                        >
                                                            <option value="EMPLOYEE">Employee</option>
                                                            <option value="MANAGER">Manager</option>
                                                            <option value="HR">HR</option>
                                                        </select>
                                                    </td>
                                                    <td className="p-4">
                                                        <span className={
                                                            user.active
                                                                ? "font-semibold text-green-700"
                                                                : "font-semibold text-red-600"
                                                        }>
                                                            {user.active ? "Active" : "Inactive"}
                                                        </span>
                                                    </td>
                                                    <td className="p-4">
                                                        <button
                                                            onClick={() => changeStatus(user)}
                                                            className="rounded-lg border border-slate-300 px-3 py-2 font-semibold hover:bg-slate-100"
                                                        >
                                                            {user.active ? "Deactivate" : "Activate"}
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>

                                    {users.length === 0 && (
                                        <p className="p-6 text-center text-slate-500">
                                            No users found.
                                        </p>
                                    )}
                                </div>
                            </section>
                        )}

                        {tab === "onboarding" && (
                            <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_340px]">
                                <form
                                    onSubmit={handleOnboarding}
                                    className="space-y-5 rounded-xl border border-slate-200 bg-white p-6 shadow-sm"
                                >
                                    <div>
                                        <h2 className="text-xl font-bold text-slate-800">
                                            Employee Onboarding
                                        </h2>
                                        <p className="mt-1 text-sm text-slate-500">
                                            Create a new account or assign an employee profile to an existing account.
                                        </p>
                                    </div>

                                    <div>
                                        <div>
    <label className="mb-2 block text-sm font-semibold text-slate-700">
        Account Type
    </label>

    <select
        value={accountType}
        onChange={e => {
            setAccountType(e.target.value as "existing" | "new");
            setError("");
        }}
        className="w-full rounded-lg border border-slate-300 bg-white px-3 py-3 text-slate-800 outline-none focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
    >
        <option value="existing">Existing Account</option>
        <option value="new">New Account</option>
    </select>
</div>
                                        <div className="flex flex-wrap gap-3">
                                            <button
                                                type="button"
                                                onClick={() => setAccountType("existing")}
                                                className={`rounded-lg border px-4 py-2 text-sm font-semibold ${
                                                    accountType === "existing"
                                                        ? "border-blue-700 bg-blue-700 text-white"
                                                        : "border-slate-300 text-slate-700"
                                                }`}
                                            >
                                                Existing Account
                                            </button>
                                            <button
                                                type="button"
                                                onClick={() => setAccountType("new")}
                                                className={`rounded-lg border px-4 py-2 text-sm font-semibold ${
                                                    accountType === "new"
                                                        ? "border-blue-700 bg-blue-700 text-white"
                                                        : "border-slate-300 text-slate-700"
                                                }`}
                                            >
                                                New Account
                                            </button>
                                        </div>
                                    </div>

                                    {accountType === "existing" ? (
                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Select Registered User
                                            </label>
                                            <select
                                                required
                                                value={selectedUserId}
                                                onChange={e => setSelectedUserId(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-3"
                                            >
                                                <option value="">Choose an account</option>
                                                {availableUsers.map(user => (
                                                    <option key={user.id} value={user.id}>
                                                        {user.fullName} ({user.email})
                                                    </option>
                                                ))}
                                            </select>
                                            <p className="mt-1 text-xs text-slate-500">
                                                Only active accounts without an employee profile are listed.
                                            </p>
                                            {availableUsers.length === 0 && (
                                                <p className="mt-2 text-sm text-amber-700">
                                                    No eligible accounts found. Register a new account or check User Management.
                                                </p>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="grid gap-4 sm:grid-cols-2">
                                            <div>
                                                <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                    Full Name
                                                </label>
                                                <input
                                                    required
                                                    value={newFullName}
                                                    onChange={e => setNewFullName(e.target.value)}
                                                    className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                    placeholder="Employee name"
                                                />
                                            </div>
                                            <div>
                                                <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                    Email
                                                </label>
                                                <input
                                                    required
                                                    type="email"
                                                    value={newEmail}
                                                    onChange={e => setNewEmail(e.target.value)}
                                                    className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                    placeholder="employee@example.com"
                                                />
                                            </div>
                                            <div className="sm:col-span-2">
                                                <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                    Temporary Password
                                                </label>
                                                <input
                                                    required
                                                    type="password"
                                                    minLength={8}
                                                    value={newPassword}
                                                    onChange={e => setNewPassword(e.target.value)}
                                                    className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                    placeholder="Minimum 8 characters"
                                                />
                                                <p className="mt-1 text-xs text-slate-500">
                                                    Share the temporary password securely with the employee.
                                                </p>
                                            </div>
                                        </div>
                                    )}

                                    <div className="grid gap-4 sm:grid-cols-2">
                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Employee Code
                                            </label>
                                            <input
                                                required
                                                value={employeeCode}
                                                onChange={e => setEmployeeCode(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                placeholder="e.g. EMP001"
                                            />
                                        </div>

                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Phone Number
                                            </label>
                                            <input
                                                required
                                                type="tel"
                                                value={phone}
                                                onChange={e => setPhone(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                placeholder="Phone number"
                                            />
                                        </div>

                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Designation
                                            </label>
                                            <input
                                                required
                                                value={designation}
                                                onChange={e => setDesignation(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                                placeholder="e.g. Software Engineer"
                                            />
                                        </div>

                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Department
                                            </label>
                                            <select
                                                required
                                                value={departmentId}
                                                onChange={e => setDepartmentId(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-3"
                                            >
                                                <option value="">Select department</option>
                                                {departments.map(department => (
                                                    <option key={department.id} value={department.id}>
                                                        {department.name}
                                                    </option>
                                                ))}
                                            </select>
                                        </div>

                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Reporting Manager
                                            </label>
                                            <select
                                                value={managerId}
                                                onChange={e => setManagerId(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-3"
                                            >
                                                <option value="">No manager assigned</option>
                                                {managers.map(manager => (
                                                    <option key={manager.id} value={manager.id}>
                                                        {manager.fullName} ({manager.employeeCode})
                                                    </option>
                                                ))}
                                            </select>
                                        </div>

                                        <div>
                                            <label className="mb-1 block text-sm font-semibold text-slate-700">
                                                Joining Date
                                            </label>
                                            <input
                                                required
                                                type="date"
                                                value={joiningDate}
                                                onChange={e => setJoiningDate(e.target.value)}
                                                className="w-full rounded-lg border border-slate-300 px-3 py-3"
                                            />
                                        </div>
                                    </div>

                                    <button
                                        type="submit"
                                        disabled={saving || departments.length === 0 ||
                                            (accountType === "existing" && availableUsers.length === 0)}
                                        className="w-full rounded-lg bg-blue-700 px-5 py-3 font-semibold text-white hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                        {saving ? "Processing..." : "Complete Employee Onboarding"}
                                    </button>
                                </form>

                                <aside className="h-fit space-y-4">
                                    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
                                        <h3 className="font-bold text-slate-800">Onboarding Summary</h3>
                                        <div className="mt-4 space-y-3">
                                            <div className="flex justify-between text-sm">
                                                <span className="text-slate-500">Total employees</span>
                                                <strong className="text-slate-800">{employees.length}</strong>
                                            </div>
                                            <div className="flex justify-between text-sm">
                                                <span className="text-slate-500">Unassigned accounts</span>
                                                <strong className="text-slate-800">{availableUsers.length}</strong>
                                            </div>
                                            <div className="flex justify-between text-sm">
                                                <span className="text-slate-500">Departments</span>
                                                <strong className="text-slate-800">{departments.length}</strong>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="rounded-xl border border-blue-100 bg-blue-50 p-5">
                                        <h3 className="font-bold text-blue-900">How it works</h3>
                                        <ol className="mt-3 list-inside list-decimal space-y-2 text-sm text-blue-800">
                                            <li>Select an existing account or create a new one.</li>
                                            <li>Enter the employee's official details.</li>
                                            <li>Assign a department and optional manager.</li>
                                            <li>Complete onboarding to create the employee profile.</li>
                                        </ol>
                                    </div>
                                </aside>
                            </div>
                        )}

                        {tab === "departments" && (
                            <div className="grid gap-6 lg:grid-cols-[360px_1fr]">
                                <form
                                    onSubmit={saveDepartment}
                                    className="h-fit space-y-4 rounded-xl border border-slate-200 bg-white p-5 shadow-sm"
                                >
                                    <h2 className="text-lg font-bold text-slate-800">
                                        {editingId ? "Edit Department" : "Add Department"}
                                    </h2>

                                    <div>
                                        <label className="mb-1 block text-sm font-medium text-slate-700">
                                            Department Name
                                        </label>
                                        <input
                                            required
                                            maxLength={100}
                                            value={name}
                                            onChange={e => setName(e.target.value)}
                                            className="w-full rounded-lg border border-slate-300 px-3 py-2"
                                            placeholder="e.g. Engineering"
                                        />
                                    </div>

                                    <div>
                                        <label className="mb-1 block text-sm font-medium text-slate-700">
                                            Description
                                        </label>
                                        <textarea
                                            maxLength={500}
                                            value={description}
                                            onChange={e => setDescription(e.target.value)}
                                            className="w-full rounded-lg border border-slate-300 px-3 py-2"
                                            rows={4}
                                            placeholder="Department description"
                                        />
                                    </div>

                                    <div className="flex gap-2">
                                        <button
                                            disabled={saving}
                                            className="rounded-lg bg-blue-700 px-4 py-2 font-semibold text-white disabled:opacity-50"
                                        >
                                            {saving ? "Saving..." : editingId ? "Update" : "Create"}
                                        </button>
                                        {editingId !== null && (
                                            <button
                                                type="button"
                                                onClick={resetDepartmentForm}
                                                className="rounded-lg border px-4 py-2"
                                            >
                                                Cancel
                                            </button>
                                        )}
                                    </div>
                                </form>

                                <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
                                    <div className="border-b p-5">
                                        <h2 className="text-lg font-bold text-slate-800">
                                            Departments ({departments.length})
                                        </h2>
                                    </div>

                                    <div className="overflow-x-auto">
                                        <table className="w-full text-left text-sm">
                                            <thead className="bg-slate-50 text-slate-600">
                                                <tr>
                                                    <th className="p-4">Name</th>
                                                    <th className="p-4">Description</th>
                                                    <th className="p-4">Action</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {departments.map(department => (
                                                    <tr key={department.id} className="border-t">
                                                        <td className="p-4 font-semibold text-slate-800">
                                                            {department.name}
                                                        </td>
                                                        <td className="p-4 text-slate-600">
                                                            {department.description || "—"}
                                                        </td>
                                                        <td className="p-4">
                                                            <button
                                                                onClick={() => startEdit(department)}
                                                                className="rounded-lg border border-slate-300 px-3 py-2 font-semibold hover:bg-slate-100"
                                                            >
                                                                Edit
                                                            </button>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>

                                        {departments.length === 0 && (
                                            <p className="p-6 text-center text-slate-500">
                                                No departments found.
                                            </p>
                                        )}
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
