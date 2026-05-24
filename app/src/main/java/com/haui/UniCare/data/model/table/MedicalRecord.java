package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class MedicalRecord implements java.io.Serializable {
    public int id;

    @SerializedName("patient_id")
    public int patientId;

    @SerializedName("doctor_id")
    public int doctorId;

    @SerializedName("appointment_id")
    public Integer appointmentId;

    @SerializedName("visit_date")
    public String visitDate;

    public String diagnosis;
    public String prescription;

    @SerializedName("doctor_notes")
    public String doctorNotes;

    public MedicalRecord() {}

    public MedicalRecord(int id, int patientId, int doctorId, Integer appointmentId, String visitDate, String diagnosis, String prescription, String doctorNotes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.visitDate = visitDate;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.doctorNotes = doctorNotes;
    }
}
