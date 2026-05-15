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

    public Doctor(int id, String name, String degree, int experienceYears, String address, int avatarResource, String specialties) {
        this.id = id;
        this.name = name;
        this.degree = degree;
        this.experienceYears = experienceYears;
        this.address = address;
        this.avatarResource = avatarResource;
        this.specialties = specialties;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDegree() { return degree != null ? degree : ""; }
    public void setDegree(String degree) { this.degree = degree; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getAddress() { return address != null ? address : "Địa chỉ đang cập nhật"; }
    public void setAddress(String address) { this.address = address; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getAvatarResource() { return avatarResource; }
    public void setAvatarResource(int avatarResource) { this.avatarResource = avatarResource; }

    public String getSpecialties() { return specialties != null ? specialties : ""; }
    public void setSpecialties(String specialties) { this.specialties = specialties; }

    public String getBio() { return bio != null ? bio : "Thông tin đang cập nhật"; }
    public void setBio(String bio) { this.bio = bio; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getExperienceText() {
        return experienceYears + " năm kinh nghiệm";
    }
}
