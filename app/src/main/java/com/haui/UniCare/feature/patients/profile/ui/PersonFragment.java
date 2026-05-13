package com.haui.UniCare.feature.patients.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.haui.UniCare.R;
import com.haui.UniCare.feature.auth.ui.LoginActivity;
import com.haui.UniCare.feature.patients.profile.viewmodel.ChangePass;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment {
    Button btnSignOut;
    TextView tvShareApp,tvChangePass,tvUserNameHome;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ánh xạ View (Logic xử lý ở đây)
        btnSignOut = view.findViewById(R.id.button2);
        tvShareApp = view.findViewById(R.id.textView9);
        tvChangePass = view.findViewById(R.id.textView10);
        tvUserNameHome = view.findViewById(R.id.textView);
        displayUserInfo();
        // 2. Bắt sự kiện Click
        btnSignOut.setOnClickListener(v -> {
            // 1. Xóa trạng thái đăng nhập trong SharedPreferences
            SharedPreferences sharedPref = requireActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear(); // Xóa sạch dữ liệu (hoặc dùng editor.putBoolean("isLoggedIn", false))
            editor.apply();

            // 2. Chuyển hướng về màn hình Đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // 3. Đóng Activity hiện tại
            requireActivity().finish();
        });
        tvShareApp.setOnClickListener(v -> {
            String packageName = requireContext().getPackageName();
            String deepLink = "unicare://app";
            String shareMessage = "Truy cập UniCare ngay tại: " + deepLink  + packageName;
            
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(intent, "Chia sẻ qua:"));
        });
        tvChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePass.class);
            startActivity(intent);
        });
    }
    private void displayUserInfo() {
        if (getContext() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            String fullName = sharedPref.getString("fullName", "Người dùng");
            tvUserNameHome.setText(fullName);
        }
    }
}