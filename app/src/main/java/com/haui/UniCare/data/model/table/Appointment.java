package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class Appointment {
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
