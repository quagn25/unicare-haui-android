package com.haui.UniCare.feature.patients.doctor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.appointment.viewmodel.BookAppointmentActivity;

import java.text.DecimalFormat;

public class DoctorDetailActivity extends AppCompatActivity {

    private ImageView imgDoctorDetail;
    private TextView tvDegreeDetail, tvNameDetail, tvSpecialtyDetail, tvExpDetail, tvFeeDetail, tvBioDetail, tvAddressDetail;
    private Button btnBookDoctorDetail;
    private Toolbar toolbar;
    private Doctor selectedDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        initViews();
        setupToolbar();

        // Lấy dữ liệu Doctor từ Intent
        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");
        if (selectedDoctor != null) {
            displayDoctorInfo(selectedDoctor);
        }

        btnBookDoctorDetail.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorDetailActivity.this, BookAppointmentActivity.class);
            intent.putExtra("doctor_data", selectedDoctor);
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        imgDoctorDetail = findViewById(R.id.imgDoctorDetail);
        tvDegreeDetail = findViewById(R.id.tvDegreeDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvSpecialtyDetail = findViewById(R.id.tvSpecialtyDetail);
        tvExpDetail = findViewById(R.id.tvExpDetail);
        tvFeeDetail = findViewById(R.id.tvFeeDetail);
        tvBioDetail = findViewById(R.id.tvBioDetail);
        tvAddressDetail = findViewById(R.id.tvAddressDetail);
        btnBookDoctorDetail = findViewById(R.id.btnBookDoctorDetail);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết bác sĩ");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayDoctorInfo(Doctor doctor) {
        tvDegreeDetail.setText(doctor.getDegree());
        tvNameDetail.setText(doctor.getName());
        tvSpecialtyDetail.setText(doctor.getSpecialties());
        tvExpDetail.setText(doctor.getExperienceText());
        tvBioDetail.setText(doctor.getBio());
        tvAddressDetail.setText(doctor.getAddress());

        // Format giá tiền
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String fee = formatter.format(doctor.getConsultationFee()) + "đ";
        tvFeeDetail.setText(fee);

        // Load ảnh
        if (doctor.getAvatarUrl() != null && !doctor.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(doctor.getAvatarUrl())
                    .placeholder(R.drawable.doctorbook)
                    .error(R.drawable.doctorbook)
                    .into(imgDoctorDetail);
        } else {
            imgDoctorDetail.setImageResource(doctor.getAvatarResource() != 0 
                    ? doctor.getAvatarResource() : R.drawable.doctorbook);
        }
    }
}
