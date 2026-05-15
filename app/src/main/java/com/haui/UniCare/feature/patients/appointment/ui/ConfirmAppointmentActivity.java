package com.haui.UniCare.feature.patients.appointment.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
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

        displayDoctorInfo();
        displayPatientInfo();

        // Nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Nút "Xác nhận đặt lịch"
        binding.btnConfirmBook.setOnClickListener(v -> {
            if (selectedDoctor != null) {
                if (AppConstants.USE_MOCK_DATA) {
                    Toast.makeText(this, "Đã đặt lịch thành công (Chế độ Mock)", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "Đặt lịch thành công! Vui lòng chờ bác sĩ xác nhận.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Đặt lịch thất bại. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
            }
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

        String datetime = selectedDate + " " + selectedTime;
        String note = binding.etAppointmentNote.getText().toString().trim();
        
        viewModel.createAppointment(currentPatientId, selectedDoctor.getId(), datetime, note);
    }
}
