package com.haui.UniCare.feature.patients.appointment.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.BookingDate;
import com.haui.UniCare.data.model.TimeSlot;
import com.haui.UniCare.data.model.VaccineType;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.appointment.adapter.VaccineTypeAdapter;
import com.haui.UniCare.feature.patients.appointment.ui.ConfirmAppointmentActivity;
import com.haui.UniCare.feature.patients.doctor.adapter.BookingDateAdapter;
import com.haui.UniCare.feature.patients.doctor.adapter.TimeSlotAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookVaccineActivity extends AppCompatActivity {

    private RecyclerView rvVaccineTypes;
    private RecyclerView rcvCalendar, rvMorningSlots, rvAfternoonSlots, rvEveningSlots;
    
    private VaccineTypeAdapter vaccineAdapter;
    private BookingDateAdapter dateAdapter;
    private TimeSlotAdapter morningAdapter, afternoonAdapter, eveningAdapter;
    
    private Button btnBookVaccine;
    private ImageButton btnBack, btnPrevMonth, btnNextMonth;
    private TextView tvMonth;

    private List<VaccineType> vaccineList = new ArrayList<>();
    private List<TimeSlot> morningSlots = new ArrayList<>();
    private List<TimeSlot> afternoonSlots = new ArrayList<>();
    private List<TimeSlot> eveningSlots = new ArrayList<>();
    
    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vaccine);

        currentCalendar = Calendar.getInstance();

        mapping();
        initVaccineData();
        updateCalendar();
        setupEvents();
    }

    private void mapping() {
        rvVaccineTypes = findViewById(R.id.rvVaccineTypes);
        rcvCalendar = findViewById(R.id.recyclerCalendar);
        rvMorningSlots = findViewById(R.id.rvMorningSlots);
        rvAfternoonSlots = findViewById(R.id.rvAfternoonSlots);
        rvEveningSlots = findViewById(R.id.rvEveningSlots);
        btnBookVaccine = findViewById(R.id.btnBookVaccine);
        btnBack = findViewById(R.id.btnBack);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        tvMonth = findViewById(R.id.tvMonth);
    }

    private void initVaccineData() {
        // Load the 5 seeded vaccine virtual doctors
        vaccineList.add(new VaccineType(21, "Vắc-xin Cúm mùa", "Liều nhắc lại", 250000.0, "Phòng 201 - UniCare"));
        vaccineList.add(new VaccineType(22, "Vắc-xin HPV", "Mũi 2/3", 1200000.0, "Phòng 105 - UniCare"));
        vaccineList.add(new VaccineType(23, "Vắc-xin Viêm gan B", "Mũi 3/3", 180000.0, "Phòng 203 - UniCare"));
        vaccineList.add(new VaccineType(24, "Vắc-xin Covid-19", "Mũi nhắc", 0.0, "Phòng 105 - UniCare"));
        vaccineList.add(new VaccineType(25, "Vắc-xin Sởi - Quai bị - Rubella", "Mũi 1", 350000.0, "Phòng 201 - UniCare"));

        vaccineAdapter = new VaccineTypeAdapter(vaccineList, vaccine -> validateSelection());
        rvVaccineTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvVaccineTypes.setAdapter(vaccineAdapter);
    }

    private void updateCalendar() {
        // 1. Calendar Header
        SimpleDateFormat monthFormat = new SimpleDateFormat("'Lịch tháng' MM/yyyy", new Locale("vi", "VN"));
        tvMonth.setText(monthFormat.format(currentCalendar.getTime()));

        // 2. Generate Dates for Grid
        List<BookingDate> dates = new ArrayList<>();
        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - 2;

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
        boolean vaccineSelected = vaccineAdapter != null && vaccineAdapter.getSelectedVaccine() != null;
        boolean dateSelected = dateAdapter != null && dateAdapter.getSelectedDate() != null;
        boolean timeSelected = (morningAdapter != null && morningAdapter.getSelectedTime() != null) || 
                              (afternoonAdapter != null && afternoonAdapter.getSelectedTime() != null) || 
                              (eveningAdapter != null && eveningAdapter.getSelectedTime() != null);
        
        boolean isReady = vaccineSelected && dateSelected && timeSelected;
        btnBookVaccine.setEnabled(isReady);
        btnBookVaccine.setAlpha(isReady ? 1.0f : 0.5f);
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

        btnBookVaccine.setOnClickListener(v -> {
            VaccineType selectedVaccine = vaccineAdapter.getSelectedVaccine();
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

            if (selectedVaccine != null && selectedDate != null && selectedTime != null) {
                // Reconstruct a virtual Doctor object to seamlessly reuse ConfirmAppointmentActivity
                Doctor vaccineDoc = new Doctor();
                vaccineDoc.setId(selectedVaccine.getDbDoctorId());
                vaccineDoc.setName(selectedVaccine.getName());
                vaccineDoc.setDegree(selectedVaccine.getDoseInfo());
                vaccineDoc.setSpecialties("Tiêm chủng");
                vaccineDoc.setConsultationFee((float) selectedVaccine.getPrice());
                vaccineDoc.setBio("Tiêm chủng");
                vaccineDoc.setAddress(selectedVaccine.getNote()); // Contains the specific room details e.g. "Phòng 201 - UniCare"

                Intent intent = new Intent(this, ConfirmAppointmentActivity.class);
                intent.putExtra("doctor_data", vaccineDoc);

                String dateString = selectedDate.getDate() + "/" + 
                                  (currentCalendar.get(Calendar.MONTH) + 1) + "/" + 
                                  currentCalendar.get(Calendar.YEAR);
                
                intent.putExtra("selected_date", dateString);
                intent.putExtra("selected_time", selectedTime.getTimeRange() + " (" + periodName + ")");
                intent.putExtra("is_morning", isMorning);

                startActivity(intent);
            } else {
                Toast.makeText(this, "Vui lòng chọn đầy đủ thông tin vắc-xin, ngày và giờ tiêm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
