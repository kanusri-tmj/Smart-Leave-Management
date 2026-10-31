import { useEffect, useState } from "react";

export default function ThemeToggle() {
    const [dark, setDark] = useState(() => {
        return localStorage.getItem("smartleave_theme") === "dark";
    });

    useEffect(() => {
        document.documentElement.classList.toggle("dark", dark);
        localStorage.setItem("smartleave_theme", dark ? "dark" : "light");
    }, [dark]);

    return (
        <button
            type="button"
            onClick={() => setDark(previous => !previous)}
            title={dark ? "Switch to light mode" : "Switch to dark mode"}
            aria-label={dark ? "Switch to light mode" : "Switch to dark mode"}
            style={{
                position: "fixed",
                right: "24px",
                bottom: "24px",
                zIndex: 9999,
                width: "52px",
                height: "52px",
                borderRadius: "50%",
                border: "1px solid var(--theme-border)",
                background: "var(--theme-card)",
                color: "var(--theme-text)",
                fontSize: "25px",
                cursor: "pointer",
                boxShadow: "0 4px 15px rgba(0,0,0,0.2)"
            }}
        >
            {dark ? "☀" : "☾"}
        </button>
    );
}
