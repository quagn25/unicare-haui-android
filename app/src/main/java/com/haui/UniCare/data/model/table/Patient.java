package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class Patient {
    public int id;

    @SerializedName("user_id")
    public int userId;

    @SerializedName("full_name")
    public String fullName;

    public String dob; // DATE in SQL, usually String "yyyy-MM-dd" in JSON
    public String gender;
    public String phone;
    public String address;

    @SerializedName("medical_history")
    public String medicalHistory;

    public Patient() {}

    public Patient(int id, int userId, String fullName, String dob, String gender, String phone, String address, String medicalHistory) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }
}
