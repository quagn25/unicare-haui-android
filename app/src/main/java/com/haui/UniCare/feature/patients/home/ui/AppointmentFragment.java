package com.haui.UniCare.feature.patients.home.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.haui.UniCare.R;

public class AppointmentFragment extends Fragment {

    // Khai báo các thành phần giao diện
    private LinearLayout layoutEmptyState;
    private RecyclerView rcvAppointments;
    private EditText edtSearch;
    private ImageView btnFilter;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout từ file XML (fragment_appointment.xml)
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        // 1. Ánh xạ các View từ XML sang Java
        initViews(view);

        // 2. Cài đặt các sự kiện (Click, Gõ phím...)
        setupEvents();

        // 3. Giả lập trạng thái hiển thị màn hình trống (giống trong ảnh)
        showEmptyState();

        return view;
    }

    private void initViews(View view) {
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        rcvAppointments = view.findViewById(R.id.rcv_appointments);
        edtSearch = view.findViewById(R.id.edt_search);
        btnFilter = view.findViewById(R.id.btn_filter);
    }

    private void setupEvents() {
        // Sự kiện khi bấm vào nút Lọc (Cái phễu)
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở bộ lọc", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện khi người dùng gõ vào thanh tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Sau này sẽ gọi adapter.filter(s) ở đây để lọc danh sách
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // Hàm dùng để hiển thị giao diện "Bạn chưa có lịch khám"
    private void showEmptyState() {
        if (layoutEmptyState != null && rcvAppointments != null) {
            layoutEmptyState.setVisibility(View.VISIBLE); // Hiện ảnh trống
            rcvAppointments.setVisibility(View.GONE);     // Ẩn danh sách
        }
    }

    // Hàm (dự phòng) sau này dùng để hiển thị danh sách khi gọi API thành công
    private void showDataState() {
        if (layoutEmptyState != null && rcvAppointments != null) {
            layoutEmptyState.setVisibility(View.GONE);     // Ẩn ảnh trống
            rcvAppointments.setVisibility(View.VISIBLE);   // Hiện danh sách
        }
    }
}