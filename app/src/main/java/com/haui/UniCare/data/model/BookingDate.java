package com.haui.UniCare.data.model;

public class BookingDate {
    private String dayOfWeek;
    private String date;
    private String slotCount;
    private boolean isSelected;

    public BookingDate(String dayOfWeek, String date, String slotCount) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.slotCount = slotCount;
        this.isSelected = false;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public String getSlotCount() { return slotCount; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
