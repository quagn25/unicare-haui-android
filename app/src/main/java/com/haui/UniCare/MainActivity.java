package com.haui.UniCare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.haui.UniCare.feature.patients.home.ui.HomeFragment;
import com.haui.UniCare.feature.auth.ui.LoginActivity;
import com.haui.UniCare.feature.patients.home.ui.NotificationFragment;
import com.haui.UniCare.feature.patients.home.ui.PersonFragment;
import com.haui.UniCare.feature.patients.home.ui.AppointmentFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. KIỂM TRA PHIÊN ĐĂNG NHẬP (Lưu trong UniCarePrefs)
        SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
        String username = sharedPref.getString("username", "");

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 2. THIẾT LẬP GIAO DIỆN CHÍNH
        EdgeToEdge.enable(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            getWindow().setNavigationBarDividerColor(android.graphics.Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        // Xử lý nút Back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Bỏ systemBars.bottom để BottomNav tràn xuống dưới
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        setupBottomNavigation();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && "schedule".equals(intent.getStringExtra("select_tab"))) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_schedule);
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_schedule) {
                selectedFragment = new AppointmentFragment();
            } else if (id == R.id.nav_notifications) {
                selectedFragment = new NotificationFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new PersonFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn muốn đóng ứng dụng hay đăng xuất khỏi hệ thống?")
                .setPositiveButton("Thoát App", (dialog, which) -> finish())
                .setNeutralButton("Đăng xuất", (dialog, which) -> {
                    SharedPreferences sharedPref = getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                    sharedPref.edit().clear().apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
