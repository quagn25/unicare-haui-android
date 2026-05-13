package com.haui.UniCare.data.model;

public class TimeSlot {
    private String timeRange;
    private boolean isSelected;

    public TimeSlot(String timeRange) {
        this.timeRange = timeRange;
        this.isSelected = false;
    }

    public String getTimeRange() { return timeRange; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
