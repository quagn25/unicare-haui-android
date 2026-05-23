package com.haui.UniCare.feature.auth.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.ResetPasswordRequest;
import com.haui.UniCare.data.model.SendOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {
    
    // Layouts for steps
    private LinearLayout layoutStep1, layoutStep2, layoutStep3;
    
    // Step 1 components
    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private Button btnSendOtp;
    
    // Step 2 components
    private TextInputLayout tilOtp;
    private TextInputEditText etOtp;
    private Button btnVerifyOtp;
    
    // Step 3 components
    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private Button btnChangePassword;
    
    private TextView tvBackToLogin;
    private LoadingDialog loadingDialog;
    private ApiService apiService;
    
    private String currentEmail = "";
    private String currentUsername = ""; // Will be received from server during sendOtp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        loadingDialog = new LoadingDialog(this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        mapping();
        setupListeners();
        showStep(1);
    }

    private void mapping() {
        layoutStep1 = findViewById(R.id.layout_step1);
        layoutStep2 = findViewById(R.id.layout_step2);
        layoutStep3 = findViewById(R.id.layout_step3);

        tilEmail = findViewById(R.id.til_email);
        etEmail = findViewById(R.id.et_email);
        btnSendOtp = findViewById(R.id.btn_send_otp);

        tilOtp = findViewById(R.id.til_otp);
        etOtp = findViewById(R.id.et_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        tilPassword = findViewById(R.id.til_password);
        etPassword = findViewById(R.id.et_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);

        tvBackToLogin = findViewById(R.id.tv_back_to_login);
    }

    private void setupListeners() {
        tvBackToLogin.setOnClickListener(v -> finish());

        // Step 1: Send OTP
        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (validateStep1(email)) {
                sendOtp(email);
            }
        });

        // Step 2: Verify OTP (UI only, resetPassword handles actual verification)
        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (validateStep2(otp)) {
                showStep(3);
            }
        });

        // Step 3: Change Password
        btnChangePassword.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
            
            if (validateStep3(password, confirmPassword)) {
                resetPassword(currentUsername, currentEmail, otp, password);
            }
        });
    }

    private void showStep(int step) {
        layoutStep1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        layoutStep2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        layoutStep3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);
    }

    private boolean validateStep1(String email) {
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return false;
        }
        tilEmail.setError(null);
        return true;
    }

    private boolean validateStep2(String otp) {
        if (otp.isEmpty() || otp.length() != 6) {
            tilOtp.setError("Mã OTP phải gồm 6 chữ số");
            return false;
        }
        tilOtp.setError(null);
        return true;
    }

    private boolean validateStep3(String password, String confirmPassword) {
        if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            return false;
        }
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        return true;
    }

    private void sendOtp(String email) {
        loadingDialog.showLoading();
        SendOtpRequest request = new SendOtpRequest(email);
        apiService.sendOtp(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if ("success".equals(res.getStatus())) {
                        currentEmail = email;
                        currentUsername = res.getUsername(); // Saved for later
                        Toast.makeText(ForgotPasswordActivity.this, res.getMessage() != null ? res.getMessage() : "Mã OTP đã được gửi!", Toast.LENGTH_LONG).show();
                        showStep(2);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Email không tồn tại trong hệ thống";
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
                        if (response.code() == 500) {
                            errorMsg = "Lỗi kết nối server gửi email";
                        }
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword(String username, String email, String otp, String password) {
        loadingDialog.showLoading();
        ResetPasswordRequest request = new ResetPasswordRequest(username, email, otp, password);
        apiService.resetPassword(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse res = response.body();
                    if ("success".equals(res.getStatus())) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Lưu phiên đăng nhập tự động để vào thẳng MainActivity
                        SharedPreferences sharedPref = getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.putString("fullName", "Người dùng"); 
                        // userId có thể được server trả về thêm trong tương lai để đồng bộ hơn
                        editor.apply();

                        // Chuyển hướng về Trang chủ (MainActivity)
                        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
