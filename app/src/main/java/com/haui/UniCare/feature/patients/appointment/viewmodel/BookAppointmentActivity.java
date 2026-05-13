package com.haui.UniCare.feature.patients.appointment.viewmodel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.BookingDate;
import com.haui.UniCare.data.model.TimeSlot;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.appointment.ui.ConfirmAppointmentActivity;
import com.haui.UniCare.feature.patients.doctor.adapter.BookingDateAdapter;
import com.haui.UniCare.feature.patients.doctor.adapter.TimeSlotAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private RecyclerView rcvDates, rcvTimes;
    private BookingDateAdapter dateAdapter;
    private TimeSlotAdapter timeAdapter;
    private Button btnContinue;
    private ImageView imgDoctor, btnBack;
    private TextView tvDoctorName, tvDoctorTitle, tvDoctorSpecialty;
    private TextView tabMorning, tabAfternoon, tvCurrentMonth;
    private Doctor selectedDoctor;

    private List<TimeSlot> morningSlots = new ArrayList<>();
    private List<TimeSlot> afternoonSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Nhận dữ liệu bác sĩ
        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");

        mapping();
        initDoctorInfo();
        initBookingData();
        setupEvents();

        // Mặc định chọn buổi sáng
        selectTab(true);
    }

    private void mapping() {
        rcvDates = findViewById(R.id.rcv_dates);
        rcvTimes = findViewById(R.id.rcv_times);
        btnContinue = findViewById(R.id.btn_continue);
        btnBack = findViewById(R.id.btn_back);
        imgDoctor = findViewById(R.id.img_doctor_avatar);
        tvDoctorName = findViewById(R.id.tv_doctor_name);
        tvDoctorTitle = findViewById(R.id.tv_doctor_title);
        tvDoctorSpecialty = findViewById(R.id.tv_doctor_specialty);
        tabMorning = findViewById(R.id.tabMorning);
        tabAfternoon = findViewById(R.id.tabAfternoon);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
    }

    private void initDoctorInfo() {
        if (selectedDoctor != null) {
            tvDoctorName.setText(selectedDoctor.getName());
            tvDoctorTitle.setText(selectedDoctor.getDegree());
            tvDoctorSpecialty.setText("Chuyên khoa: " + selectedDoctor.getSpecialties());

            Glide.with(this)
                    .load(selectedDoctor.getAvatarUrl())
                    .placeholder(R.drawable.doctorbook)
                    .into(imgDoctor);
        }
    }

    private void initBookingData() {
        // Tạo danh sách 7 ngày tới tự động
        List<BookingDate> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Hiển thị tháng/năm hiện tại (Ví dụ: Tháng 05/2024)
        SimpleDateFormat monthFormat = new SimpleDateFormat("'Tháng' MM/yyyy", new Locale("vi", "VN"));
        if (tvCurrentMonth != null) {
            tvCurrentMonth.setText(monthFormat.format(calendar.getTime()) + " ∨");
        }

        String[] daysOfWeek = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};

        for (int i = 0; i < 7; i++) {
            // Lấy thứ và ngày
            String dayLabel = daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            String dateLabel = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

            // Mặc định cho mỗi ngày có 9 slot trống (giả lập)
            dates.add(new BookingDate(dayLabel, dateLabel, "9 slot"));

            // Tăng thêm 1 ngày
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        dateAdapter = new BookingDateAdapter(dates, date -> validateSelection());
        rcvDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rcvDates.setAdapter(dateAdapter);

        // Mock dữ liệu giờ sáng
        morningSlots.clear();
        morningSlots.add(new TimeSlot("07:00-08:00"));
        morningSlots.add(new TimeSlot("08:00-09:00"));
        morningSlots.add(new TimeSlot("09:00-10:00"));
        morningSlots.add(new TimeSlot("10:00-11:00"));
        morningSlots.add(new TimeSlot("10:00-11:00"));
        morningSlots.add(new TimeSlot("11:00-12:00"));

        // Mock dữ liệu giờ chiều
        afternoonSlots.clear();
        afternoonSlots.add(new TimeSlot("13:00-14:00"));
        afternoonSlots.add(new TimeSlot("14:00-15:00"));
        afternoonSlots.add(new TimeSlot("15:00-16:00"));
        afternoonSlots.add(new TimeSlot("16:00-17:00"));
        afternoonSlots.add(new TimeSlot("18:00-19:00"));
        afternoonSlots.add(new TimeSlot("19:00-20:00"));

        rcvTimes.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void selectTab(boolean isMorning) {
        if (isMorning) {
            tabMorning.setBackgroundResource(R.drawable.bg_tab_morning);
            tabMorning.setTextColor(Color.BLACK);
            tabAfternoon.setBackgroundColor(Color.TRANSPARENT);
            tabAfternoon.setTextColor(Color.GRAY);

            timeAdapter = new TimeSlotAdapter(morningSlots, time -> validateSelection());
        } else {
            tabAfternoon.setBackgroundResource(R.drawable.bg_tab_morning); // Tạm dùng chung shape
            tabAfternoon.setTextColor(Color.BLACK);
            tabMorning.setBackgroundColor(Color.TRANSPARENT);
            tabMorning.setTextColor(Color.GRAY);

            timeAdapter = new TimeSlotAdapter(afternoonSlots, time -> validateSelection());
        }
        rcvTimes.setAdapter(timeAdapter);
        validateSelection();
    }

    private void validateSelection() {
        boolean isReady = dateAdapter.getSelectedDate() != null && timeAdapter.getSelectedTime() != null;
        btnContinue.setEnabled(isReady);
        btnContinue.setAlpha(isReady ? 1.0f : 0.5f);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        tabMorning.setOnClickListener(v -> selectTab(true));
        tabAfternoon.setOnClickListener(v -> selectTab(false));

        btnContinue.setOnClickListener(v -> {
            BookingDate selectedDate = dateAdapter.getSelectedDate();
            TimeSlot selectedTime = timeAdapter.getSelectedTime();

            if (selectedDate != null && selectedTime != null) {
                Intent intent = new Intent(BookAppointmentActivity.this, ConfirmAppointmentActivity.class);

                intent.putExtra("doctor_data", selectedDoctor);

                String monthYear = tvCurrentMonth.getText().toString()
                        .replace("Tháng ", "")
                        .replace(" ∨", "");

                // FIX: Use getDayOfWeek() instead of getDay() to match BookingDate model
                String fullDate = selectedDate.getDayOfWeek() + " " + selectedDate.getDate() + "/" + monthYear;
                intent.putExtra("selected_date", fullDate);

                intent.putExtra("selected_time", selectedTime.getTimeRange());

                boolean isMorning = (tabMorning.getCurrentTextColor() == Color.BLACK);
                intent.putExtra("is_morning", isMorning);

                startActivity(intent);
            }
        });
    }
}
