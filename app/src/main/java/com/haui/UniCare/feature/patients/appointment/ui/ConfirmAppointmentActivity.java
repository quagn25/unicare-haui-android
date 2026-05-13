package com.haui.UniCare.feature.patients.appointment.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.haui.UniCare.R;
import com.haui.UniCare.core.base.BaseActivity;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.databinding.ActivityConfirmAppointmentBinding;
import com.haui.UniCare.feature.patients.appointment.viewmodel.AppointmentViewModel;

public class ConfirmAppointmentActivity extends BaseActivity {

    private ActivityConfirmAppointmentBinding binding;
    private AppointmentViewModel viewModel;
    private Doctor selectedDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Nhận dữ liệu từ BookAppointmentActivity
        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");
        String selectedDate = getIntent().getStringExtra("selected_date");
        String selectedTime = getIntent().getStringExtra("selected_time");
        boolean isMorning = getIntent().getBooleanExtra("is_morning", true);

        // Hiển thị thông tin bác sĩ
        if (selectedDoctor != null) {
            binding.tvConfirmDoctorName.setText(selectedDoctor.getName());
            binding.tvConfirmSpecialty.setText(selectedDoctor.getSpecialties());
            
            Glide.with(this)
                    .load(selectedDoctor.getAvatarUrl())
                    .placeholder(R.drawable.doctorbook)
                    .into(binding.imgConfirmDoctor);
        }

        // Hiển thị thông tin lịch khám
        binding.tvConfirmDate.setText(selectedDate);
        binding.tvConfirmTime.setText(selectedTime + (isMorning ? " (Sáng)" : " (Chiều)"));

        // Nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Nút "Xác nhận đặt lịch"
        binding.btnConfirmBook.setOnClickListener(v -> {
            if (selectedDoctor != null) {
                // Giả sử patientId = 1 (cần lấy từ Session/Auth sau này)
                int currentPatientId = 1;
                viewModel.createAppointment(currentPatientId, selectedDoctor.getId());
            } else {
                Toast.makeText(this, "Thiếu thông tin bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát kết quả từ ViewModel
        viewModel.getShowLoading().observe(this, isLoading -> {
            if (isLoading) showLoadingDialog();
            else hideLoadingDialog();
        });

//        // Thêm xử lý thành công nếu cần (giả định có LiveData này)
//         viewModel.getIsSuccess().observe(this, success -> {
//             if (success) {
//                 Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
//                 finish();
//             }
//         });
    }
}
