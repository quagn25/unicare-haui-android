package com.haui.UniCare.data.model;

import java.io.Serializable;

public class VaccineType implements Serializable {
    private int dbDoctorId;
    private String name;
    private String doseInfo;
    private double price;
    private String note;

    public VaccineType(int dbDoctorId, String name, String doseInfo, double price, String note) {
        this.dbDoctorId = dbDoctorId;
        this.name = name;
        this.doseInfo = doseInfo;
        this.price = price;
        this.note = note;
    }

    public int getDbDoctorId() { return dbDoctorId; }
    public String getName() { return name; }
    public String getDoseInfo() { return doseInfo; }
    public double getPrice() { return price; }
    public String getNote() { return note; }
}
