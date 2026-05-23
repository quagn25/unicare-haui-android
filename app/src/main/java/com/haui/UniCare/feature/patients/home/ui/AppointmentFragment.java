package com.haui.UniCare.feature.patients.home.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorDetailActivity;
import com.haui.UniCare.feature.patients.appointment.viewmodel.BookVaccineActivity;
import com.haui.UniCare.feature.patients.appointment.viewmodel.BookAppointmentActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.haui.UniCare.R;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.MockData;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.Notification;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.feature.patients.appointment.adapter.AppointmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFragment extends Fragment implements AppointmentAdapter.OnAppointmentActionListener {

    private LinearLayout layoutEmptyState;
    private RecyclerView rcvAppointments;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    private LinearLayout layoutTabUpcoming, layoutTabCompleted;
    private TextView tvTabUpcoming, tvTabCompleted, tvRegisterButton;
    private ImageView imgTabUpcoming, imgTabCompleted;
    private LinearLayout btnRegisterVaccine;

    private String currentTab = "Đặt lịch khám"; 

    public AppointmentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        initViews(view);
        setupRecyclerView();
        setupEvents();

        if (getActivity() != null && getActivity().getIntent() != null) {
            String activeTab = getActivity().getIntent().getStringExtra("active_tab");
            if ("vaccine".equals(activeTab)) {
                currentTab = "Lịch tiêm";
                getActivity().getIntent().removeExtra("active_tab");
            }
        }
        updateTabUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews(View view) {
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        rcvAppointments = view.findViewById(R.id.rcv_appointments);
        layoutTabUpcoming = view.findViewById(R.id.layout_tab_upcoming);
        layoutTabCompleted = view.findViewById(R.id.layout_tab_completed);
        tvTabUpcoming = view.findViewById(R.id.tv_tab_upcoming);
        tvTabCompleted = view.findViewById(R.id.tv_tab_completed);
        imgTabUpcoming = view.findViewById(R.id.img_tab_upcoming);
        imgTabCompleted = view.findViewById(R.id.img_tab_completed);
        btnRegisterVaccine = view.findViewById(R.id.btn_register_vaccine);
        tvRegisterButton = view.findViewById(R.id.tv_register_button);
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(appointmentList);
        adapter.setActionListener(this);
        rcvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvAppointments.setAdapter(adapter);
    }

    private void setupEvents() {
        layoutTabUpcoming.setOnClickListener(v -> {
            if (!currentTab.equals("Đặt lịch khám")) {
                currentTab = "Đặt lịch khám";
                updateTabUI();
                loadData();
            }
        });

        layoutTabCompleted.setOnClickListener(v -> {
            if (!currentTab.equals("Lịch tiêm")) {
                currentTab = "Lịch tiêm";
                updateTabUI();
                loadData();
            }
        });

        btnRegisterVaccine.setOnClickListener(v -> {
            if (currentTab.equals("Đặt lịch khám")) {
                startActivity(new Intent(getContext(), com.haui.UniCare.feature.patients.doctor.ui.DoctorListActivity.class));
            } else {
                startActivity(new Intent(getContext(), BookVaccineActivity.class));
            }
        });
    }

    private void updateTabUI() {
        if (currentTab.equals("Đặt lịch khám")) {
            layoutTabUpcoming.setBackgroundResource(R.drawable.bg_tab_active);
            layoutTabCompleted.setBackgroundResource(android.R.color.transparent);
            tvTabUpcoming.setTextColor(Color.WHITE);
            tvTabCompleted.setTextColor(Color.parseColor("#6B7280"));
            imgTabUpcoming.setColorFilter(Color.WHITE);
            imgTabCompleted.setColorFilter(Color.parseColor("#6B7280"));
            tvRegisterButton.setText("+ Đặt lịch khám mới");
        } else {
            layoutTabUpcoming.setBackgroundResource(android.R.color.transparent);
            layoutTabCompleted.setBackgroundResource(R.drawable.bg_tab_active);
            tvTabUpcoming.setTextColor(Color.parseColor("#6B7280"));
            tvTabCompleted.setTextColor(Color.WHITE);
            imgTabUpcoming.setColorFilter(Color.parseColor("#6B7280"));
            imgTabCompleted.setColorFilter(Color.WHITE);
            tvRegisterButton.setText("+ Đăng ký mũi tiêm mới");
        }
    }

    private void loadData() {
        if (AppConstants.USE_MOCK_DATA) {
            filterAndSetData(new ArrayList<>());
        } else {
            fetchAppointmentsFromServer();
        }
    }

    private void fetchAppointmentsFromServer() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = requireActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int userId = sharedPref.getInt("userId", -1);

        if (userId == -1) {
            filterAndSetData(new ArrayList<>());
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAppointments(userId).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                List<Appointment> serverList = response.isSuccessful() && response.body() != null ? response.body() : new ArrayList<>();
                filterAndSetData(serverList);
            }
            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                filterAndSetData(new ArrayList<>());
            }
        });
    }

    private void filterAndSetData(List<Appointment> serverList) {
        appointmentList.clear();
        
        if (AppConstants.USE_MOCK_DATA) {
            List<Appointment> allMocks = MockData.getMockAppointments();
            for (Appointment app : allMocks) {
                addIfMatchesTab(app);
            }
        }

        for (Appointment serverApp : serverList) {
            boolean isDuplicate = false;
            for (Appointment existing : appointmentList) {
                if (existing.id == serverApp.id && existing.id != 0) {
                    isDuplicate = true; break;
                }
            }
            if (!isDuplicate) addIfMatchesTab(serverApp);
        }
        
        adapter.setData(appointmentList);
        if (appointmentList.isEmpty()) showEmptyState(); else showDataState();
    }

    private void addIfMatchesTab(Appointment app) {
        boolean isVaccine = (app.doctorName != null && app.doctorName.startsWith("Vắc-xin")) 
                         || (app.note != null && app.note.contains("Tiêm chủng"));
        
        if (currentTab.equals("Đặt lịch khám")) {
            if (!isVaccine) appointmentList.add(app);
        } else {
            if (isVaccine) appointmentList.add(app);
        }
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        rcvAppointments.setVisibility(View.GONE);
    }

    private void showDataState() {
        layoutEmptyState.setVisibility(View.GONE);
        rcvAppointments.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReschedule(Appointment appointment) {
        boolean isVaccine = (appointment.doctorName != null && appointment.doctorName.startsWith("Vắc-xin"));
        Intent intent;
        if (isVaccine) {
            intent = new Intent(getContext(), BookVaccineActivity.class);
            intent.putExtra("selected_vaccine_name", appointment.doctorName);
        } else {
            intent = new Intent(getContext(), BookAppointmentActivity.class);
            Doctor doctor = new Doctor();
            doctor.setId(appointment.doctorId);
            doctor.setName(appointment.doctorName);
            doctor.setDegree(appointment.doctorTitle);
            intent.putExtra("doctor_data", doctor);
        }
        intent.putExtra("reschedule_appointment_id", appointment.id);
        startActivity(intent);
    }

    @Override
    public void onCancel(Appointment appointment) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn này không?")
                .setNegativeButton("Quay lại", null)
                .setPositiveButton("Hủy lịch", (dialog, which) -> {
                    performCancelAppointment(appointment);
                })
                .show();
    }

    private void performCancelAppointment(Appointment appointment) {
        if (!AppConstants.USE_MOCK_DATA) {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Map<String, Integer> body = new HashMap<>();
            body.put("appointmentId", appointment.id);
            
            apiService.cancelAppointment(body).enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã hủy lịch thành công", Toast.LENGTH_SHORT).show();
                        loadData();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi hủy lịch", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            MockData.removeMockAppointment(appointment.id);
            
            // Thêm thông báo giả vào tab thông báo
            Notification n = new Notification();
            n.setTitle("Lịch hẹn đã hủy");
            String itemType = (appointment.doctorName != null && appointment.doctorName.startsWith("Vắc-xin")) ? "tiêm chủng" : "khám bệnh";
            n.setContent("Bạn đã hủy thành công lịch " + itemType + " (" + appointment.doctorName + ") vào ngày " + appointment.appointmentDatetime);
            n.setType("LICH_KHAM");
            MockData.addMockNotification(n);

            Toast.makeText(getContext(), "Đã hủy lịch và gửi thông báo (Mock Mode)", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }
}
