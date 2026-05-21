package com.haui.UniCare.data.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private String type; // LICH_KHAM, TIEM_CHUNG, KET_QUA, UU_DAI, CAP_NHAT

    @SerializedName("is_read")
    private int isRead; // 0 = Chưa đọc, 1 = Đã đọc

    @SerializedName("created_at")
    private String createdAt;

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to check if read
    public boolean isRead() {
        return isRead == 1;
    }
}
