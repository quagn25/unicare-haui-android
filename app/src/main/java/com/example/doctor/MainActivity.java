package com.example.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDoctorSection();
        setupBottomNav();
    }

    private void setupDoctorSection() {
        RecyclerView rv = findViewById(R.id.rvHomeDoctors);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Doctor> list = DoctorRepository.getInstance().getHomeDoctors();
        rv.setAdapter(new HomeDoctorAdapter(this, list));

        ((ImageButton) findViewById(R.id.btnMoreDoctors)).setOnClickListener(v ->
                startActivity(new Intent(this, DoctorListActivity.class)));
    }

    private void setupBottomNav() {
        LinearLayout navAppointment = findViewById(R.id.navAppointment);
        if (navAppointment != null) {
            navAppointment.setOnClickListener(v ->
                    startActivity(new Intent(this, AppointmentActivity.class)));
        }
    }
}