import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
    getNotifications,
    markNotificationRead,
    markAllNotificationsRead,
    type Notification
} from "../services/notificationService";

type FilterType = "all" | "unread" | "read";

export default function Notifications() {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [filter, setFilter] = useState<FilterType>("all");

    const loadNotifications = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const data = await getNotifications();
            setNotifications(data);
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to load notifications.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadNotifications();
    }, [loadNotifications]);

    const unreadCount = notifications.filter(item => !item.read).length;
    const readCount = notifications.length - unreadCount;

    const filteredNotifications = useMemo(() => {
        if (filter === "unread") return notifications.filter(item => !item.read);
        if (filter === "read") return notifications.filter(item => item.read);
        return notifications;
    }, [notifications, filter]);

    async function handleMarkRead(id: number) {
        setSaving(true);
        setError("");

        try {
            await markNotificationRead(id);
            setNotifications(current =>
                current.map(item => item.id === id ? { ...item, read: true } : item)
            );
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update notification.");
        } finally {
            setSaving(false);
        }
    }

    async function handleMarkAllRead() {
        setSaving(true);
        setError("");

        try {
            await markAllNotificationsRead();
            setNotifications(current =>
                current.map(item => ({ ...item, read: true }))
            );
        } catch (err: any) {
            setError(err.response?.data?.message || "Unable to update notifications.");
        } finally {
            setSaving(false);
        }
    }

    function formatDate(value: string) {
        const date = new Date(value);

        if (Number.isNaN(date.getTime())) return value;

        return date.toLocaleString("en-IN", {
            day: "numeric",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function getTypeStyle(type: string) {
        const value = type?.toLowerCase() || "";

        if (value.includes("leave")) {
            return "bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-300";
        }

        if (value.includes("approval") || value.includes("approved")) {
            return "bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-300";
        }

        if (value.includes("reject")) {
            return "bg-red-100 text-red-700 dark:bg-red-500/15 dark:text-red-300";
        }

        return "bg-slate-100 text-slate-700 dark:bg-slate-700 dark:text-slate-200";
    }

    return (
        <div className="min-h-screen bg-slate-50 p-4 text-slate-900 dark:bg-slate-950 dark:text-slate-100 sm:p-6 lg:p-8">
            <div className="mx-auto max-w-6xl space-y-7">

                <header className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <p className="text-sm font-bold uppercase tracking-widest text-blue-600 dark:text-blue-400">
                            Updates Centre
                        </p>
                        <h1 className="mt-2 text-3xl font-bold tracking-tight sm:text-4xl">
                            Notifications
                        </h1>
                        <p className="mt-2 text-base text-slate-600 dark:text-slate-400 sm:text-lg">
                            Keep track of your leave requests and account activity.
                        </p>
                    </div>

                    <Link
                        to="/"
                        className="rounded-xl border border-slate-200 bg-white px-5 py-3 font-semibold text-slate-700 shadow-sm transition hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100 dark:hover:bg-slate-700"
                    >
                        ← Dashboard
                    </Link>
                </header>

                {error && (
                    <div className="flex items-start justify-between gap-3 rounded-xl border border-red-200 bg-red-50 p-4 text-red-700 dark:border-red-500/30 dark:bg-red-500/10 dark:text-red-300">
                        <p className="text-base">{error}</p>
                        <button onClick={() => setError("")} className="font-bold">
                            ×
                        </button>
                    </div>
                )}

                <section className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-800 dark:bg-slate-900">
                        <p className="text-sm font-semibold text-slate-500 dark:text-slate-400">
                            Total Notifications
                        </p>
                        <div className="mt-3 flex items-center justify-between">
                            <h2 className="text-3xl font-bold">{notifications.length}</h2>
                            <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-100 text-xl dark:bg-blue-500/15">
                                ♧
                            </span>
                        </div>
                    </div>

                    <div className="rounded-2xl border border-blue-200 bg-blue-50 p-5 shadow-sm dark:border-blue-500/30 dark:bg-blue-500/10">
                        <p className="text-sm font-semibold text-blue-700 dark:text-blue-300">
                            Unread Notifications
                        </p>
                        <div className="mt-3 flex items-center justify-between">
                            <h2 className="text-3xl font-bold text-blue-700 dark:text-blue-300">
                                {unreadCount}
                            </h2>
                            <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-100 text-xl dark:bg-blue-500/20">
                                ●
                            </span>
                        </div>
                    </div>

                    <div className="rounded-2xl border border-emerald-200 bg-emerald-50 p-5 shadow-sm dark:border-emerald-500/30 dark:bg-emerald-500/10">
                        <p className="text-sm font-semibold text-emerald-700 dark:text-emerald-300">
                            Read Notifications
                        </p>
                        <div className="mt-3 flex items-center justify-between">
                            <h2 className="text-3xl font-bold text-emerald-700 dark:text-emerald-300">
                                {readCount}
                            </h2>
                            <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-100 text-xl dark:bg-emerald-500/20">
                                ✓
                            </span>
                        </div>
                    </div>
                </section>

                <section className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm dark:border-slate-800 dark:bg-slate-900">
                    <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-200 p-5 dark:border-slate-800 sm:p-6">
                        <div>
                            <h2 className="text-xl font-bold sm:text-2xl">
                                Your Notifications
                            </h2>
                            <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
                                {unreadCount} unread out of {notifications.length} notifications
                            </p>
                        </div>

                        <div className="flex flex-wrap gap-2">
                            <button
                                onClick={loadNotifications}
                                disabled={loading}
                                className="rounded-xl border border-slate-200 px-4 py-2.5 font-semibold text-slate-700 transition hover:bg-slate-100 disabled:opacity-50 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800"
                            >
                                ↻ Refresh
                            </button>

                            <button
                                onClick={handleMarkAllRead}
                                disabled={saving || unreadCount === 0}
                                className="rounded-xl bg-blue-600 px-4 py-2.5 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-40"
                            >
                                {saving ? "Updating..." : "Mark All as Read"}
                            </button>
                        </div>
                    </div>

                    <div className="flex flex-wrap gap-2 border-b border-slate-200 px-5 py-4 dark:border-slate-800 sm:px-6">
                        {([
                            { value: "all", label: "All", count: notifications.length },
                            { value: "unread", label: "Unread", count: unreadCount },
                            { value: "read", label: "Read", count: readCount }
                        ] as const).map(tab => (
                            <button
                                key={tab.value}
                                onClick={() => setFilter(tab.value)}
                                className={`rounded-xl px-4 py-2.5 text-sm font-semibold transition ${
                                    filter === tab.value
                                        ? "bg-blue-600 text-white shadow-sm"
                                        : "bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700"
                                }`}
                            >
                                {tab.label}
                                <span className="ml-2 opacity-80">{tab.count}</span>
                            </button>
                        ))}
                    </div>

                    <div className="space-y-3 p-4 sm:p-6">
                        {loading ? (
                            <div className="py-16 text-center">
                                <div className="mx-auto h-9 w-9 animate-spin rounded-full border-4 border-blue-200 border-t-blue-600" />
                                <p className="mt-4 font-medium text-slate-500 dark:text-slate-400">
                                    Loading notifications...
                                </p>
                            </div>
                        ) : filteredNotifications.length === 0 ? (
                            <div className="rounded-2xl border border-dashed border-slate-300 px-5 py-16 text-center dark:border-slate-700">
                                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-slate-100 text-3xl dark:bg-slate-800">
                                    ✓
                                </div>
                                <h3 className="mt-5 text-xl font-bold">
                                    {notifications.length === 0
                                        ? "No notifications yet"
                                        : filter === "unread"
                                            ? "You're all caught up"
                                            : "No read notifications"}
                                </h3>
                                <p className="mt-2 text-base text-slate-500 dark:text-slate-400">
                                    {notifications.length === 0
                                        ? "New updates about your leave activity will appear here."
                                        : "Try selecting another filter to see your notifications."}
                                </p>
                                {filter !== "all" && (
                                    <button
                                        onClick={() => setFilter("all")}
                                        className="mt-5 rounded-xl bg-blue-600 px-5 py-2.5 font-semibold text-white hover:bg-blue-700"
                                    >
                                        View All Notifications
                                    </button>
                                )}
                            </div>
                        ) : (
                            filteredNotifications.map(item => (
                                <article
                                    key={item.id}
                                    className={`rounded-2xl border p-4 transition sm:p-5 ${
                                        item.read
                                            ? "border-slate-200 bg-white hover:border-slate-300 dark:border-slate-800 dark:bg-slate-900 dark:hover:border-slate-600"
                                            : "border-blue-200 bg-blue-50/70 shadow-sm dark:border-blue-500/30 dark:bg-blue-500/5"
                                    }`}
                                >
                                    <div className="flex items-start gap-3 sm:gap-4">
                                        <div className={`mt-1 flex h-11 w-11 shrink-0 items-center justify-center rounded-xl text-lg font-bold ${
                                            item.read
                                                ? "bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-300"
                                                : "bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-300"
                                        }`}>
                                            {item.read ? "✓" : "●"}
                                        </div>

                                        <div className="min-w-0 flex-1">
                                            <div className="flex flex-wrap items-center gap-2">
                                                <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 sm:text-lg">
                                                    {item.title}
                                                </h3>

                                                {!item.read && (
                                                    <span className="rounded-full bg-blue-100 px-2.5 py-1 text-xs font-bold text-blue-700 dark:bg-blue-500/20 dark:text-blue-300">
                                                        UNREAD
                                                    </span>
                                                )}
                                            </div>

                                            <p className="mt-2 whitespace-pre-wrap text-base leading-relaxed text-slate-700 dark:text-slate-300">
                                                {item.message}
                                            </p>

                                            <div className="mt-4 flex flex-wrap items-center justify-between gap-3">
                                                <div className="flex flex-wrap items-center gap-3">
                                                    <span className={`rounded-full px-3 py-1 text-xs font-bold ${getTypeStyle(item.type)}`}>
                                                        {item.type || "General"}
                                                    </span>
                                                    <span className="text-sm text-slate-500 dark:text-slate-400">
                                                        {formatDate(item.createdAt)}
                                                    </span>
                                                </div>

                                                {!item.read && (
                                                    <button
                                                        onClick={() => handleMarkRead(item.id)}
                                                        disabled={saving}
                                                        className="rounded-lg px-3 py-2 text-sm font-bold text-blue-700 transition hover:bg-blue-100 disabled:opacity-50 dark:text-blue-300 dark:hover:bg-blue-500/10"
                                                    >
                                                        Mark as read
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </article>
                            ))
                        )}
                    </div>

                    {!loading && notifications.length > 0 && (
                        <div className="border-t border-slate-200 px-5 py-4 text-center text-sm text-slate-500 dark:border-slate-800 dark:text-slate-400">
                            Showing {filteredNotifications.length} of {notifications.length} notifications
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}
