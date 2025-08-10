package com.example.canpay_passenger;

public class NotificationItem {
    public String title;
    public String date;
    public int iconResId;
    public boolean isUnread;

    public NotificationItem(String title, String date, int iconResId, boolean isUnread) {
        this.title = title;
        this.date = date;
        this.iconResId = iconResId;
        this.isUnread = isUnread;
    }

    public NotificationItem(String title, String date, boolean isUnread) {
        this.title = title;
        this.date = date;
        this.isUnread = isUnread;
    }
}
