package com.haui.UniCare.feature.patients.profile.ui;

import com.haui.UniCare.core.base.BaseActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.PatientProfileResponse;
import com.haui.UniCare.data.model.PatientProfileUpdateRequest;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextInputEditText etEditName;
    private TextInputEditText etEditDob;
    private RadioGroup rgEditGender;
    private RadioButton rbEditMale;
    private RadioButton rbEditFemale;
    private TextInputEditText etEditPhone;
    private TextInputEditText etEditAddress;
    private TextInputEditText etEditEmail;
    private MaterialButton btnSaveProfile;

    private LoadingDialog loadingDialog;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        loadingDialog = new LoadingDialog(this);

        SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", 0);

        initViews();
        setupListeners();
        loadExistingProfile();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etEditName = findViewById(R.id.etEditName);
        etEditDob = findViewById(R.id.etEditDob);
        rgEditGender = findViewById(R.id.rgEditGender);
        rbEditMale = findViewById(R.id.rbEditMale);
        rbEditFemale = findViewById(R.id.rbEditFemale);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);
        etEditEmail = findViewById(R.id.etEditEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etEditDob.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        etEditName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String fullName = etEditName.getText().toString().trim();
                if (fullName.isEmpty()) {
                    etEditName.setError("Họ và tên không được để trống");
                } else if (fullName.length() < 2) {
                    etEditName.setError("Họ và tên phải có ít nhất 2 ký tự");
                }
            }
        });

        etEditPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = etEditPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    etEditPhone.setError("Số điện thoại không được để trống");
                } else if (!phone.matches("^(0|\\+84)[0-9]{9}$")) {
                    etEditPhone.setError("Số điện thoại không hợp lệ (gồm 10 số)");
                }
            }
        });

        etEditEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = etEditEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    etEditEmail.setError("Email không được để trống");
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEditEmail.setError("Email không đúng định dạng");
                }
            }
        });

        etEditAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String address = etEditAddress.getText().toString().trim();
                if (address.isEmpty()) {
                    etEditAddress.setError("Địa chỉ không được để trống");
                }
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etEditDob.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void loadExistingProfile() {
        if (userId == 0) return;

        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getPatientProfile(userId).enqueue(new Callback<PatientProfileResponse>() {
            @Override
            public void onResponse(Call<PatientProfileResponse> call, Response<PatientProfileResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    populateFields(response.body().profile);
                }
            }

            @Override
            public void onFailure(Call<PatientProfileResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Log.e("EDIT_PROFILE_DEBUG", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void populateFields(PatientProfileResponse.Profile profile) {
        if (profile == null) return;

        etEditName.setText(profile.fullName);
        etEditDob.setText(profile.dob);
        etEditPhone.setText(profile.phone);
        etEditAddress.setText(profile.address);
        etEditEmail.setText(profile.email);

        if ("Nam".equalsIgnoreCase(profile.gender)) {
            rbEditMale.setChecked(true);
        } else if ("Nữ".equalsIgnoreCase(profile.gender)) {
            rbEditFemale.setChecked(true);
        }
    }

    private void saveProfile() {
        String fullName = etEditName.getText().toString().trim();
        String dob = etEditDob.getText().toString().trim();
        String phone = etEditPhone.getText().toString().trim();
        String address = etEditAddress.getText().toString().trim();
        String email = etEditEmail.getText().toString().trim();

        String gender = "Nam";
        if (rgEditGender.getCheckedRadioButtonId() == R.id.rbEditFemale) {
            gender = "Nữ";
        }

        if (fullName.isEmpty()) {
            etEditName.setError("Họ và tên không được để trống");
            etEditName.requestFocus();
            return;
        }
        if (fullName.length() < 2) {
            etEditName.setError("Họ và tên phải có ít nhất 2 ký tự");
            etEditName.requestFocus();
            return;
        }

        if (dob.isEmpty()) {
            etEditDob.setError("Ngày sinh không được để trống");
            return;
        }

        if (phone.isEmpty()) {
            etEditPhone.setError("Số điện thoại không được để trống");
            etEditPhone.requestFocus();
            return;
        }
        if (!phone.matches("^(0|\\+84)[0-9]{9}$")) {
            etEditPhone.setError("Số điện thoại không hợp lệ (gồm 10 số)");
            etEditPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEditEmail.setError("Email không được để trống");
            etEditEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEditEmail.setError("Email không đúng định dạng");
            etEditEmail.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            etEditAddress.setError("Địa chỉ không được để trống");
            etEditAddress.requestFocus();
            return;
        }

        loadingDialog.showLoading();
        PatientProfileUpdateRequest request = new PatientProfileUpdateRequest(userId, fullName, dob, gender, phone, address, email);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        apiService.updatePatientProfile(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    
                    // Sync user SharedPreferences immediately
                    SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                    sharedPref.edit().putString("fullName", fullName).apply();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Log.e("EDIT_PROFILE_DEBUG", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
