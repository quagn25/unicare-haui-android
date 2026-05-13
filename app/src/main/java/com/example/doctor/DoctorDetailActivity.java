package com.example.doctor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DoctorDetailActivity extends AppCompatActivity {

    private Doctor doctor;
    private BookingDateAdapter dateAdapter;
    private TimeSlotAdapter morningAdapter, afternoonAdapter, eveningAdapter;
    private String selectedSlot = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        int doctorId = getIntent().getIntExtra("doctor_id", 1);
        doctor = DoctorRepository.getInstance().getDoctorById(doctorId);
        if (doctor == null) { finish(); return; }

        bindData();
        setupCalendar();
        setupTimeSlots();
        setupButtons();
    }

    private void bindData() {
        ((ImageView) findViewById(R.id.imgAvatar))   .setImageResource(doctor.getAvatarResId());
        ((TextView)  findViewById(R.id.tvTitle))      .setText(doctor.getTitle());
        ((TextView)  findViewById(R.id.tvName))       .setText(doctor.getName());
        ((TextView)  findViewById(R.id.tvExperience)) .setText(doctor.getExperience());
        ((TextView)  findViewById(R.id.tvRole))       .setText(doctor.getRole());

        ChipGroup chipGroup = findViewById(R.id.chipGroupSpecialties);
        chipGroup.removeAllViews();
        for (String s : doctor.getSpecialties()) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setClickable(false);
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(getColor(R.color.chip_text));
            chipGroup.addView(chip);
        }

        View layoutNote = findViewById(R.id.layoutNote);
        TextView tvNote = findViewById(R.id.tvNoteContent);
        if (doctor.getNote() != null && !doctor.getNote().isEmpty()) {
            layoutNote.setVisibility(View.VISIBLE);
            tvNote.setText(doctor.getNote());
        } else {
            layoutNote.setVisibility(View.GONE);
        }

        TextView tvBio = findViewById(R.id.tvBiography);
        if (doctor.getBiography() != null && !doctor.getBiography().isEmpty()) {
            tvBio.setText(doctor.getBiography());
        }
    }

    private void setupCalendar() {
        updateMonthLabel(Calendar.getInstance());
        List<BookingDateAdapter.DateItem> items = buildDateItems(Calendar.getInstance());
        dateAdapter = new BookingDateAdapter(this, items, this::onDateSelected);

        RecyclerView rv = findViewById(R.id.recyclerCalendar);
        rv.setLayoutManager(new GridLayoutManager(this, 7));
        rv.setAdapter(dateAdapter);

        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            Calendar current = dateAdapter.getCurrentMonth();
            current.add(Calendar.MONTH, -1);
            Calendar now = Calendar.getInstance();
            if (current.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
                    (current.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                            current.get(Calendar.MONTH) < now.get(Calendar.MONTH))) return;
            refreshCalendar(current);
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            Calendar current = dateAdapter.getCurrentMonth();
            current.add(Calendar.MONTH, 1);
            Calendar maxMonth = Calendar.getInstance();
            maxMonth.add(Calendar.MONTH, 3);
            if (current.after(maxMonth)) return;
            refreshCalendar(current);
        });
    }

    // Khi chọn ngày mới → reset khung giờ
    private void onDateSelected() {
        selectedSlot = null;
        if (morningAdapter   != null) morningAdapter.clearSelection();
        if (afternoonAdapter != null) afternoonAdapter.clearSelection();
        if (eveningAdapter   != null) eveningAdapter.clearSelection();
    }

    private void refreshCalendar(Calendar cal) {
        updateMonthLabel(cal);
        dateAdapter.updateData(buildDateItems(cal), cal);
        onDateSelected();
    }

    private void updateMonthLabel(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("'Lịch tháng' MM/yyyy", new Locale("vi"));
        ((TextView) findViewById(R.id.tvMonth)).setText(sdf.format(cal.getTime()));
    }

    private List<BookingDateAdapter.DateItem> buildDateItems(Calendar cal) {
        List<BookingDateAdapter.DateItem> items = new ArrayList<>();
        Calendar today = Calendar.getInstance();

        Calendar start = (Calendar) cal.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);

        int firstDow = start.get(Calendar.DAY_OF_WEEK);
        int padding  = (firstDow == Calendar.SUNDAY) ? 6 : firstDow - Calendar.MONDAY;
        for (int i = 0; i < padding; i++) {
            items.add(new BookingDateAdapter.DateItem("", 0, 0));
        }

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int d = 1; d <= maxDay; d++) {
            Calendar day = (Calendar) cal.clone();
            day.set(Calendar.DAY_OF_MONTH, d);
            int dow = day.get(Calendar.DAY_OF_WEEK);

            boolean isPast = day.get(Calendar.YEAR) < today.get(Calendar.YEAR)
                    || (day.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && day.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && day.get(Calendar.DAY_OF_MONTH) < today.get(Calendar.DAY_OF_MONTH));

            boolean isSunday = (dow == Calendar.SUNDAY);
            int slots = (isSunday || isPast) ? 0 : 9;
            items.add(new BookingDateAdapter.DateItem(getDayLabel(dow), d, slots, isSunday || isPast));
        }
        return items;
    }

    private String getDayLabel(int dow) {
        switch (dow) {
            case Calendar.MONDAY:    return "T2";
            case Calendar.TUESDAY:   return "T3";
            case Calendar.WEDNESDAY: return "T4";
            case Calendar.THURSDAY:  return "T5";
            case Calendar.FRIDAY:    return "T6";
            case Calendar.SATURDAY:  return "T7";
            case Calendar.SUNDAY:    return "CN";
            default: return "";
        }
    }

    private void setupTimeSlots() {
        TimeSlotAdapter.OnSlotSelectedListener listener = slot -> selectedSlot = slot;

        List<String> morning = Arrays.asList(
                "07:30 - 08:00", "08:00 - 08:30", "08:30 - 09:00",
                "09:00 - 09:30", "09:30 - 10:00", "10:00 - 10:30",
                "10:30 - 11:00", "11:00 - 11:30");
        List<String> afternoon = Arrays.asList(
                "13:00 - 13:30", "13:30 - 14:00", "14:00 - 14:30",
                "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00",
                "16:00 - 16:30", "16:30 - 17:00");
        List<String> evening = Arrays.asList(
                "17:30 - 18:00", "18:00 - 18:30", "18:30 - 19:00",
                "19:00 - 19:30", "19:30 - 20:00");

        morningAdapter   = new TimeSlotAdapter(this, morning,   listener);
        afternoonAdapter = new TimeSlotAdapter(this, afternoon, listener);
        eveningAdapter   = new TimeSlotAdapter(this, evening,   listener);

        setupSlotRv(R.id.rvMorningSlots,   morningAdapter);
        setupSlotRv(R.id.rvAfternoonSlots, afternoonAdapter);
        setupSlotRv(R.id.rvEveningSlots,   eveningAdapter);
    }

    private void setupSlotRv(int rvId, TimeSlotAdapter adapter) {
        RecyclerView rv = findViewById(rvId);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(adapter);
        rv.setNestedScrollingEnabled(false);
    }

    private void setupButtons() {
        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());

        ((MaterialButton) findViewById(R.id.btnBooking)).setOnClickListener(v -> {
            BookingDateAdapter.DateItem date = dateAdapter.getSelectedDate();
            if (date == null || date.getDayNum() == 0) {
                Toast.makeText(this, "Vui lòng chọn ngày khám", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedSlot == null) {
                Toast.makeText(this, "Vui lòng chọn khung giờ khám", Toast.LENGTH_SHORT).show();
                return;
            }
            new BookingConfirmDialog(this, doctor,
                    date.getDayLabel(), date.getDayNum(),
                    dateAdapter.getCurrentMonthFormatted(), selectedSlot).show();
        });
    }
}