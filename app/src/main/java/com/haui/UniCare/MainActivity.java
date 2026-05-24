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
        if (intent != null) {
            String selectTab = intent.getStringExtra("select_tab");
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            if (bottomNav != null && selectTab != null) {
                if ("schedule".equals(selectTab)) {
                    bottomNav.setSelectedItemId(R.id.nav_schedule);
                } else if ("profile".equals(selectTab)) {
                    bottomNav.setSelectedItemId(R.id.nav_profile);
                } else if ("notifications".equals(selectTab)) {
                    bottomNav.setSelectedItemId(R.id.nav_notifications);
                } else if ("home".equals(selectTab)) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                }
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

            // Remove badge when switching to notifications tab
            if (id == R.id.nav_notifications) {
                bottomNav.removeBadge(R.id.nav_notifications);
            }

            return true;
        });

        // Set mock unread notification badge
        com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_notifications);
        badge.setVisible(true);
        badge.setNumber(3); // Mock number of unread notifications
        badge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        badge.setBadgeTextColor(getResources().getColor(android.R.color.white));
    }
    private void showExitDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_custom_confirm);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        android.view.View btnCloseIcon = dialog.findViewById(R.id.btnCloseIcon);
        if (btnCloseIcon != null) {
            btnCloseIcon.setOnClickListener(v -> dialog.dismiss());
        }

        android.widget.TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Thoát ứng dụng?");

        android.widget.TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText("Bạn có chắc chắn muốn thoát ứng dụng không?");

        com.google.android.material.button.MaterialButton btnExit = dialog.findViewById(R.id.btnPrimary);
        btnExit.setText("Thoát App");
        btnExit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EF4444")));
        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        com.google.android.material.button.MaterialButton btnLogout = dialog.findViewById(R.id.btnSecondary);
        btnLogout.setVisibility(android.view.View.GONE);

        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Đóng");
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
