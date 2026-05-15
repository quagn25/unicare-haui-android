package com.haui.UniCare.feature.patients.doctor.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.doctor.adapter.DoctorAdapter;

import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.MockData;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorListActivity extends BaseActivity {

    private RecyclerView rcvDoctors;
    private DoctorAdapter doctorAdapter;
    private EditText etSearchDoctor;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        mapping();
        setupRecyclerView();
        setupSearch();
        
        btnBack.setOnClickListener(v -> finish());

        // Gọi API lấy dữ liệu từ server (Hoặc dùng MockData nếu là bản Debug)
        loadDoctors();
    }

    private void loadDoctors() {
        if (AppConstants.USE_MOCK_DATA) {
            List<Doctor> doctors = MockData.getMockDoctors();
            displayDoctors(doctors);
        } else {
            fetchDoctorsFromServer();
        }
    }

    private void displayDoctors(List<Doctor> doctors) {
        // Kiểm tra xem có yêu cầu lọc theo chuyên khoa không
        String specialtyName = getIntent().getStringExtra("specialty_name");
        if (specialtyName != null && !specialtyName.isEmpty()) {
            List<Doctor> filtered = new ArrayList<>();
            for (Doctor d : doctors) {
                if (d.getSpecialties().toLowerCase().contains(specialtyName.toLowerCase())) {
                    filtered.add(d);
                }
            }
            doctorAdapter.updateList(filtered);
            if (filtered.isEmpty()) {
                Toast.makeText(this, "Không có bác sĩ chuyên khoa " + specialtyName, Toast.LENGTH_SHORT).show();
            }
        } else {
            doctorAdapter.updateList(doctors);
        }
    }

    private void mapping() {
        rcvDoctors = findViewById(R.id.rcv_doctors);
        etSearchDoctor = findViewById(R.id.etSearchDoctor);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDoctors.setLayoutManager(linearLayoutManager);

        // Khởi tạo adapter với danh sách trống trước
        doctorAdapter = new DoctorAdapter(new ArrayList<>());
        rcvDoctors.setAdapter(doctorAdapter);
    }

    private void fetchDoctorsFromServer() {
        showLoadingDialog();

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getDoctors().enqueue(new Callback<List<Doctor>>() {
            @Override
            public void onResponse(Call<List<Doctor>> call, Response<List<Doctor>> response) {
                hideLoadingDialog();
                if (response.isSuccessful() && response.body() != null) {
                    displayDoctors(response.body());
                } else {
                    Toast.makeText(DoctorListActivity.this, "Không thể lấy danh sách bác sĩ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Doctor>> call, Throwable t) {
                hideLoadingDialog();
                Log.e("DoctorList", "Error: " + t.getMessage());
                Toast.makeText(DoctorListActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        etSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (doctorAdapter != null) {
                    doctorAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
