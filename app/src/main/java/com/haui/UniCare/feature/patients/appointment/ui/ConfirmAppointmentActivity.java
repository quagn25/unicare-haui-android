package com.haui.UniCare.feature.patients.appointment.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.databinding.ActivityConfirmAppointmentBinding;
import com.haui.UniCare.feature.patients.appointment.viewmodel.AppointmentViewModel;

import java.util.Locale;

public class ConfirmAppointmentActivity extends BaseActivity {

    private ActivityConfirmAppointmentBinding binding;
    private AppointmentViewModel viewModel;
    private Doctor selectedDoctor;
    private String selectedDate;
    private String selectedTime;
    private int rescheduleAppointmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Nhận dữ liệu từ BookAppointmentActivity
        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTime = getIntent().getStringExtra("selected_time");
        rescheduleAppointmentId = getIntent().getIntExtra("reschedule_appointment_id", -1);

        if (rescheduleAppointmentId != -1) {
            binding.btnConfirmBook.setText("Xác nhận đổi lịch");
        }

        displayDoctorInfo();
        displayPatientInfo();

        // Nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Nút "Xác nhận đặt lịch"
        binding.btnConfirmBook.setOnClickListener(v -> {
            if (selectedDoctor != null) {
                if (AppConstants.USE_MOCK_DATA) {
                    String toastMsg = rescheduleAppointmentId != -1 ? "Đã đổi lịch thành công (Chế độ Mock)" : "Đã đặt lịch thành công (Chế độ Mock)";
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ConfirmAppointmentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("select_tab", "schedule");
                    startActivity(intent);
                    finish();
                } else {
                    performBooking();
                }
            } else {
                Toast.makeText(this, "Thiếu thông tin bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát kết quả từ ViewModel
        viewModel.getShowLoading().observe(this, isLoading -> {
            if (isLoading) showLoadingDialog();
            else hideLoadingDialog();
        });

        viewModel.getIsBookingSuccess().observe(this, success -> {
            if (success) {
                String successMsg = rescheduleAppointmentId != -1 ? "Đổi lịch hẹn thành công!" : "Đặt lịch thành công! Vui lòng chờ bác sĩ xác nhận.";
                Toast.makeText(this, successMsg, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ConfirmAppointmentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("select_tab", "schedule");
                startActivity(intent);
                finish();
            } else {
                String errorMsg = rescheduleAppointmentId != -1 ? "Đổi lịch thất bại. Vui lòng thử lại sau." : "Đặt lịch thất bại. Vui lòng thử lại sau.";
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Bỏ systemBars.bottom để BottomNav tràn xuống dưới
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });
    }

    private void displayDoctorInfo() {
        if (selectedDoctor != null) {
            binding.tvConfirmDoctorName.setText(selectedDoctor.getName());
            binding.tvConfirmSpecialty.setText(selectedDoctor.getSpecialties());
            binding.tvConfirmFee.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", selectedDoctor.getConsultationFee()));
            
            if (selectedDoctor.getAvatarUrl() != null && !selectedDoctor.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(selectedDoctor.getAvatarUrl())
                        .placeholder(R.drawable.doctorbook)
                        .into(binding.imgConfirmDoctor);
            } else {
                binding.imgConfirmDoctor.setImageResource(selectedDoctor.getAvatarResource() != 0 
                        ? selectedDoctor.getAvatarResource() : R.drawable.doctorbook);
            }
        }
        binding.tvConfirmDate.setText(selectedDate);
        binding.tvConfirmTime.setText(selectedTime);
    }

    private void displayPatientInfo() {
        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        String fullName = sharedPref.getString("fullName", "Bùi Văn Quang");
        String username = sharedPref.getString("username", "0386260806"); // Dùng làm số điện thoại nếu cần
        
        // Hiển thị thông tin đã lưu hoặc mock nếu chưa có
        binding.tvPatientName.setText(fullName);
        binding.tvPatientPhone.setText(username);
        
        // Bạn có thể lưu thêm DOB và Gender vào SharedPreferences lúc Login/Register để hiện ở đây
        // Hiện tại giả định các giá trị mặc định hoặc lấy từ Prefs nếu có
        binding.tvPatientDob.setText(sharedPref.getString("dob", "01/01/2006"));
        binding.tvPatientGender.setText(sharedPref.getString("gender", "Nam"));
    }

    private void performBooking() {
        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentPatientId = sharedPref.getInt("userId", -1);

        if (currentPatientId == -1) {
            Toast.makeText(this, "Bạn cần đăng nhập để đặt lịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse date (d/M/yyyy or dd/MM/yyyy) to (yyyy-MM-dd)
        String formattedDate = "";
        try {
            if (selectedDate != null && selectedDate.contains("/")) {
                String[] dateParts = selectedDate.split("/");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
            } else {
                formattedDate = selectedDate;
            }
        } catch (Exception e) {
            formattedDate = selectedDate;
        }

        // Parse time (HH:mm - HH:mm (Period)) to (HH:mm:00)
        String formattedTime = "00:00:00";
        try {
            if (selectedTime != null && selectedTime.contains("-")) {
                String startHour = selectedTime.split("-")[0].trim();
                formattedTime = startHour + ":00";
            }
        } catch (Exception e) {
            // fallback
        }

        String datetime = formattedDate + " " + formattedTime;
        String note = binding.etAppointmentNote.getText().toString().trim();
        
        if (rescheduleAppointmentId != -1) {
            viewModel.updateAppointment(rescheduleAppointmentId, datetime, note);
        } else {
            viewModel.createAppointment(currentPatientId, selectedDoctor.getId(), datetime, note);
        }
    }
}
