package com.haui.UniCare.feature.patients.appointment.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

    private RecyclerView rcvCalendar, rvMorningSlots, rvAfternoonSlots, rvEveningSlots;
    private BookingDateAdapter dateAdapter;
    private TimeSlotAdapter morningAdapter, afternoonAdapter, eveningAdapter;
    private Button btnBooking;
    private ImageButton btnBack, btnPrevMonth, btnNextMonth;
    private ImageView imgAvatar;
    private TextView tvName, tvTitle, tvExperience, tvMonth, tvBiography;
    private Doctor selectedDoctor;

    private List<TimeSlot> morningSlots = new ArrayList<>();
    private List<TimeSlot> afternoonSlots = new ArrayList<>();
    private List<TimeSlot> eveningSlots = new ArrayList<>();
    
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");
        currentCalendar = Calendar.getInstance();

        mapping();
        initDoctorInfo();
        updateCalendar();
        setupEvents();
    }

    private void mapping() {
        rcvCalendar = findViewById(R.id.recyclerCalendar);
        rvMorningSlots = findViewById(R.id.rvMorningSlots);
        rvAfternoonSlots = findViewById(R.id.rvAfternoonSlots);
        rvEveningSlots = findViewById(R.id.rvEveningSlots);
        btnBooking = findViewById(R.id.btnBooking);
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvName);
        tvTitle = findViewById(R.id.tvTitle);
        tvExperience = findViewById(R.id.tvExperience);
        tvMonth = findViewById(R.id.tvMonth);
        tvBiography = findViewById(R.id.tvBiography);
    }

    private void initDoctorInfo() {
        if (selectedDoctor != null) {
            tvName.setText(selectedDoctor.getName());
            tvTitle.setText(selectedDoctor.getDegree());
            tvExperience.setText(selectedDoctor.getExperienceText());
            tvBiography.setText(selectedDoctor.getBio());

            if (selectedDoctor.getAvatarUrl() != null && !selectedDoctor.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(selectedDoctor.getAvatarUrl())
                        .placeholder(R.drawable.doctorbook)
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(selectedDoctor.getAvatarResource() != 0 
                        ? selectedDoctor.getAvatarResource() : R.drawable.doctorbook);
            }
        }
    }

    private void updateCalendar() {
        // 1. Calendar Header
        SimpleDateFormat monthFormat = new SimpleDateFormat("'Lịch tháng' MM/yyyy", new Locale("vi", "VN"));
        tvMonth.setText(monthFormat.format(currentCalendar.getTime()));

        // 2. Generate Dates for Grid
        List<BookingDate> dates = new ArrayList<>();
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        // Find day of week for the 1st day (1=Sun, 2=Mon...)
        // We want T2 (Mon) as first column, so if Sun(1) -> it's the 7th col
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - 2;

        // Add padding days
        for (int i = 0; i < offset; i++) {
            dates.add(new BookingDate("", "", ""));
        }

        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] daysOfWeekLabels = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        
        Calendar today = Calendar.getInstance();
        boolean isCurrentMonth = today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                                today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            String dayLabel = daysOfWeekLabels[cal.get(Calendar.DAY_OF_WEEK) - 1];
            
            // Only show slots for future days (or today)
            String slots = "";
            if (!isCurrentMonth || i >= today.get(Calendar.DAY_OF_MONTH)) {
                slots = "9 slot";
            }
            
            dates.add(new BookingDate(dayLabel, String.valueOf(i), slots));
        }

        dateAdapter = new BookingDateAdapter(dates, date -> validateSelection());
        rcvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        rcvCalendar.setAdapter(dateAdapter);

        // 3. Time Slots
        initTimeSlots();
        validateSelection();
    }

    private void initTimeSlots() {
        morningSlots.clear();
        morningSlots.add(new TimeSlot("07:00 - 07:30"));
        morningSlots.add(new TimeSlot("07:30 - 08:00"));
        morningSlots.add(new TimeSlot("08:00 - 08:30"));
        morningSlots.add(new TimeSlot("08:30 - 09:00"));
        morningSlots.add(new TimeSlot("09:00 - 09:30"));
        morningSlots.add(new TimeSlot("09:30 - 10:00"));

        afternoonSlots.clear();
        afternoonSlots.add(new TimeSlot("13:30 - 14:00"));
        afternoonSlots.add(new TimeSlot("14:00 - 14:30"));
        afternoonSlots.add(new TimeSlot("14:30 - 15:00"));
        afternoonSlots.add(new TimeSlot("15:00 - 15:30"));
        afternoonSlots.add(new TimeSlot("15:30 - 16:00"));
        afternoonSlots.add(new TimeSlot("16:00 - 16:30"));

        eveningSlots.clear();
        eveningSlots.add(new TimeSlot("18:00 - 18:30"));
        eveningSlots.add(new TimeSlot("18:30 - 19:00"));
        eveningSlots.add(new TimeSlot("19:00 - 19:30"));

        if (morningAdapter == null) {
            morningAdapter = new TimeSlotAdapter(morningSlots, slot -> onTimeSlotSelected(1));
            afternoonAdapter = new TimeSlotAdapter(afternoonSlots, slot -> onTimeSlotSelected(2));
            eveningAdapter = new TimeSlotAdapter(eveningSlots, slot -> onTimeSlotSelected(3));

            rvMorningSlots.setLayoutManager(new GridLayoutManager(this, 3));
            rvAfternoonSlots.setLayoutManager(new GridLayoutManager(this, 3));
            rvEveningSlots.setLayoutManager(new GridLayoutManager(this, 3));

            rvMorningSlots.setAdapter(morningAdapter);
            rvAfternoonSlots.setAdapter(afternoonAdapter);
            rvEveningSlots.setAdapter(eveningAdapter);
        } else {
            morningAdapter.notifyDataSetChanged();
            afternoonAdapter.notifyDataSetChanged();
            eveningAdapter.notifyDataSetChanged();
        }
    }

    private void onTimeSlotSelected(int period) {
        if (period != 1) morningAdapter.clearSelection();
        if (period != 2) afternoonAdapter.clearSelection();
        if (period != 3) eveningAdapter.clearSelection();
        validateSelection();
    }

    private void validateSelection() {
        boolean dateSelected = dateAdapter.getSelectedDate() != null;
        boolean timeSelected = (morningAdapter != null && morningAdapter.getSelectedTime() != null) || 
                              (afternoonAdapter != null && afternoonAdapter.getSelectedTime() != null) || 
                              (eveningAdapter != null && eveningAdapter.getSelectedTime() != null);
        
        boolean isReady = dateSelected && timeSelected;
        btnBooking.setEnabled(isReady);
        btnBooking.setAlpha(isReady ? 1.0f : 0.5f);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        btnBooking.setOnClickListener(v -> {
            BookingDate selectedDate = dateAdapter.getSelectedDate();
            TimeSlot selectedTime = null;
            String periodName = "";
            boolean isMorning = false;

            if (morningAdapter.getSelectedTime() != null) {
                selectedTime = morningAdapter.getSelectedTime();
                periodName = "Sáng";
                isMorning = true;
            } else if (afternoonAdapter.getSelectedTime() != null) {
                selectedTime = afternoonAdapter.getSelectedTime();
                periodName = "Chiều";
            } else if (eveningAdapter.getSelectedTime() != null) {
                selectedTime = eveningAdapter.getSelectedTime();
                periodName = "Tối";
            }

            if (selectedDate != null && selectedTime != null) {
                Intent intent = new Intent(this, ConfirmAppointmentActivity.class);
                intent.putExtra("doctor_data", selectedDoctor);

                String dateString = selectedDate.getDate() + "/" + 
                                  (currentCalendar.get(Calendar.MONTH) + 1) + "/" + 
                                  currentCalendar.get(Calendar.YEAR);
                
                intent.putExtra("selected_date", dateString);
                intent.putExtra("selected_time", selectedTime.getTimeRange() + " (" + periodName + ")");
                intent.putExtra("is_morning", isMorning);

                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày và giờ khám", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
