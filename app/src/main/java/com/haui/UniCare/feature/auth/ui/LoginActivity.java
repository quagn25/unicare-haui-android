package com.haui.UniCare.feature.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;

public class LoginActivity extends AppCompatActivity {
    TextView tvForgotPass, tvRegister;
    TextInputLayout tilUsername, tilPassword;
    TextInputEditText etUsername, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // SỬA LỖI: Đổi activity_main thành login_activity để khớp với các ID bên dưới
        setContentView(R.layout.login_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            check(username, password);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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

    private void setupErrorClearer() {
        // Xử lý cho Username
        etUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilUsername.setError(null);
                    tilUsername.setErrorEnabled(false);
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
    }

    private void check(String username, String password) {
        if (username.isEmpty()) {
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("Vui lòng nhập tên tài khoản");
        } else {
            tilUsername.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setErrorEnabled(true);
            tilPassword.setError("Vui lòng nhập mật khẩu");
        } else {
            tilPassword.setError(null);
        }
    }
}
