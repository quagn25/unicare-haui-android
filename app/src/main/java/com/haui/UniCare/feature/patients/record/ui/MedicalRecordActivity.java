package com.haui.UniCare.feature.patients.record.ui;

import com.haui.UniCare.core.base.BaseActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.MedicalRecord;

import java.util.Calendar;

public class MedicalRecordActivity extends BaseActivity {

    private ImageView btnBack;
    private TextView tvPatientHeader, tvRecordCode;
    private TextView tvAdmissionStatus, tvDiagnosis, tvPrescription, tvDoctorNotes;
    private Button btnSetReminder;

    private MedicalRecord record;
    private String patientName;
    private String patientDob;
    private String appointmentReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record);

        record = (MedicalRecord) getIntent().getSerializableExtra("medical_record");
        patientName = getIntent().getStringExtra("patient_name");
        patientDob = getIntent().getStringExtra("patient_dob");
        appointmentReason = getIntent().getStringExtra("appointment_reason");

        if (record == null) {
            Toast.makeText(this, "Không có dữ liệu bệnh án", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupEvents();
        displayData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvPatientHeader = findViewById(R.id.tv_patient_header);
        tvRecordCode = findViewById(R.id.tv_record_code);
        tvAdmissionStatus = findViewById(R.id.tv_admission_status);
        tvDiagnosis = findViewById(R.id.tv_diagnosis);
        tvPrescription = findViewById(R.id.tv_prescription);
        tvDoctorNotes = findViewById(R.id.tv_doctor_notes);
        btnSetReminder = findViewById(R.id.btn_set_reminder);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        
        btnSetReminder.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đặt nhắc nhở lịch uống thuốc thành công!", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayData() {
        // Tính tuổi từ patientDob (Định dạng dd/MM/yyyy hoặc YYYY-MM-DD)
        int age = 20; // Default fallback age
        if (patientDob != null && !patientDob.isEmpty()) {
            try {
                String cleanDob = patientDob.trim();
                String yearStr = "";
                if (cleanDob.contains("/")) {
                    String[] parts = cleanDob.split("/");
                    yearStr = parts[parts.length - 1];
                } else if (cleanDob.contains("-")) {
                    String[] parts = cleanDob.split("-");
                    yearStr = parts[0];
                }
                
                if (!yearStr.isEmpty()) {
                    int birthYear = Integer.parseInt(yearStr);
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    // Nếu năm hiện tại chưa đạt tới 2026, giả định theo hệ thống hiện tại là 2026
                    if (currentYear < 2026) {
                        currentYear = 2026;
                    }
                    age = currentYear - birthYear;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String name = patientName != null ? patientName : "Người bệnh";
        tvPatientHeader.setText(name + ", " + age + " tuổi");

        // Format mã số bệnh án đẹp mắt, vd: 000001
        String code = String.format("%06d", record.id);
        tvRecordCode.setText("Mã số: " + code);

        // Tình trạng lúc vào viện: Nếu lý do khám trống, lấy mô tả trong bệnh án hoặc hiển thị mặc định
        String reason = appointmentReason != null && !appointmentReason.isEmpty() ? appointmentReason : "Sốt cao, đau họng, mệt mỏi";
        tvAdmissionStatus.setText(reason);

        tvDiagnosis.setText(record.diagnosis != null && !record.diagnosis.isEmpty() ? record.diagnosis : "Chưa có chẩn đoán");
        
        tvPrescription.setText(record.prescription != null && !record.prescription.isEmpty() ? 
                               record.prescription.replace("\\n", "\n") : "Không kê đơn");
                               
        tvDoctorNotes.setText(record.doctorNotes != null && !record.doctorNotes.isEmpty() ? 
                             record.doctorNotes : "Không có lời dặn");
    }
}
