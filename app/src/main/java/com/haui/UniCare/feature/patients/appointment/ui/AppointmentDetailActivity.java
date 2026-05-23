package com.haui.UniCare.feature.patients.appointment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.AppointmentDetailResponse;
import com.haui.UniCare.data.model.MedicalRecordResponse;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.data.model.table.MedicalRecord;
import com.haui.UniCare.feature.patients.record.activity.MedicalRecordActivity;
import com.haui.UniCare.feature.patients.record.activity.TreatmentPlanActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvDoctorName, tvDoctorSpecialty;
    private TextView tvAppointmentDate, tvPatientName, tvPatientDob, tvPatientPhone, tvAppointmentReason, tvAppointmentStatus;
    private Button btnViewMedicalRecord, btnViewTreatmentPlan;
    private LoadingDialog loadingDialog;

    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        appointment = (Appointment) getIntent().getSerializableExtra("appointment_data");
        if (appointment == null) {
            Toast.makeText(this, "Không có dữ liệu cuộc hẹn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadingDialog = new LoadingDialog(this);
        initViews();
        setupEvents();
        displayBasicInfo();
        loadDetailedInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvDoctorSpecialty = findViewById(R.id.tv_doctor_specialty);
        tvAppointmentDate = findViewById(R.id.tv_appointment_date);
        tvPatientName = findViewById(R.id.tv_patient_name);
        tvPatientDob = findViewById(R.id.tv_patient_dob);
        tvPatientPhone = findViewById(R.id.tv_patient_phone);
        tvAppointmentReason = findViewById(R.id.tv_appointment_reason);
        tvAppointmentStatus = findViewById(R.id.tv_appointment_status);
        btnViewMedicalRecord = findViewById(R.id.btn_view_medical_record);
        btnViewTreatmentPlan = findViewById(R.id.btn_view_treatment_plan);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnViewMedicalRecord.setOnClickListener(v -> viewMedicalRecord());

        btnViewTreatmentPlan.setOnClickListener(v -> viewTreatmentPlan());
    }

    private void displayBasicInfo() {
        String docTitle = appointment.doctorTitle != null ? appointment.doctorTitle : "BS";
        String docName = appointment.doctorName != null ? appointment.doctorName : "Chưa cập nhật";
        tvDoctorName.setText("Bác sĩ: " + docTitle + ". " + docName);

        String specialty = appointment.specialtyName != null ? appointment.specialtyName : 
                            (appointment.doctorBio != null ? appointment.doctorBio : "Đa khoa");
        tvDoctorSpecialty.setText("Chuyên khoa: " + specialty);

        // Định dạng ngày hiển thị
        String rawDate = appointment.appointmentDatetime;
        if (rawDate != null) {
            rawDate = rawDate.replace("T", " ").replace("Z", "");
            if (rawDate.contains(".")) {
                rawDate = rawDate.substring(0, rawDate.indexOf("."));
            }
        }
        tvAppointmentDate.setText(rawDate != null ? rawDate : "Chưa cập nhật");

        tvAppointmentReason.setText(appointment.note != null && !appointment.note.isEmpty() ? appointment.note : "Không có lý do khám");

        String statusStr = "Chờ khám";
        int statusColor = 0xFFD97706; // Amber
        if ("CONFIRMED".equalsIgnoreCase(appointment.status)) {
            statusStr = "Đã xác nhận";
            statusColor = 0xFF3B82F6; // Blue
        } else if ("CANCELLED".equalsIgnoreCase(appointment.status)) {
            statusStr = "Đã hủy";
            statusColor = 0xFFEF4444; // Red
        } else if ("COMPLETED".equalsIgnoreCase(appointment.status)) {
            statusStr = "Hoàn tất";
            statusColor = 0xFF10B981; // Green
        }
        tvAppointmentStatus.setText(statusStr);
        tvAppointmentStatus.setTextColor(statusColor);
    }

    private void loadDetailedInfo() {
        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAppointmentDetails(appointment.id).enqueue(new Callback<AppointmentDetailResponse>() {
            @Override
            public void onResponse(Call<AppointmentDetailResponse> call, Response<AppointmentDetailResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    Appointment detailedApp = response.body().data;
                    if (detailedApp != null) {
                        tvPatientName.setText(detailedApp.patientName != null ? detailedApp.patientName : "Chưa cập nhật");
                        
                        // Định dạng ngày sinh đẹp mắt
                        String dob = detailedApp.patientDob;
                        if (dob != null && dob.contains("T")) {
                            dob = dob.substring(0, dob.indexOf("T"));
                            String[] parts = dob.split("-");
                            if (parts.length == 3) {
                                dob = parts[2] + "/" + parts[1] + "/" + parts[0];
                            }
                        }
                        tvPatientDob.setText(dob != null ? dob : "Chưa cập nhật");
                        tvPatientPhone.setText(detailedApp.patientPhone != null ? detailedApp.patientPhone : "Chưa cập nhật");
                    }
                } else {
                    // Fallback to basic profiles
                    tvPatientName.setText("Chưa cập nhật");
                    tvPatientDob.setText("Chưa cập nhật");
                    tvPatientPhone.setText("Chưa cập nhật");
                }
            }

            @Override
            public void onFailure(Call<AppointmentDetailResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                tvPatientName.setText("Chưa cập nhật");
                tvPatientDob.setText("Chưa cập nhật");
                tvPatientPhone.setText("Chưa cập nhật");
            }
        });
    }

    private void viewMedicalRecord() {
        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getMedicalRecord(appointment.id).enqueue(new Callback<MedicalRecordResponse>() {
            @Override
            public void onResponse(Call<MedicalRecordResponse> call, Response<MedicalRecordResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    MedicalRecord record = response.body().data;
                    if (record != null) {
                        Intent intent = new Intent(AppointmentDetailActivity.this, MedicalRecordActivity.class);
                        intent.putExtra("medical_record", record);
                        intent.putExtra("patient_name", tvPatientName.getText().toString());
                        intent.putExtra("patient_dob", tvPatientDob.getText().toString());
                        intent.putExtra("appointment_reason", tvAppointmentReason.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(AppointmentDetailActivity.this, "Không có dữ liệu bệnh án", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AppointmentDetailActivity.this, "Chưa có bệnh án cho lượt khám này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalRecordResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Toast.makeText(AppointmentDetailActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewTreatmentPlan() {
        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getMedicalRecord(appointment.id).enqueue(new Callback<MedicalRecordResponse>() {
            @Override
            public void onResponse(Call<MedicalRecordResponse> call, Response<MedicalRecordResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    MedicalRecord record = response.body().data;
                    if (record != null) {
                        Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                        intent.putExtra("record_id", record.id);
                        startActivity(intent);
                    } else {
                        // Pass -1 to show empty state
                        Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                        intent.putExtra("record_id", -1);
                        startActivity(intent);
                    }
                } else {
                    // Pass -1 to show empty state
                    Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                    intent.putExtra("record_id", -1);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<MedicalRecordResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                // Pass -1 to show empty state
                Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                intent.putExtra("record_id", -1);
                startActivity(intent);
            }
        });
    }
}
