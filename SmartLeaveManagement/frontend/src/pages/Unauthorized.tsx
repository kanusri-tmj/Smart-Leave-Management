import { Link } from "react-router-dom";

export default function Unauthorized() {
    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4">
            <div className="max-w-md rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
                <h1 className="text-3xl font-bold text-slate-900">Access Denied</h1>
                <p className="mt-3 text-sm text-slate-500">
                    Your account does not have permission to access this page.
                </p>
                <Link
                    to="/"
                    className="mt-6 inline-block rounded-xl bg-blue-600 px-5 py-3 text-sm font-semibold text-white hover:bg-blue-700"
                >
                    Return to Dashboard
                </Link>
            </div>
        </div>
    );
}
