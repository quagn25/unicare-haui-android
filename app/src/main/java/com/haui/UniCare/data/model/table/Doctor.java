package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Doctor implements Serializable {
    private int id;
    private String name;

    @SerializedName("title")
    private String degree;             // Khớp với cột 'title' (ThS. BS, TS. BS...)

    @SerializedName("experience_years")
    private int experienceYears;       // Khớp với cột 'experience_years'

    @SerializedName("workplace_address")
    private String address;            // Khớp với cột 'workplace_address'

    @SerializedName("avatar_url")
    private String avatarUrl;          // Khớp với cột 'avatar_url'

    @SerializedName("specialties")
    private String specialties;        // Tên chuyên khoa (thường trả về chuỗi sau khi JOIN)

    @SerializedName("bio")
    private String bio;                // Giới thiệu chi tiết

    @SerializedName("consultation_fee")
    private double consultationFee;    // Phí khám

    private int avatarResource;        // Dùng cho dữ liệu mẫu local

    public Doctor() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDegree() { return degree != null ? degree : ""; }
    public String getSpecialties() { return specialties != null ? specialties : ""; }
    public String getAddress() { return address != null ? address : "Địa chỉ đang cập nhật"; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getAvatarResource() { return avatarResource; }
    public String getBio() { return bio != null ? bio : "Thông tin đang cập nhật"; }
    public double getConsultationFee() { return consultationFee; }

    public String getExperienceText() {
        return experienceYears + " năm kinh nghiệm";
    }
}
