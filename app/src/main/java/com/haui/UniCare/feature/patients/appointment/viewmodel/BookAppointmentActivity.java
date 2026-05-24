package com.haui.UniCare.feature.patients.appointment.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private int rescheduleAppointmentId = -1;

    private List<TimeSlot> morningSlots = new ArrayList<>();
    private List<TimeSlot> afternoonSlots = new ArrayList<>();
    private List<TimeSlot> eveningSlots = new ArrayList<>();
    
    private final String[] allMorning = {"07:00 - 07:30", "07:30 - 08:00", "08:00 - 08:30", "08:30 - 09:00", "09:00 - 09:30", "09:30 - 10:00"};
    private final String[] allAfternoon = {"13:30 - 14:00", "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00", "16:00 - 16:30"};
    private final String[] allEvening = {"18:00 - 18:30", "18:30 - 19:00", "19:00 - 19:30"};
    
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_appointment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedDoctor = (Doctor) getIntent().getSerializableExtra("doctor_data");
        rescheduleAppointmentId = getIntent().getIntExtra("reschedule_appointment_id", -1);
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
            
            String slots = "";
            int available = getAvailableSlotsCount(cal, today);
            if (available > 0) {
                slots = available + " slot";
            }
            
            dates.add(new BookingDate(dayLabel, String.valueOf(i), slots));
        }

        dateAdapter = new BookingDateAdapter(dates, date -> {
            updateTimeSlots(date);
            validateSelection();
        });
        rcvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        rcvCalendar.setAdapter(dateAdapter);

        // Select the first available day by default
        for (BookingDate bd : dates) {
            if (!bd.getDate().isEmpty() && !bd.getSlotCount().isEmpty()) {
                bd.setSelected(true);
                dateAdapter.setSelectedDate(bd);
                updateTimeSlots(bd);
                break;
            }
        }
        validateSelection();
    }

    private int getAvailableSlotsCount(Calendar targetDay, Calendar today) {
        if (targetDay.get(Calendar.YEAR) < today.get(Calendar.YEAR) ||
            (targetDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) && targetDay.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR))) {
            return 0; // Past day
        }
        
        if (targetDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) && targetDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            int count = 0;
            int currentHour = today.get(Calendar.HOUR_OF_DAY);
            int currentMinute = today.get(Calendar.MINUTE);
            for (String s : allMorning) if (isFutureSlot(s, currentHour, currentMinute)) count++;
            for (String s : allAfternoon) if (isFutureSlot(s, currentHour, currentMinute)) count++;
            for (String s : allEvening) if (isFutureSlot(s, currentHour, currentMinute)) count++;
            return count;
        }
        
        return allMorning.length + allAfternoon.length + allEvening.length;
    }

    private boolean isFutureSlot(String slotText, int currentHour, int currentMinute) {
        String startTime = slotText.split(" - ")[0];
        String[] parts = startTime.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        if (h > currentHour) return true;
        if (h == currentHour && m >= currentMinute) return true;
        return false;
    }

    private boolean isCurrentMonth() {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
               today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);
    }

    private void updateTimeSlots(BookingDate selectedDate) {
        if (selectedDate == null || selectedDate.getDate().isEmpty()) {
            morningSlots.clear(); afternoonSlots.clear(); eveningSlots.clear();
            notifyAdapters();
            return;
        }

        Calendar today = Calendar.getInstance();
        boolean isToday = isCurrentMonth() && Integer.parseInt(selectedDate.getDate()) == today.get(Calendar.DAY_OF_MONTH);
        
        int currentHour = today.get(Calendar.HOUR_OF_DAY);
        int currentMinute = today.get(Calendar.MINUTE);

        morningSlots.clear();
        for (String s : allMorning) {
            if (!isToday || isFutureSlot(s, currentHour, currentMinute)) morningSlots.add(new TimeSlot(s));
        }
        
        afternoonSlots.clear();
        for (String s : allAfternoon) {
            if (!isToday || isFutureSlot(s, currentHour, currentMinute)) afternoonSlots.add(new TimeSlot(s));
        }

        eveningSlots.clear();
        for (String s : allEvening) {
            if (!isToday || isFutureSlot(s, currentHour, currentMinute)) eveningSlots.add(new TimeSlot(s));
        }

        if (morningAdapter != null) morningAdapter.clearSelection();
        if (afternoonAdapter != null) afternoonAdapter.clearSelection();
        if (eveningAdapter != null) eveningAdapter.clearSelection();

        notifyAdapters();
    }

    private void notifyAdapters() {


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
                intent.putExtra("reschedule_appointment_id", rescheduleAppointmentId);

                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn đầy đủ ngày và giờ khám", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
