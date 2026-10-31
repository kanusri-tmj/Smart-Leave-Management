import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { CalendarDays } from "lucide-react";
import { registerUser } from "../../services/authService";

export default function Register() {
    const navigate = useNavigate();

    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError("");

        if (password.length < 8) {
            setError("Password must contain at least 8 characters.");
            return;
        }

        if (password !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        setLoading(true);

        try {
            await registerUser({ fullName, email, password });
            navigate("/login", {
                replace: true,
                state: { message: "Registration successful. Please sign in." },
            });
        } catch (err: any) {
            setError(
                err.response?.data?.message ||
                err.response?.data?.error ||
                "Registration failed. Please check your details."
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-10">
            <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-8 shadow-xl">
                <div className="mb-7 text-center">
                    <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-600 text-white">
                        <CalendarDays size={28} />
                    </div>
                    <h1 className="mt-4 text-2xl font-bold text-slate-900">
                        Create Account
                    </h1>
                    <p className="mt-2 text-sm text-slate-500">
                        Register as an employee
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Full Name
                        </label>
                        <input
                            value={fullName}
                            onChange={e => setFullName(e.target.value)}
                            placeholder="Enter your full name"
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-blue-500"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Email Address
                        </label>
                        <input
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            placeholder="Enter your email"
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-blue-500"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Password
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            placeholder="Minimum 8 characters"
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-blue-500"
                            minLength={8}
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Confirm Password
                        </label>
                        <input
                            type="password"
                            value={confirmPassword}
                            onChange={e => setConfirmPassword(e.target.value)}
                            placeholder="Re-enter your password"
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 text-sm outline-none focus:border-blue-500"
                            required
                        />
                    </div>

                    {error && (
                        <p className="rounded-lg bg-rose-50 p-3 text-sm text-rose-700">
                            {error}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full rounded-xl bg-blue-600 py-3 font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
                    >
                        {loading ? "Creating account..." : "Create Account"}
                    </button>
                </form>

                <p className="mt-6 text-center text-sm text-slate-500">
                    Already registered?{" "}
                    <Link to="/login" className="font-semibold text-blue-600 hover:underline">
                        Sign In
                    </Link>
                </p>
            </div>
        </div>
    );
}
