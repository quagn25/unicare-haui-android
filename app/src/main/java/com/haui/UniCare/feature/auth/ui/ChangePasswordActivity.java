package com.haui.UniCare.feature.auth.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.ChangePasswordRequest;
import com.haui.UniCare.data.model.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChange;

    private LoadingDialog loadingDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        loadingDialog = new LoadingDialog(this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        mapping();
        setupListeners();
    }

    private void mapping() {
        btnBack = findViewById(R.id.btnBack);
        etCurrentPassword = findViewById(R.id.textInputEditText1);
        etNewPassword = findViewById(R.id.textInputEditText2);
        etConfirmPassword = findViewById(R.id.textInputEditText3);
        btnChange = findViewById(R.id.button5);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChange.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(currentPassword, newPassword);
        });
    }

    private void changePassword(String currentPassword, String newPassword) {
        SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.showLoading();
        ChangePasswordRequest request = new ChangePasswordRequest(userId, currentPassword, newPassword);

        apiService.changePassword(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if ("success".equals(res.getStatus())) {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Trở lại màn hình profile
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Đổi mật khẩu thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            GenericResponse errorResponse = gson.fromJson(errStr, GenericResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ChangePasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
