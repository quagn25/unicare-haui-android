package com.example.doctor;

public class Appointment {
    private int doctorId;
    private String doctorName;
    private String specialty;
    private String date;
    private String timeSlot;
    private String address;
    private String fee;

    public Appointment(int doctorId, String doctorName, String specialty,
                       String date, String timeSlot, String address, String fee) {
        this.doctorId   = doctorId;
        this.doctorName = doctorName;
        this.specialty  = specialty;
        this.date       = date;
        this.timeSlot   = timeSlot;
        this.address    = address;
        this.fee        = fee;
    }

    public int getDoctorId()     { return doctorId; }
    public String getDoctorName(){ return doctorName; }
    public String getSpecialty() { return specialty; }
    public String getDate()      { return date; }
    public String getTimeSlot()  { return timeSlot; }
    public String getAddress()   { return address; }
    public String getFee()       { return fee; }
}