package com.haui.UniCare.data.model.datetime;

public class TimeSlot {
    private String timeRange; // 17:30 - 17:40
    private boolean isAvailable;
    private boolean isSelected;

    public TimeSlot(String timeRange, boolean isAvailable, boolean isSelected) {
        this.timeRange = timeRange;
        this.isAvailable = isAvailable;
        this.isSelected = isSelected;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
