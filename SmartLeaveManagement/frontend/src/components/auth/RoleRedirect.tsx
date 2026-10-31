import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function RoleRedirect() {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    switch (user.role) {
        case "HR":
            return <Navigate to="/hr/dashboard" replace />;

        case "MANAGER":
            return <Navigate to="/manager/dashboard" replace />;

        case "EMPLOYEE":
            return <Navigate to="/employee/dashboard" replace />;

        default:
            return <Navigate to="/unauthorized" replace />;
    }
}
