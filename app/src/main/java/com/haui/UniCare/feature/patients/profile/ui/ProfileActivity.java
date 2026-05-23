package com.haui.UniCare.feature.patients.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.PatientProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvEditProfile;
    private TextView tvProfileName;
    private TextView tvProfileDob;
    private TextView tvProfileGender;
    private TextView tvProfilePhone;
    private TextView tvProfileAddress;
    private TextView tvProfileEmail;

    private LoadingDialog loadingDialog;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingDialog = new LoadingDialog(this);

        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getInt("userId", 0);

        initViews();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvEditProfile = findViewById(R.id.tvEditProfile);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileDob = findViewById(R.id.tvProfileDob);
        tvProfileGender = findViewById(R.id.tvProfileGender);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivityForResult(intent, 100);
        });
    }

    private void loadProfileData() {
        if (userId == 0) {
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getPatientProfile(userId).enqueue(new Callback<PatientProfileResponse>() {
            @Override
            public void onResponse(Call<PatientProfileResponse> call, Response<PatientProfileResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    displayProfile(response.body().profile);
                } else {
                    Toast.makeText(ProfileActivity.this, "Không thể tải thông tin hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientProfileResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Log.e("PROFILE_DEBUG", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProfile(PatientProfileResponse.Profile profile) {
        if (profile == null) return;
        tvProfileName.setText(profile.fullName != null ? profile.fullName : "Chưa cập nhật");
        tvProfileDob.setText(profile.dob != null ? profile.dob : "Chưa cập nhật");
        tvProfileGender.setText(profile.gender != null ? profile.gender : "Chưa cập nhật");
        tvProfilePhone.setText(profile.phone != null ? profile.phone : "Chưa cập nhật");
        tvProfileAddress.setText(profile.address != null ? profile.address : "Chưa cập nhật");
        tvProfileEmail.setText(profile.email != null ? profile.email : "Chưa cập nhật");

        // Sync local preferences name
        SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
        sharedPref.edit().putString("fullName", profile.fullName).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadProfileData();
        }
    }
}
