package com.haui.UniCare.feature.patients.profile.viewmodel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.R;

public class ChangePass extends AppCompatActivity {
    ImageButton btnBack;
    Button btnChange;
    TextInputLayout tilPassPresent,tilPassword,tilConfirmPassword;
    TextInputEditText etPassPresent,etPassword,etConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        mapping();
        setupErrorClearer();
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnChange.setOnClickListener(v -> {
            String PassPresent = etPassPresent.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmpassword = etConfirmPassword.getText().toString().trim();
            if(check(PassPresent, password, confirmpassword)){
                finish();
            }
        });

    }
    private void mapping(){
        btnBack = findViewById(R.id.btnBack);
        btnChange = findViewById(R.id.button5);
        tilPassPresent = findViewById(R.id.textInputLayout6);
        tilPassword = findViewById(R.id.textInputLayout7);
        tilConfirmPassword = findViewById(R.id.textInputLayout8);

        etPassPresent = findViewById(R.id.textInputEditText1);
        etPassword = findViewById(R.id.textInputEditText2);
        etConfirmPassword = findViewById(R.id.textInputEditText3);
    }
    private void setupErrorClearer() {
        // Xử lý cho Username
        etPassPresent.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi người dùng gõ bất kỳ ký tự nào, xóa lỗi ngay lập tức
                if (s.length() > 0) {
                    tilPassPresent.setError(null);
                    tilPassPresent.setErrorEnabled(false); // Tắt hoàn toàn dòng thông báo lỗi
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
    private Boolean check(String PassPresent,String password,String confirmpassword){
        boolean rs = true;
        if (PassPresent.isEmpty()) {
            rs = false;
            tilPassPresent.setErrorEnabled(true);
            tilPassPresent.setError("Vui lòng nhập mật khẩu");
        } else if (PassPresent.length() < 8 || password.length() > 20) {
            rs = false;
            tilPassPresent.setErrorEnabled(true);
            tilPassPresent.setError("Mật khẩu phải từ 8-20 ký tự");
        } else if (!PassPresent.matches(".*[A-Z].*")) {
            rs = false;
            tilPassPresent.setErrorEnabled(true);
            tilPassPresent.setError("Mật khẩu phải có ít nhất 1 chữ viết hoa");
        } else if (!PassPresent.matches(".*[0-9].*")) {
            rs = false;
            tilPassPresent.setErrorEnabled(true);
            tilPassPresent.setError("Mật khẩu phải có ít nhất 1 chữ số");
        } else if (!PassPresent.matches(".*[@#$%^&+=!].*")) {
            rs = false;
            tilPassPresent.setErrorEnabled(true);
            tilPassPresent.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
        } else {
            tilPassPresent.setError(null);
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
