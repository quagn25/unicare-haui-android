package com.example.doctor;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {

    private static AppointmentManager instance;
    private List<Appointment> appointments = new ArrayList<>();

    private AppointmentManager() {}

    public static AppointmentManager getInstance() {
        if (instance == null) instance = new AppointmentManager();
        return instance;
    }

    public void addAppointment(Appointment appt) {
        appointments.add(appt);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }
}