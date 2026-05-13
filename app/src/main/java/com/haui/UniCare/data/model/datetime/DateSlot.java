package com.haui.UniCare.data.model.datetime;

public class DateSlot {
    private String dayOfWeek; // T4, T5...
    private String date;      // 13, 14...
    private String fullDate;  // 2026-05-13 (Dùng để gửi lên API)
    private int availableSlots;
    private boolean isSelected;

    public DateSlot(String dayOfWeek, String date, String fullDate, int availableSlots, boolean isSelected) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.fullDate = fullDate;
        this.availableSlots = availableSlots;
        this.isSelected = isSelected;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}