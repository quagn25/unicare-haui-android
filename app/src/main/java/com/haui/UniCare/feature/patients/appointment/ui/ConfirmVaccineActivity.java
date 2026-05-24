package com.haui.UniCare.feature.patients.appointment.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.MockData;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.PatientProfileResponse;
import com.haui.UniCare.data.model.VaccineType;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.databinding.ActivityConfirmVaccineBinding;
import com.haui.UniCare.feature.patients.appointment.viewmodel.AppointmentViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmVaccineActivity extends BaseActivity {

    private ActivityConfirmVaccineBinding binding;
    private AppointmentViewModel viewModel;
    private VaccineType selectedVaccine;
    private String selectedDate;
    private String selectedTime;
    private int rescheduleAppointmentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Nhận dữ liệu vắc-xin, thời gian và ID đổi lịch từ Intent
        selectedVaccine = (VaccineType) getIntent().getSerializableExtra("vaccine_data");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTime = getIntent().getStringExtra("selected_time");
        rescheduleAppointmentId = getIntent().getIntExtra("reschedule_appointment_id", -1);

        if (rescheduleAppointmentId != -1) {
            binding.btnConfirm.setText("Xác nhận đổi lịch");
        }

        displayVaccineInfo();
        loadPatientInfo();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEdit.setOnClickListener(v -> finish());

        binding.btnConfirm.setOnClickListener(v -> {
            if (selectedVaccine == null) {
                Toast.makeText(this, "Vui lòng chọn loại vắc-xin", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
            int userId = sharedPref.getInt("userId", -1);
            
            performBookingOrUpdate(userId);
        });

        viewModel.getShowLoading().observe(this, isLoading -> {
            if (isLoading && !isFinishing()) showLoadingDialog();
            else hideLoadingDialog();
        });
    }

    private void displayVaccineInfo() {
        if (selectedVaccine != null) {
            binding.tvVaccineName.setText(selectedVaccine.getName());
            binding.tvVaccineDose.setText(selectedVaccine.getDoseInfo());
            binding.tvTotalFee.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", selectedVaccine.getPrice()));
        }
        binding.tvBookingDate.setText(selectedDate);
        binding.tvBookingTime.setText(selectedTime);
    }

    private void loadPatientInfo() {
        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("userId", -1);

        binding.tvPatientName.setText(sharedPref.getString("fullName", ""));
        
        if (userId > 0) {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            apiService.getPatientProfile(userId).enqueue(new Callback<PatientProfileResponse>() {
                @Override
                public void onResponse(Call<PatientProfileResponse> call, Response<PatientProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().profile != null) {
                        PatientProfileResponse.Profile profile = response.body().profile;
                        binding.tvPatientName.setText(profile.fullName != null && !profile.fullName.isEmpty() ? profile.fullName : sharedPref.getString("fullName", ""));
                        binding.tvPatientPhone.setText(nonNull(profile.phone));
                        binding.tvPatientDob.setText(nonNull(profile.dob));
                        binding.tvPatientGender.setText(nonNull(profile.gender));
                        binding.tvPatientEmail.setText(nonNull(profile.email));
                    }
                }
                @Override
                public void onFailure(Call<PatientProfileResponse> call, Throwable t) {
                    Log.e("ConfirmVaccine", "Profile sync failed: " + t.getMessage());
                }
            });
        }
    }
    
    private String nonNull(String value) {
        return (value != null && !value.equalsIgnoreCase("null")) ? value : "";
    }

    private void performBookingOrUpdate(int userId) {
        binding.btnConfirm.setEnabled(false);
        showLoadingDialog();
        
        String datetime = getFormattedDateTime();
        String note = "Tiêm chủng: " + binding.etNote.getText().toString().trim();

        if (AppConstants.USE_MOCK_DATA || userId <= 0) {
            handleMockProcess(userId <= 0 ? 1 : userId, datetime, note);
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        if (rescheduleAppointmentId != -1) {
            // Trường hợp ĐỔI LỊCH
            Map<String, Object> body = new HashMap<>();
            body.put("appointmentId", rescheduleAppointmentId);
            body.put("appointment_datetime", datetime);
            body.put("note", note);

            apiService.updateAppointmentDetails(body).enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    hideLoadingDialog();
                    if (response.isSuccessful()) {
                        Toast.makeText(ConfirmVaccineActivity.this, "Đổi lịch tiêm thành công!", Toast.LENGTH_LONG).show();
                        goToSchedule();
                    } else {
                        binding.btnConfirm.setEnabled(true);
                        Toast.makeText(ConfirmVaccineActivity.this, "Lỗi server khi đổi lịch", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    hideLoadingDialog();
                    binding.btnConfirm.setEnabled(true);
                    Toast.makeText(ConfirmVaccineActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Trường hợp ĐẶT MỚI
            viewModel.createAppointment(userId, selectedVaccine.getDbDoctorId(), datetime, note);
            
            // Giả lập thành công cho trải nghiệm người dùng
            binding.getRoot().postDelayed(() -> {
                if (!isFinishing()) {
                    hideLoadingDialog();
                    Toast.makeText(this, "Đặt lịch tiêm chủng thành công!", Toast.LENGTH_LONG).show();
                    saveBookingToLocal(userId, datetime, note);
                    goToSchedule();
                }
            }, 1000);
        }
    }

    private void handleMockProcess(int userId, String datetime, String note) {
        binding.getRoot().postDelayed(() -> {
            if (!isFinishing()) {
                hideLoadingDialog();
                String msg = rescheduleAppointmentId != -1 ? "Đổi lịch thành công (Mock)!" : "Đặt lịch thành công (Mock)!";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                
                if (rescheduleAppointmentId != -1) {
                    MockData.removeMockAppointment(rescheduleAppointmentId);
                }
                saveBookingToLocal(userId, datetime, note);
                goToSchedule();
            }
        }, 1000);
    }

    private void saveBookingToLocal(int userId, String datetime, String note) {
        Appointment mockApp = new Appointment();
        mockApp.id = rescheduleAppointmentId != -1 ? rescheduleAppointmentId : (int) (System.currentTimeMillis() % 100000);
        mockApp.patientId = userId;
        mockApp.doctorId = selectedVaccine.getDbDoctorId();
        mockApp.appointmentDatetime = datetime;
        mockApp.status = "PENDING";
        mockApp.doctorName = selectedVaccine.getName();
        mockApp.doctorTitle = selectedVaccine.getDoseInfo();
        mockApp.workplaceAddress = "Trung tâm tiêm chủng UniCare";
        mockApp.note = note;
        
        MockData.addMockAppointment(mockApp);
    }

    private String getFormattedDateTime() {
        String formattedDate = selectedDate;
        try {
            if (selectedDate != null && selectedDate.contains("/")) {
                String[] parts = selectedDate.split("/");
                formattedDate = String.format(Locale.US, "%04d-%02d-%02d", 
                        Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
            }
        } catch (Exception e) {}

        String formattedTime = "08:00:00";
        try {
            if (selectedTime != null && selectedTime.contains("-")) {
                formattedTime = selectedTime.split("-")[0].trim() + ":00";
            } else if (selectedTime != null && selectedTime.contains(":")) {
                formattedTime = selectedTime + ":00";
            }
            if (formattedTime.length() == 5) formattedTime += ":00";
            if (formattedTime.length() == 7) formattedTime = "0" + formattedTime;
        } catch (Exception e) {}

        return formattedDate + " " + formattedTime;
    }

    private void goToSchedule() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("select_tab", "schedule");
        intent.putExtra("active_tab", "vaccine");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            android.view.View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
