import {
    createContext,
    useContext,
    useState,
    type ReactNode,
} from "react";

import {
    loginUser,
    type AuthUser,
} from "../services/authService";

interface AuthContextType {
    user: AuthUser | null;
    loading: boolean;
    isAuthenticated: boolean;
    login: (email: string, password: string) => Promise<AuthUser>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

function getStoredUser(): AuthUser | null {
    try {
        const stored = localStorage.getItem("smartleave_user");
        return stored ? JSON.parse(stored) as AuthUser : null;
    } catch {
        localStorage.removeItem("smartleave_user");
        localStorage.removeItem("smartleave_token");
        return null;
    }
}

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<AuthUser | null>(getStoredUser);

    const loading = false;

    async function login(email: string, password: string): Promise<AuthUser> {
        const response = await loginUser(email, password);

        const authenticatedUser: AuthUser = {
            userId: response.userId,
            fullName: response.fullName,
            email: response.email,
            role: response.role,
        };

        localStorage.setItem("smartleave_token", response.token);
        localStorage.setItem(
            "smartleave_user",
            JSON.stringify(authenticatedUser)
        );

        setUser(authenticatedUser);

        return authenticatedUser;
    }

    function logout() {
        localStorage.removeItem("smartleave_token");
        localStorage.removeItem("smartleave_user");
        setUser(null);
    }

    const isAuthenticated = Boolean(
        user && localStorage.getItem("smartleave_token")
    );

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                isAuthenticated,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }

    return context;
}