package com.haui.UniCare.data.model;

import com.google.gson.annotations.SerializedName;

public class PatientProfileResponse {
    public String status;
    public Profile profile;

    public static class Profile {
        @SerializedName("patientId")
        public int patientId;

        @SerializedName("user_id")
        public int userId;

        @SerializedName(value = "full_name", alternate = {"fullName"})
        public String fullName;

        public String dob;
        public String gender;
        
        @SerializedName(value = "phone", alternate = {"phoneNumber"})
        public String phone;
        public String address;
        public String email;
        public String username;
    }
}
