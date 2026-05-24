package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class Appointment implements java.io.Serializable {
    public int id;

    @SerializedName("patient_id")
    public int patientId;

    @SerializedName("doctor_id")
    public int doctorId;

    @SerializedName("appointment_datetime")
    public String appointmentDatetime;

    public String status;

    @SerializedName("created_at")
    public String createdAt;

    // Các trường bổ sung để hiển thị (Lấy từ JOIN trong DB)
    @SerializedName("doctor_name")
    public String doctorName;

    @SerializedName("doctor_title")
    public String doctorTitle;

    @SerializedName("doctor_bio")
    public String doctorBio;

    @SerializedName("specialty_name")
    public String specialtyName;

    @SerializedName("workplace_address")
    public String workplaceAddress;

    @SerializedName("consultation_fee")
    public double consultationFee;

    public String note;

    @SerializedName("patient_name")
    public String patientName;

    @SerializedName("patient_dob")
    public String patientDob;

    @SerializedName("patient_phone")
    public String patientPhone;

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, String appointmentDatetime, String status, String createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDatetime = appointmentDatetime;
        this.status = status;
        this.createdAt = createdAt;
    }
}
