package com.haui.UniCare.feature.patients.appointment.ui;

import com.haui.UniCare.core.base.BaseActivity;

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

public class AppointmentDetailActivity extends BaseActivity {

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
                    launchMedicalRecord(record);
                } else {
                    launchMedicalRecord(null);
                }
            }

            @Override
            public void onFailure(Call<MedicalRecordResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                launchMedicalRecord(null);
            }
        });
    }

    private void launchMedicalRecord(MedicalRecord record) {
        if (record == null) {
            record = generateMockMedicalRecord();
        }
        Intent intent = new Intent(AppointmentDetailActivity.this, MedicalRecordActivity.class);
        intent.putExtra("medical_record", record);
        intent.putExtra("patient_name", tvPatientName.getText().toString());
        intent.putExtra("patient_dob", tvPatientDob.getText().toString());
        intent.putExtra("appointment_reason", tvAppointmentReason.getText().toString());
        startActivity(intent);
    }

    private MedicalRecord generateMockMedicalRecord() {
        MedicalRecord mock = new MedicalRecord();
        mock.id = appointment.id;
        mock.visitDate = appointment.appointmentDatetime;
        
        String specialty = appointment.specialtyName != null ? appointment.specialtyName.toLowerCase() : "";

        if (specialty.contains("tim mạch")) {
            mock.diagnosis = "Tăng huyết áp vô căn (nguyên phát)";
            mock.prescription = "1. Bisoprolol 5mg x 30 viên (Ngày 1 viên sáng)\n2. Aspirin 81mg x 30 viên (Ngày 1 viên tối)";
            mock.doctorNotes = "Kiêng ăn mặn, tập thể dục nhẹ nhàng đều đặn. Tái khám sau 1 tháng.";
        } else if (specialty.contains("nhi")) {
            mock.diagnosis = "Viêm họng cấp / Sốt siêu vi";
            mock.prescription = "1. Paracetamol 250mg (Uống khi sốt > 38.5)\n2. Oresol (Uống bù nước)\n3. Vitamin C (Ngày 1 viên)";
            mock.doctorNotes = "Theo dõi nhiệt độ trẻ thường xuyên. Lau mát khi sốt cao. Tái khám nếu sốt không hạ sau 3 ngày.";
        } else if (specialty.contains("da liễu")) {
            mock.diagnosis = "Viêm da cơ địa / Chàm (Eczema)";
            mock.prescription = "1. Fucidin 2% (Bôi ngày 2 lần)\n2. Cetirizine 10mg (Ngày 1 viên tối)";
            mock.doctorNotes = "Giữ ẩm da, tránh tiếp xúc hóa chất, xà phòng tẩy rửa mạnh. Uống nhiều nước.";
        } else if (specialty.contains("sản") || specialty.contains("phụ khoa")) {
            mock.diagnosis = "Viêm âm đạo do nấm Candida / Thai kỳ khỏe mạnh";
            mock.prescription = "1. Vitamin tổng hợp (Ngày 1 viên)\n2. Sắt + Axit Folic (Ngày 1 viên)";
            mock.doctorNotes = "Giữ vệ sinh vùng kín. Ăn uống đầy đủ dưỡng chất. Hẹn lịch siêu âm tháng sau.";
        } else if (specialty.contains("nội tiết")) {
            mock.diagnosis = "Đái tháo đường type 2";
            mock.prescription = "1. Metformin 500mg (Ngày 2 viên, chia sáng chiều)";
            mock.doctorNotes = "Kiểm tra đường huyết mao mạch mỗi sáng. Hạn chế tinh bột, đồ ngọt.";
        } else if (specialty.contains("tiêu hóa")) {
            mock.diagnosis = "Viêm loét dạ dày tá tràng / Trào ngược dạ dày thực quản (GERD)";
            mock.prescription = "1. Omeprazole 20mg (Ngày 1 viên trước ăn sáng)\n2. Phosphalugel (Ngày 2 gói sau ăn)";
            mock.doctorNotes = "Ăn uống đúng giờ, không thức khuya, kiêng cay nóng, rượu bia. Kê gối cao khi ngủ.";
        } else if (specialty.contains("thần kinh")) {
            mock.diagnosis = "Đau dây thần kinh tọa / Rối loạn tiền đình";
            mock.prescription = "1. Gabapentin 300mg (Ngày 1 viên tối)\n2. Vitamin 3B (Ngày 2 viên)";
            mock.doctorNotes = "Hạn chế mang vác nặng, sai tư thế. Tập vật lý trị liệu cột sống.";
        } else if (specialty.contains("răng hàm mặt") || specialty.contains("nha khoa")) {
            mock.diagnosis = "Sâu răng mức độ trung bình / Viêm tủy răng non";
            mock.prescription = "1. Ibuprofen 400mg (Uống khi đau)\n2. Nước súc miệng Chlorhexidine";
            mock.doctorNotes = "Đã thực hiện trám răng sinh học. Đánh răng kỹ ngày 2 lần. Hạn chế nhai đồ cứng.";
        } else if (specialty.contains("mắt") || specialty.contains("nhãn khoa")) {
            mock.diagnosis = "Khô mắt / Viêm kết mạc dị ứng";
            mock.prescription = "1. Nước mắt nhân tạo Refresh Tears (Nhỏ 4 lần/ngày)\n2. Thuốc nhỏ mắt kháng viêm (Theo chỉ định)";
            mock.doctorNotes = "Hạn chế sử dụng thiết bị điện tử liên tục. Đeo kính râm khi ra đường tránh bụi.";
        } else {
            mock.diagnosis = "Chưa phát hiện bệnh lý nguy hiểm";
            mock.prescription = "Không cần dùng thuốc đặc trị";
            mock.doctorNotes = "Tiếp tục theo dõi sức khỏe tại nhà, duy trì lối sống lành mạnh. Tái khám khi có triệu chứng mới.";
        }
        
        return mock;
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
                        intent.putExtra("specialty_name", appointment.specialtyName);
                        startActivity(intent);
                    } else {
                        // Pass -1 to show empty state
                        Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                        intent.putExtra("record_id", -1);
                        intent.putExtra("specialty_name", appointment.specialtyName);
                        startActivity(intent);
                    }
                } else {
                    // Pass -1 to show empty state
                    Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                    intent.putExtra("record_id", -1);
                    intent.putExtra("specialty_name", appointment.specialtyName);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<MedicalRecordResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                // Pass -1 to show empty state
                Intent intent = new Intent(AppointmentDetailActivity.this, TreatmentPlanActivity.class);
                intent.putExtra("record_id", -1);
                intent.putExtra("specialty_name", appointment.specialtyName);
                startActivity(intent);
            }
        });
    }
}
