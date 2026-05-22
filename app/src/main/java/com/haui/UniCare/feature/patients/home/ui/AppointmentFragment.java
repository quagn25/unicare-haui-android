package com.haui.UniCare.feature.patients.home.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorDetailActivity;
import com.haui.UniCare.feature.patients.appointment.viewmodel.BookVaccineActivity;
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
import com.haui.UniCare.data.model.GenericResponse;
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

    // Tab Switcher Views
    private LinearLayout layoutTabUpcoming;
    private LinearLayout layoutTabCompleted;
    private TextView tvTabUpcoming;
    private TextView tvTabCompleted;
    private ImageView imgTabUpcoming;
    private ImageView imgTabCompleted;
    
    // Bottom Register Button
    private LinearLayout btnRegisterVaccine;
    private TextView tvRegisterButton;

    private String currentTab = "Đặt lịch khám"; // Tab hiện tại ("Đặt lịch khám" hoặc "Lịch tiêm")

    public AppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        initViews(view);
        setupRecyclerView();
        setupEvents();
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
        // Event tab Đặt lịch khám
        layoutTabUpcoming.setOnClickListener(v -> {
            if (!currentTab.equals("Đặt lịch khám")) {
                currentTab = "Đặt lịch khám";
                updateTabUI();
                loadData();
            }
        });

        // Event tab Lịch tiêm
        layoutTabCompleted.setOnClickListener(v -> {
            if (!currentTab.equals("Lịch tiêm")) {
                currentTab = "Lịch tiêm";
                updateTabUI();
                loadData();
            }
        });

        // Event nút đăng ký lịch mới
        btnRegisterVaccine.setOnClickListener(v -> {
            if (currentTab.equals("Đặt lịch khám")) {
                Toast.makeText(getContext(), "Đang chuyển sang trang Đặt lịch khám...", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getContext(), BookVaccineActivity.class);
                startActivity(intent);
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
            loadSampleData();
        } else {
            fetchAppointmentsFromServer();
        }
    }

    private void fetchAppointmentsFromServer() {
        if (getContext() == null) return;

        SharedPreferences sharedPref = getActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int patientId = sharedPref.getInt("userId", -1);

        if (patientId == -1) {
            loadSampleData(); // Fallback to mock data if not logged in
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAppointments(patientId).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Appointment> serverList = response.body();
                    appointmentList.clear();
                    
                    for (Appointment app : serverList) {
                        boolean isVaccine = app.doctorName != null && app.doctorName.startsWith("Vắc-xin");
                        if (currentTab.equals("Đặt lịch khám")) {
                            if (!isVaccine) {
                                appointmentList.add(app);
                            }
                        } else {
                            if (isVaccine) {
                                appointmentList.add(app);
                            }
                        }
                    }
                    
                    adapter.setData(appointmentList);
                    if (appointmentList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showDataState();
                    }
                } else {
                    loadSampleData();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e("AppointmentFragment", "Error: " + t.getMessage());
                loadSampleData();
            }
        });
    }

    private void loadSampleData() {
        appointmentList.clear();
        
        if (currentTab.equals("Đặt lịch khám")) {
            // Khám Tim mạch (Sắp tới)
            Appointment a1 = new Appointment(1, 1, 1, "2026-06-01 09:00:00", "PENDING", "2026-05-15");
            a1.doctorName = "Nguyễn Văn An";
            a1.doctorTitle = "ThS. BS";
            a1.doctorBio = "Tim mạch";
            a1.workplaceAddress = "UniCare - Phòng 105";
            
            // Khám Nha khoa (Sắp tới)
            Appointment a2 = new Appointment(2, 1, 2, "2026-06-02 14:30:00", "PENDING", "2026-05-15");
            a2.doctorName = "Trần Thị Bình";
            a2.doctorTitle = "BS";
            a2.doctorBio = "Nha khoa";
            a2.workplaceAddress = "UniCare - Phòng 203";
            
            // Khám tổng quát (Hoàn tất)
            Appointment a3 = new Appointment(3, 1, 4, "2026-03-12 08:30:00", "COMPLETED", "2026-03-10");
            a3.doctorName = "Phạm Quốc Dũng";
            a3.doctorTitle = "TS. BS";
            a3.doctorBio = "Khám tổng quát";
            a3.workplaceAddress = "UniCare - Phòng 101";
            
            appointmentList.add(a1);
            appointmentList.add(a2);
            appointmentList.add(a3);
        } else {
            // Vắc-xin Cúm mùa (Sắp tới)
            Appointment a1 = new Appointment(4, 1, 21, "2026-05-22 08:30:00", "PENDING", "2026-05-15");
            a1.doctorName = "Vắc-xin Cúm mùa";
            a1.doctorTitle = "Liều nhắc lại";
            a1.workplaceAddress = "Phòng 201 - UniCare";
            
            // Vắc-xin HPV (Sắp tới)
            Appointment a2 = new Appointment(5, 1, 22, "2026-06-10 09:00:00", "PENDING", "2026-05-15");
            a2.doctorName = "Vắc-xin HPV";
            a2.doctorTitle = "Mũi 2/3";
            a2.workplaceAddress = "Phòng 105 - UniCare";
            
            // Vắc-xin Viêm gan B (Sắp tới)
            Appointment a3 = new Appointment(6, 1, 23, "2026-07-05 14:00:00", "PENDING", "2026-05-15");
            a3.doctorName = "Vắc-xin Viêm gan B";
            a3.doctorTitle = "Mũi 3/3";
            a3.workplaceAddress = "Phòng 203 - UniCare";

            // Vắc-xin Covid-19 (Hoàn tất)
            Appointment a4 = new Appointment(7, 1, 24, "2026-03-12 10:00:00", "COMPLETED", "2026-03-10");
            a4.doctorName = "Vắc-xin Covid-19";
            a4.doctorTitle = "Mũi nhắc";
            a4.workplaceAddress = "Phòng 105 - UniCare";

            // Vắc-xin Sởi - Quai bị - Rubella (Hoàn tất)
            Appointment a5 = new Appointment(8, 1, 25, "2026-01-20 08:00:00", "COMPLETED", "2026-01-18");
            a5.doctorName = "Vắc-xin Sởi - Quai bị - Rubella";
            a5.doctorTitle = "Mũi 1";
            a5.workplaceAddress = "Phòng 201 - UniCare";
            
            appointmentList.add(a1);
            appointmentList.add(a2);
            appointmentList.add(a3);
            appointmentList.add(a4);
            appointmentList.add(a5);
        }
        
        adapter.setData(appointmentList);
        showDataState();
    }

    private void showEmptyState() {
        if (layoutEmptyState != null && rcvAppointments != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rcvAppointments.setVisibility(View.GONE);
        }
    }

    private void showDataState() {
        if (layoutEmptyState != null && rcvAppointments != null) {
            layoutEmptyState.setVisibility(View.GONE);
            rcvAppointments.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReschedule(Appointment appointment) {
        if (getContext() == null) return;

        // Reconstruct Doctor object from Appointment data
        Doctor doctor = new Doctor();
        doctor.setId(appointment.doctorId);
        doctor.setName(appointment.doctorName);
        doctor.setDegree(appointment.doctorTitle);
        doctor.setBio(appointment.doctorBio);
        doctor.setAddress(appointment.workplaceAddress);
        doctor.setConsultationFee(appointment.consultationFee);
        doctor.setSpecialties(appointment.specialtyName);

        // Open DoctorDetailActivity with reschedule mode enabled
        Intent intent = new Intent(getContext(), DoctorDetailActivity.class);
        intent.putExtra("doctor_data", doctor);
        intent.putExtra("reschedule_appointment_id", appointment.id);
        startActivity(intent);
    }

    @Override
    public void onCancel(Appointment appointment) {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Huỷ lịch hẹn?")
                .setMessage("Bạn có chắc muốn huỷ lịch hẹn này? Thao tác này không thể hoàn tác.")
                .setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Xác nhận huỷ", (dialog, which) -> {
                    dialog.dismiss();
                    if (AppConstants.USE_MOCK_DATA) {
                        Toast.makeText(getContext(), "Huỷ lịch thành công (Demo)", Toast.LENGTH_SHORT).show();
                    } else {
                        performCancel(appointment.id);
                    }
                })
                .show();
    }

    private void performReschedule(int appointmentId) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("appointmentId", appointmentId);

        apiService.rescheduleAppointment(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đổi lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(getContext(), "Không thể đổi lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performCancel(int appointmentId) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("appointmentId", appointmentId);

        apiService.cancelAppointment(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã huỷ lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(getContext(), "Không thể huỷ lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
