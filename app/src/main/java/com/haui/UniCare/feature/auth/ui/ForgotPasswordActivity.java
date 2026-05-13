package com.haui.UniCare.feature.auth.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.ResetPasswordRequest;
import com.haui.UniCare.data.model.SendOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputLayout tilUsername,tilEmail,tilOtp,tilPassword,tilConfirmPassword;
    TextInputEditText etUsername,etEmail,etOtp,etPassword,etConfirmPassword;
    TextView tvLogin;
    Button btnSendOtp,btnChangePassword;
    LoadingDialog loadingDialog;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        
        loadingDialog = new LoadingDialog(this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        
        mapping();
        setupErrorClearer();
        tvLogin.setOnClickListener(v -> {
            finish();
        });
        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                tilEmail.setError("Vui lòng nhập email");
                tilEmail.setErrorEnabled(true);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Email sai định dạng");
                tilEmail.setErrorEnabled(true);
            } else {
                sendOtp(email);
            }
        });
        btnChangePassword.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmpassword = etConfirmPassword.getText().toString().trim();
            if(check(username,email,otp,password,confirmpassword)){
                resetPassword(username, email, otp, password);
            }
        });
    }

    private void sendOtp(String email) {
        loadingDialog.showLoading();
        SendOtpRequest request = new SendOtpRequest(email);
        apiService.sendOtp(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse genericResponse = response.body();
                    if ("success".equals(genericResponse.getStatus())) {
                        Toast.makeText(ForgotPasswordActivity.this, "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, genericResponse.getMessage() != null ? genericResponse.getMessage() : "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Log.e("FORGOT_PASSWORD", "Error: " + t.getMessage());
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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
                    GenericResponse genericResponse = response.body();
                    if ("success".equals(genericResponse.getStatus())) {
                        Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, genericResponse.getMessage() != null ? genericResponse.getMessage() : "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                Log.e("FORGOT_PASSWORD", "Error: " + t.getMessage());
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapping(){
        tvLogin = findViewById(R.id.textView7);
        btnSendOtp = findViewById(R.id.button4);
        btnChangePassword = findViewById(R.id.button3);

        tilUsername = findViewById(R.id.textInputLayout4);
        etUsername = findViewById(R.id.textInputEditText);

        tilEmail = findViewById(R.id.textInputLayout5);
        etEmail = findViewById(R.id.textInputEditText1);

        tilOtp = findViewById(R.id.textInputLayout6);
        etOtp = findViewById(R.id.textInputEditText4);

        tilPassword = findViewById(R.id.textInputLayout7);
        etPassword = findViewById(R.id.textInputEditText2);

        tilConfirmPassword = findViewById(R.id.textInputLayout8);
        etConfirmPassword = findViewById(R.id.textInputEditText3);
    }
    private void setupErrorClearer() {
        // Xử lý cho Username
        etUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi người dùng gõ bất kỳ ký tự nào, xóa lỗi ngay lập tức
                if (s.length() > 0) {
                    tilUsername.setError(null);
                    tilUsername.setErrorEnabled(false); // Tắt hoàn toàn dòng thông báo lỗi
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });


        etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilEmail.setError(null);
                    tilEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etOtp.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilOtp.setError(null);
                    tilOtp.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });



        // Xử lý cho Password
        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilPassword.setError(null);
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etConfirmPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilConfirmPassword.setError(null);
                    tilConfirmPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    private Boolean check(String username,String email,String otp,String password,String confirmpassword){
        boolean rs = true;
        if (username.isEmpty()) {
            rs = false;
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Vui lòng nhập tên tài khoản");
        } else if (username.length() < 6 || username.length() > 20) {
            rs = false;
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Tên tài khoản phải từ 6 đến 20 ký tự");
        } else if (Character.isDigit(username.charAt(0))) {
            rs = false;
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Tên tài khoản không được bắt đầu bằng số");
        } else if (!username.matches(".*[0-9].*")) {
            rs = false;
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Tên tài khoản phải có ít nhất 1 chữ số");
        } else if (!username.matches("^[a-zA-Z0-9]+$")) {
            rs = false;
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Tên tài khoản không được chứa dấu, khoảng trắng hoặc ký tự đặc biệt");
        } else {
            tilUsername.setError(null);
        }

        // Kiểm tra Email (Sử dụng Patterns có sẵn của Android)
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            rs = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email sai định dạng (VD: ten@gmail.com)");
            rs = false;
        } else {
            tilEmail.setErrorEnabled(false);
        }


        if (otp.isEmpty()) {
            rs = false;
            tilOtp.setErrorEnabled(true);
            tilOtp.setError("Vui lòng nhập mã OTP");
        } else if (otp.length() != 6) {
            rs = false;
            tilOtp.setErrorEnabled(true);
            tilOtp.setError("Mã OTP phải có đúng 6 chữ số");
        } else if (!otp.matches("^[0-9]+$")) {
            rs = false;
            tilOtp.setErrorEnabled(true);
            tilOtp.setError("Mã OTP chỉ được chứa các chữ số");
        } else {
            tilOtp.setError(null);
            tilOtp.setErrorEnabled(false);
        }


        if (password.isEmpty()) {
            rs = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Vui lòng nhập mật khẩu");
        } else if (password.length() < 8 || password.length() > 20) {
            rs = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Mật khẩu phải từ 8-20 ký tự");
        } else if (!password.matches(".*[A-Z].*")) {
            rs = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Mật khẩu phải có ít nhất 1 chữ viết hoa");
        } else if (!password.matches(".*[0-9].*")) {
            rs = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Mật khẩu phải có ít nhất 1 chữ số");
        } else if (!password.matches(".*[@#$%^&+=!].*")) {
            rs = false;
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
        } else {
            tilPassword.setError(null);
        }

        if (confirmpassword.isEmpty()) {
            rs = false;
            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
        } else if (!password.equals(confirmpassword)) {
            rs = false;
            tilConfirmPassword.setErrorEnabled(true);
            tilConfirmPassword.setError("Mật khẩu không khớp");
        } else {
            tilConfirmPassword.setError(null);
        }


        return rs;
    }
}
