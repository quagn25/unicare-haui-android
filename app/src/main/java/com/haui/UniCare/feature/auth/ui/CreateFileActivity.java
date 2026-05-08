package com.haui.UniCare.feature.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.MainActivity;
import com.haui.UniCare.R;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.RegisterRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateFileActivity extends AppCompatActivity {
    TextInputLayout tilName, tilDate, tilEmail, tilGender;
    TextInputEditText edtName, edtDate, edtEmail;
    Button btnNext;
    RadioButton radioMale, radioFemale;

    private String username, password, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createfile);

        mapping();
        setupErrorClearer();

        // Lấy dữ liệu từ Intent
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        role = getIntent().getStringExtra("role");

        edtDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        btnNext.setOnClickListener(v -> {
            if (validateData()) {
                performRegister();
            }
        });
    }

    private void mapping() {
        tilName = findViewById(R.id.textInputLayout4);
        tilDate = findViewById(R.id.textInputLayout5);
        tilEmail = findViewById(R.id.textInputLayout6);
        edtName = findViewById(R.id.textInputEditText4);
        edtDate = findViewById(R.id.textInputEditText5);
        edtEmail = findViewById(R.id.textInputEditText6);
        btnNext = findViewById(R.id.button3);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        tilGender = findViewById(R.id.textInputLayout7);
    }

    private void performRegister() {
        String name = edtName.getText().toString().trim();
        String dob = edtDate.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String gender = radioMale.isChecked() ? "MALE" : "FEMALE";

        // Tạo object request
        RegisterRequest request = new RegisterRequest(username, password, role, name, dob, gender, email);

        // Gọi API - Đã đổi thành getInstance() cho đúng với RetrofitClient.java
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.register(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateFileActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateFileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateFileActivity.this, "Lỗi đăng ký: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateFileActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        // Khởi tạo DatePicker chuẩn Material 3
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("CHỌN NGÀY SINH")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Định dạng ngày được chọn và hiển thị lên EditText
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(selection));
            edtDate.setText(dateString);
            tilDate.setErrorEnabled(false); // Tắt báo lỗi nếu đã chọn
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private boolean validateData() {
        boolean isValid = true;

        String name = edtName.getText().toString().trim();
        String dob = edtDate.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        // Kiểm tra Họ tên
        String namePattern = "^[a-zA-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s]+$";
        String containsVietnameseAccent = ".*[ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ].*";
        if (name.isEmpty()) {
            tilName.setError("Vui lòng nhập họ tên");
            isValid = false;
        } else if (name.length() < 2 || name.length() > 50) {
            tilName.setError("Họ tên phải từ 2 đến 50 ký tự");
            isValid = false;
        } else if (!name.matches(namePattern)) {
            tilName.setError("Họ tên không được chứa số hoặc ký tự đặc biệt");
            isValid = false;
        } else if (!name.matches(containsVietnameseAccent)) {
            tilName.setError("Họ tên phải là tiếng Việt có dấu");
            isValid = false;
        } else {
            tilName.setErrorEnabled(false);
        }

        // Kiểm tra Ngày sinh
        if (dob.isEmpty()) {
            tilDate.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        } else {
            tilDate.setErrorEnabled(false);
        }


        // Kiểm tra Email (Sử dụng Patterns có sẵn của Android)
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email sai định dạng (VD: ten@gmail.com)");
            isValid = false;
        } else {
            tilEmail.setErrorEnabled(false);
        }

        if(!radioMale.isChecked() && !radioFemale.isChecked()){
            isValid = false;
            tilGender.setError("Vui lòng chọn giới tính");
        }else{
            tilGender.setErrorEnabled(false);
        }

        return isValid;
    }

    private void setupErrorClearer() {
        // Xử lý cho Email
        edtEmail.addTextChangedListener(new android.text.TextWatcher() {
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

        // Xử lý cho Name
        edtName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilName.setError(null);
                    tilName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
