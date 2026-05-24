package com.haui.UniCare.data.model;

public class PatientProfileUpdateRequest {
    public int userId;
    public String fullName;
    public String dob;
    public String gender;
    public String phone;
    public String address;
    public String email;

    public PatientProfileUpdateRequest(int userId, String fullName, String dob, String gender, String phone, String address, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }
}
