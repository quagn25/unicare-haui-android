package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class Notification {
    public int id;

    @SerializedName("user_id")
    public int userId;

    public String title;
    public String message;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("is_read")
    public boolean isRead;

    public Notification() {}

    public Notification(int id, int userId, String title, String message, String createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }
}
