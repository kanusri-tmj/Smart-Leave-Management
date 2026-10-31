import api from "./api";

export interface Notification {
    id: number;
    title: string;
    message: string;
    type: string;
    read: boolean;
    createdAt: string;
}

export async function getNotifications() {
    const response = await api.get<Notification[]>("/notifications");
    return response.data;
}

export async function getUnreadCount() {
    const response = await api.get<number>("/notifications/unread-count");
    return response.data;
}

export async function markNotificationRead(id: number) {
    await api.patch(`/notifications/${id}/read`);
}

export async function markAllNotificationsRead() {
    await api.patch("/notifications/read-all");
}
