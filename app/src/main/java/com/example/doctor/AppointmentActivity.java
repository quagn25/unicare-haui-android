package com.example.doctor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());

        List<Appointment> list = AppointmentManager.getInstance().getAppointments();
        TextView tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rv  = findViewById(R.id.recyclerAppointments);

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new AppointmentAdapter(this, list));
        }
    }
}