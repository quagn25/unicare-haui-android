package com.haui.UniCare.feature.patients.home.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.haui.UniCare.R;
import com.haui.UniCare.feature.auth.ui.LoginActivity;

public class PersonFragment extends Fragment {

    private TextView tvUserName;
    private ImageView imgAvatarPerson;
    private View btnEditAvatar;

    // Launcher to pick image from gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        updateAvatar(selectedImageUri);
                    }
                }
            }
    );

    public PersonFragment() {
        // Required empty public constructor
    }

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mapping views
        tvUserName = view.findViewById(R.id.tv_user_name);
        imgAvatarPerson = view.findViewById(R.id.imgAvatarPerson);
        btnEditAvatar = view.findViewById(R.id.btn_edit_avatar);
        
        Button btnLogout = view.findViewById(R.id.btn_logout);
        View layoutShareApp = view.findViewById(R.id.layout_share_app);
        View layoutChangePassword = view.findViewById(R.id.layout_change_password);
        View layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);
        View layoutProfileHeader = view.findViewById(R.id.layoutProfileHeader);

        // Display user info from SharedPreferences
        displayUserInfo();

        // Image picker click events
        if (imgAvatarPerson != null) {
            imgAvatarPerson.setOnClickListener(v -> openImagePicker());
        }
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v -> openImagePicker());
        }

        // Navigate to profile details
        if (layoutProfileHeader != null) {
            layoutProfileHeader.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.patients.profile.ui.ProfileActivity.class);
                startActivity(intent);
            });
        }

        // Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                            sharedPref.edit().clear().apply();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            requireActivity().finish();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }

        // Share app
        if (layoutShareApp != null) {
            layoutShareApp.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Truy cập UniCare ngay tại: https://unicare.haui.edu.vn");
                startActivity(Intent.createChooser(intent, "Chia sẻ qua:"));
            });
        }

        // Placeholder actions
        if (layoutChangePassword != null) {
            layoutChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.auth.ui.ChangePasswordActivity.class);
                startActivity(intent);
            });
        }
        if (layoutDeleteAccount != null) {
            layoutDeleteAccount.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Xóa tài khoản")
                        .setMessage("Bạn có chắc chắn muốn xóa tài khoản không? Hành động này không thể hoàn tác.")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            Toast.makeText(getContext(), "Yêu cầu xóa tài khoản đã được ghi nhận", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateAvatar(Uri imageUri) {
        if (imgAvatarPerson != null) {
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .placeholder(R.drawable.default_avt)
                    .into(imgAvatarPerson);
            
            // Persist the image URI locally (optional: upload to server)
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            sharedPref.edit().putString("avatarUri", imageUri.toString()).apply();
            
            Toast.makeText(getContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo();
    }

    private void displayUserInfo() {
        if (getContext() != null && tvUserName != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            String fullName = sharedPref.getString("fullName", "Người dùng");
            tvUserName.setText(fullName);
            
            String avatarUriStr = sharedPref.getString("avatarUri", null);
            if (avatarUriStr != null && imgAvatarPerson != null) {
                Glide.with(this)
                        .load(Uri.parse(avatarUriStr))
                        .circleCrop()
                        .placeholder(R.drawable.default_avt)
                        .into(imgAvatarPerson);
            }
        }
    }
}
