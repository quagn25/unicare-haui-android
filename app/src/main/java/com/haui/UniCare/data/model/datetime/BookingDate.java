package com.haui.UniCare.data.model.datetime;

import java.io.Serializable;

// Trong file BookingDate.java
public class BookingDate implements Serializable {
    private String day;
    private String date;
    private String subTitle;

    public BookingDate(String day, String date, String subTitle) {
        this.day = day;
        this.date = date;
        this.subTitle = subTitle;
    }

    public String getDay() { return day; } // Hoặc getDayLabel()
    public String getDate() { return date; } // Hoặc getDateLabel()
}
