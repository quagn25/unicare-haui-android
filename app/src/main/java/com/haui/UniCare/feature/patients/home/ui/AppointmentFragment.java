package com.haui.UniCare.feature.patients.home.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.feature.patients.appointment.adapter.AppointmentAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentFragment extends Fragment {

    private LinearLayout layoutEmptyState;
    private RecyclerView rcvAppointments;
    private EditText edtSearch;
    private ImageView btnFilter;
    private ImageButton btnBack;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList = new ArrayList<>();

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại fragment
        loadData();
    }

    private void initViews(View view) {
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        rcvAppointments = view.findViewById(R.id.rcv_appointments);
        edtSearch = view.findViewById(R.id.edt_search);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(appointmentList);
        rcvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvAppointments.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mở bộ lọc", Toast.LENGTH_SHORT).show();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter logic can be implemented here
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
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
            showEmptyState();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getAppointments(patientId).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentList = response.body();
                    adapter.setData(appointmentList);
                    
                    if (appointmentList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showDataState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e("AppointmentFragment", "Error: " + t.getMessage());
                showEmptyState();
            }
        });
    }

    private void loadSampleData() {
        appointmentList.clear();
        Appointment a1 = new Appointment(1, 1, 1, "2026-05-20 09:00:00", "Sắp tới", "2026-05-15");
        a1.doctorName = "PGS.TS Nguyễn Văn An";
        a1.specialtyName = "Tim mạch";
        a1.workplaceAddress = "Bệnh viện Bạch Mai, Hà Nội";
        a1.consultationFee = 500000;
        
        Appointment a2 = new Appointment(2, 1, 2, "2026-05-25 14:30:00", "Chờ xác nhận", "2026-05-15");
        a2.doctorName = "ThS.BS Trần Thu Hà";
        a2.specialtyName = "Nhi khoa";
        a2.workplaceAddress = "Bệnh viện Nhi Trung ương";
        a2.consultationFee = 300000;

        appointmentList.add(a1);
        appointmentList.add(a2);
        
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
}
