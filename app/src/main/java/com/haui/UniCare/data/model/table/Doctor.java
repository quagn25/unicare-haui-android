package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class Doctor {
    public int id;
    
    @SerializedName("user_id")
    public int userId;
    
    @SerializedName("specialty_id")
    public Integer specialtyId;
    
    public String name;
    public String phone;
    public String education;

    public Doctor() {}

    public Doctor(int id, int userId, Integer specialtyId, String name, String phone, String education) {
        this.id = id;
        this.userId = userId;
        this.specialtyId = specialtyId;
        this.name = name;
        this.phone = phone;
        this.education = education;
    }
}
