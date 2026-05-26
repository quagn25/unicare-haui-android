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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.haui.UniCare.R;
import com.haui.UniCare.feature.auth.ui.LoginActivity;

public class PersonFragment extends Fragment {

    private TextView tvUserName;
    private ImageView imgAvatarPerson;
    private View btnEditAvatar;
    
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvUserName = view.findViewById(R.id.tv_user_name);
        imgAvatarPerson = view.findViewById(R.id.imgAvatarPerson);
        btnEditAvatar = view.findViewById(R.id.btn_edit_avatar);
        
        Button btnLogout = view.findViewById(R.id.btn_logout);
        View layoutShareApp = view.findViewById(R.id.layout_share_app);
        View layoutChangePassword = view.findViewById(R.id.layout_change_password);
        View layoutDeleteAccount = view.findViewById(R.id.layout_delete_account);
        View layoutProfileHeader = view.findViewById(R.id.layoutProfileHeader);

        displayUserInfo();

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    displayUserInfo();
                    swipeRefreshLayout.setRefreshing(false);
                }, 1000);
            });
        }

        if (imgAvatarPerson != null) {
            imgAvatarPerson.setOnClickListener(v -> openImagePicker());
        }
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnClickListener(v -> openImagePicker());
        }

        if (layoutProfileHeader != null) {
            layoutProfileHeader.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.patients.profile.ui.ProfileActivity.class);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                android.app.Dialog dialog = new android.app.Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_custom_confirm);
                
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                android.view.View btnCloseIcon = dialog.findViewById(R.id.btnCloseIcon);
                if (btnCloseIcon != null) {
                    btnCloseIcon.setOnClickListener(btnV -> dialog.dismiss());
                }

                android.widget.TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
                tvTitle.setText("Đăng xuất?");
                
                android.widget.TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
                tvMessage.setText("Bạn có chắc chắn muốn đăng xuất không?");

                com.google.android.material.button.MaterialButton btnLogoutAction = dialog.findViewById(R.id.btnPrimary);
                btnLogoutAction.setText("Đăng xuất");
                btnLogoutAction.setOnClickListener(btnV -> {
                    dialog.dismiss();
                    SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                    sharedPref.edit().clear().apply();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                });

                com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
                btnCancel.setText("Đóng");
                btnCancel.setOnClickListener(btnV -> dialog.dismiss());

                dialog.show();
            });
        }

        if (layoutShareApp != null) {
            layoutShareApp.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Truy cập UniCare ngay tại: https://unicare.haui.edu.vn");
                startActivity(Intent.createChooser(intent, "Chia sẻ qua:"));
            });
        }

        if (layoutChangePassword != null) {
            layoutChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.auth.ui.ChangePasswordActivity.class);
                startActivity(intent);
            });
        }
        
        if (layoutDeleteAccount != null) {
            layoutDeleteAccount.setOnClickListener(v -> {
                android.app.Dialog dialog = new android.app.Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_custom_confirm);
                
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                android.view.View btnCloseIcon = dialog.findViewById(R.id.btnCloseIcon);
                if (btnCloseIcon != null) {
                    btnCloseIcon.setOnClickListener(btnV -> dialog.dismiss());
                }

                android.widget.TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
                tvTitle.setText("Xóa tài khoản?");
                
                android.widget.TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
                tvMessage.setText("Hành động này không thể hoàn tác. Bạn có chắc muốn tiếp tục?");

                com.google.android.material.button.MaterialButton btnDelete = dialog.findViewById(R.id.btnPrimary);
                btnDelete.setText("Xóa");
                btnDelete.setOnClickListener(btnV -> {
                    dialog.dismiss();
                    if (com.haui.UniCare.core.utils.AppConstants.USE_MOCK_DATA) {
                        Toast.makeText(getContext(), "Đã xóa tài khoản (Mock Mode)", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                        sharedPref.edit().clear().apply();
                        Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.auth.ui.WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
                        int userId = sharedPref.getInt("userId", -1);
                        if (userId != -1) {
                            com.haui.UniCare.core.network.ApiService apiService = com.haui.UniCare.core.network.RetrofitClient.getInstance().create(com.haui.UniCare.core.network.ApiService.class);
                            java.util.Map<String, Integer> body = new java.util.HashMap<>();
                            body.put("userId", userId);
                            apiService.deleteAccount(body).enqueue(new retrofit2.Callback<com.haui.UniCare.data.model.GenericResponse>() {
                                @Override
                                public void onResponse(retrofit2.Call<com.haui.UniCare.data.model.GenericResponse> call, retrofit2.Response<com.haui.UniCare.data.model.GenericResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                                        sharedPref.edit().clear().apply();
                                        Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.auth.ui.WelcomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    } else {
                                        Toast.makeText(getContext(), "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<com.haui.UniCare.data.model.GenericResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
                btnCancel.setText("Đóng");
                btnCancel.setOnClickListener(btnV -> dialog.dismiss());

                dialog.show();
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
