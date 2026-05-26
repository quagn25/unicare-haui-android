package com.haui.UniCare.feature.patients.profile.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.haui.UniCare.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditProfilePerson extends AppCompatActivity {
    ImageButton btnBack;
    RadioGroup radioGroupGender;
    RadioButton rbMale, rbFemale;
    Button btnUpdate;
    TextInputEditText etName, etDob, etPhone, etAddress, etEmail;
    TextInputLayout tilName, tilDate, tilPhone, tilAddress, tilEmail, tilGender;

    private int lastCheckedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        // Ánh xạ View
        btnBack = findViewById(R.id.btnBack);
        radioGroupGender = findViewById(R.id.rgEditGender);
        rbMale = findViewById(R.id.rbEditMale);
        rbFemale = findViewById(R.id.rbEditFemale);
        btnUpdate = findViewById(R.id.btnSaveProfile);
        
        etName = findViewById(R.id.etEditName);
        etDob = findViewById(R.id.etEditDob);
        etPhone = findViewById(R.id.etEditPhone);
        etAddress = findViewById(R.id.etEditAddress);
        etEmail = findViewById(R.id.etEditEmail);

        tilName = findViewById(R.id.tilEditName);
        tilDate = findViewById(R.id.tilEditDob);
        tilPhone = findViewById(R.id.tilEditPhone);
        tilAddress = findViewById(R.id.tilEditAddress);
        tilEmail = findViewById(R.id.tilEditEmail);
        tilGender = findViewById(R.id.tilEditGender);

        etDob.setOnClickListener(v -> showDatePickerDialog());

        // Nhận dữ liệu cũ để hiển thị
        Intent intent = getIntent();
        if (intent != null) {
            etName.setText(intent.getStringExtra("name"));
            etDob.setText(intent.getStringExtra("dob"));
            etPhone.setText(intent.getStringExtra("phone"));
            etAddress.setText(intent.getStringExtra("address"));
            etEmail.setText(intent.getStringExtra("email"));
            
            String gender = intent.getStringExtra("gender");
            if ("Nam".equals(gender)) {
                rbMale.setChecked(true);
                lastCheckedId = R.id.rbEditMale;
            } else if ("Nữ".equals(gender)) {
                rbFemale.setChecked(true);
                lastCheckedId = R.id.rbEditFemale;
            }
        }

        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener toggleListener = v -> {
            int currentId = v.getId();
            if (currentId == lastCheckedId) {
                radioGroupGender.clearCheck();
                lastCheckedId = -1;
            } else {
                lastCheckedId = currentId;
            }
        };
        rbMale.setOnClickListener(toggleListener);
        rbFemale.setOnClickListener(toggleListener);

        // Nút Cập nhật
        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (validateData(name, dob, email, phone, address)) {
                String gender = rbMale.isChecked() ? "Nam" : (rbFemale.isChecked() ? "Nữ" : "");

                // LƯU VÀO SHAREDPREFERENCES
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.putString("dob", dob);
                editor.putString("phone", phone);
                editor.putString("address", address);
                editor.putString("email", email);
                editor.putString("gender", gender);
                editor.apply();

                // Trả kết quả về qua Intent
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("dob", dob);
                resultIntent.putExtra("phone", phone);
                resultIntent.putExtra("address", address);
                resultIntent.putExtra("email", email);
                resultIntent.putExtra("gender", gender);
                setResult(RESULT_OK, resultIntent);
                
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etDob.setText(sdf.format(new Date(selection)));
            tilDate.setErrorEnabled(false);
        });
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private boolean validateData(String name, String dob, String email, String phone, String address) {
        boolean isValid = true;

        if (name.isEmpty()) {
            tilName.setError("Vui lòng nhập họ tên");
            isValid = false;
        } else {
            tilName.setErrorEnabled(false);
        }

        if (dob.isEmpty()) {
            tilDate.setError("Vui lòng chọn ngày sinh");
            isValid = false;
        } else {
            tilDate.setErrorEnabled(false);
        }

        // --- VALIDATE SỐ ĐIỆN THOẠI ---
        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            isValid = false;
        } else if (!phone.matches("^0[35789][0-9]{8}$")) {
            tilPhone.setError("Số điện thoại không hợp lệ (10 số, bắt đầu: 03,05,07,08,09)");
            isValid = false;
        } else {
            tilPhone.setErrorEnabled(false);
        }

        if (address.isEmpty()) {
            tilAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        } else {
            tilAddress.setErrorEnabled(false);
        }

        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            isValid = false;
        } else {
            tilEmail.setErrorEnabled(false);
        }

        if (!rbMale.isChecked() && !rbFemale.isChecked()) {
            tilGender.setError("Vui lòng chọn giới tính");
            isValid = false;
        } else {
            tilGender.setErrorEnabled(false);
        }

        return isValid;
    }
}
