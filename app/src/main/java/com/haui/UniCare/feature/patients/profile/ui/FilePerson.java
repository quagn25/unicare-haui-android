package com.haui.UniCare.feature.patients.profile.ui;

import android.content.Intent;
import android.content.SharedPreferences; // Thêm import này
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.haui.UniCare.R;

public class FilePerson extends AppCompatActivity {
    TextView tvEdit, tvName, tvDob, tvGender, tvPhone, tvAddress, tvEmail;
    ImageButton btnBack;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Khi cập nhật xong quay về, tải lại dữ liệu mới nhất
                    loadDataFromSharedPreferences();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Ánh xạ View
        tvEdit = findViewById(R.id.tvEditProfile);
        tvName = findViewById(R.id.tvProfileName);
        tvDob = findViewById(R.id.tvProfileDob);
        tvGender = findViewById(R.id.tvProfileGender);
        tvPhone = findViewById(R.id.tvProfilePhone);
        tvAddress = findViewById(R.id.tvProfileAddress);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnBack = findViewById(R.id.btnBack);

        // 2. TẢI DỮ LIỆU ĐÃ LƯU KHI MỞ MÀN HÌNH
        loadDataFromSharedPreferences();

        tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(FilePerson.this, EditProfilePerson.class);
            intent.putExtra("name", tvName.getText().toString());
            intent.putExtra("dob", tvDob.getText().toString());
            intent.putExtra("gender", tvGender.getText().toString());
            intent.putExtra("phone", tvPhone.getText().toString());
            intent.putExtra("address", tvAddress.getText().toString());
            intent.putExtra("email", tvEmail.getText().toString());
            editProfileLauncher.launch(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // Hàm đọc dữ liệu từ SharedPreferences
    private void loadDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // getString(key, giá_trị_mặc_định)
        tvName.setText(sharedPreferences.getString("name", "Nguyễn Văn An"));
        tvDob.setText(sharedPreferences.getString("dob", "09/11/2005"));
        tvGender.setText(sharedPreferences.getString("gender", "Nam"));
        tvPhone.setText(sharedPreferences.getString("phone", "0392817228"));
        tvAddress.setText(sharedPreferences.getString("address", "Chưa cập nhật"));
        tvEmail.setText(sharedPreferences.getString("email", "Chưa cập nhật"));
    }
}
