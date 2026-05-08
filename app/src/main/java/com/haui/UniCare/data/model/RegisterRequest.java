package com.haui.UniCare.data.model;

public class RegisterRequest {
    public String username;
    public String password;
    public String role;
    public String fullName;
    public String dob;
    public String gender;
    public String phone; // Có thể lấy email hoặc phone tùy bạn thiết kế

    public RegisterRequest(String username, String password, String role, String fullName, String dob, String gender, String phone) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
    }
}