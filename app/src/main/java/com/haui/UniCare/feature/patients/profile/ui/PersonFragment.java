package com.haui.UniCare.feature.patients.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.haui.UniCare.R;
import com.haui.UniCare.feature.auth.ui.LoginActivity;

public class PersonFragment extends Fragment {

    private Button btnLogout;
    private View layoutShareApp;
    private View layoutChangePassword;
    private TextView tvUserName;
    private TextView tvHealthRecords;
    private View layoutDeleteAccount;

    public PersonFragment() {
        // Required empty public constructor
    }

    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View chính xác theo ID trong fragment_person.xml
        btnLogout = view.findViewById(R.id.btn_logout);
        layoutShareApp = view.findViewById(R.id.layout_share_app);
        layoutChangePassword = view.findViewById(R.id.layout_change_password);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvHealthRecords = view.findViewById(R.id.tv_health_records);
        layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);

        displayUserInfo();

        // 1. Sự kiện Đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            requireActivity().finish();
        });

        // 2. Sự kiện Chia sẻ ứng dụng
        layoutShareApp.setOnClickListener(v -> {
            String packageName = requireContext().getPackageName();
            String deepLink = "unicare://app";
            String shareMessage = "Truy cập UniCare ngay tại: " + deepLink + packageName;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(intent, "Chia sẻ qua:"));
        });

        // 3. Sự kiện Đổi mật khẩu
        layoutChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng Đổi mật khẩu đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        // 4. Sự kiện Xem thông tin cá nhân (Mở FilePerson)
        tvHealthRecords.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilePerson.class);
            startActivity(intent);
        });

        // 5. Sự kiện Yêu cầu xóa tài khoản
        layoutDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Yêu cầu xóa tài khoản đang được tiếp nhận", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayUserInfo() {
        if (getContext() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            String fullName = sharedPref.getString("fullName", "Người dùng");
            tvUserName.setText(fullName);
        }
    }
}
