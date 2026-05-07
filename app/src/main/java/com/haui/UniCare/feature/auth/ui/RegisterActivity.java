package com.haui.UniCare.feature.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.haui.UniCare.R;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    TextView tvLogin;
    FrameLayout layoutDoctor,layoutPatient;
    ImageView imgDoctor,imgPatient,tickDoctor,tickPatient;
    TextInputLayout tilUsername,tilPassword,tilConfirmPassword;
    TextInputEditText etUsername,etPassword,etConfirmPassword;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mapping();
        setupErrorClearer();
        tvLogin.setOnClickListener(v -> {
            finish();
        });

        layoutDoctor.setOnClickListener(v -> {
            // Bật Doctor
            layoutDoctor.setActivated(true);
            tickDoctor.setVisibility(View.VISIBLE);
            // Tắt Patient
            layoutPatient.setActivated(false);
            tickPatient.setVisibility(View.GONE);
        });

        layoutPatient.setOnClickListener(v -> {
            // Bật Patient
            layoutPatient.setActivated(true);
            tickPatient.setVisibility(View.VISIBLE);
            // Tắt Doctor
            layoutDoctor.setActivated(false);
            tickDoctor.setVisibility(View.GONE);
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmpassword = etConfirmPassword.getText().toString().trim();
            String role = layoutDoctor.isActivated() ? "DOCTOR" : "PATIENT";

            if(check(username, password, confirmpassword)){
                Intent intent = new Intent(RegisterActivity.this, CreateFileActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });
    }
    private void mapping(){
        tvLogin = findViewById(R.id.textView7);
        layoutDoctor = findViewById(R.id.layoutDoctor);
        layoutPatient = findViewById(R.id.layoutPatient);
        imgDoctor = findViewById(R.id.imgDoctor);
        imgPatient = findViewById(R.id.imgPatient);
        tickDoctor = findViewById(R.id.tickDoctor);
        tickPatient = findViewById(R.id.tickPatient);

        tilUsername = findViewById(R.id.textInputLayout4);
        tilPassword = findViewById(R.id.textInputLayout5);
        tilConfirmPassword = findViewById(R.id.textInputLayout6);
        etUsername = findViewById(R.id.textInputEditText);
        etPassword = findViewById(R.id.textInputEditText1);
        etConfirmPassword = findViewById(R.id.textInputEditText2);
        btnRegister = findViewById(R.id.button3);
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
    private Boolean check(String username,String password,String confirmpassword){
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

        if(!layoutDoctor.isActivated() && !layoutPatient.isActivated()){
            rs = false;
            Toast.makeText(this, "Vui lòng chọn kiểu tài khoản", Toast.LENGTH_SHORT).show();
        }
        return rs;
    }
}