package com.haui.UniCare.feature.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.LoginRequest;
import com.haui.UniCare.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView tvForgotPass, tvRegister;
    TextInputLayout tilUsername, tilPassword;
    TextInputEditText etUsername, etPassword;
    Button btnLogin;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Khởi tạo LoadingDialog
        loadingDialog = new LoadingDialog(this);

        mapping();
        setupErrorClearer();

        tvRegister.setOnClickListener(v -> {
            Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(register);
        });

        tvForgotPass.setOnClickListener(v -> {
            Intent forgotpass = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(forgotpass);
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });
    }

    private void mapping() {
        tvRegister = findViewById(R.id.textView3);
        tvForgotPass = findViewById(R.id.textView2);
        tilUsername = findViewById(R.id.textInputLayout4);
        tilPassword = findViewById(R.id.textInputLayout5);
        etUsername = findViewById(R.id.textInputEditText);
        etPassword = findViewById(R.id.textInputEditText1);
        btnLogin = findViewById(R.id.button);
    }

    private boolean validateInput(String username, String password) {
        boolean isValid = true;
        if (username.isEmpty()) {
            tilUsername.setError("Vui lòng nhập tên tài khoản");
            isValid = false;
        } else {
            tilUsername.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }
        return isValid;
    }

    private void performLogin(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        // 2. Hiện loading trước khi gọi API
        loadingDialog.showLoading();

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // 3. Ẩn loading khi có kết quả từ server
                loadingDialog.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if ("success".equals(loginResponse.status)) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 3. Ẩn loading nếu lỗi kết nối
                loadingDialog.hideLoading();
                Toast.makeText(LoginActivity.this, "Lỗi kết nối Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupErrorClearer() {
        etUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) tilUsername.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) tilPassword.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
