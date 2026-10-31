import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

interface ProtectedRouteProps {
    allowedRoles?: Array<"EMPLOYEE" | "MANAGER" | "HR">;
}

export default function ProtectedRoute({
    allowedRoles,
}: ProtectedRouteProps) {
    const { user, loading, isAuthenticated } = useAuth();

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <p className="text-gray-600">Loading...</p>
            </div>
        );
    }

    if (!isAuthenticated || !user) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <Navigate to="/unauthorized" replace />;
    }

    return <Outlet />;
}
