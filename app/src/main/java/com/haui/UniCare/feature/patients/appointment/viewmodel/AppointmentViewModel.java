package com.haui.UniCare.feature.patients.appointment.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.haui.UniCare.core.base.BaseViewModel;
import com.haui.UniCare.data.model.datetime.DateSlot;
import com.haui.UniCare.data.model.datetime.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class AppointmentViewModel extends BaseViewModel {

    private final MutableLiveData<List<DateSlot>> availableDates = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<TimeSlot>> availableTimes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<DateSlot> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<TimeSlot> selectedTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isNextButtonEnabled = new MutableLiveData<>(false);

    public LiveData<List<DateSlot>> getAvailableDates() {
        return availableDates;
    }

    public LiveData<List<TimeSlot>> getAvailableTimes() {
        return availableTimes;
    }

    public LiveData<DateSlot> getSelectedDate() {
        return selectedDate;
    }

    public LiveData<TimeSlot> getSelectedTime() {
        return selectedTime;
    }

    public LiveData<Boolean> getIsNextButtonEnabled() {
        return isNextButtonEnabled;
    }

    public void selectDate(DateSlot date) {
        selectedDate.setValue(date);
        selectedTime.setValue(null);
        loadTimeSlots(date);
        checkEnableNextButton();
    }

    public void selectTime(TimeSlot time) {
        selectedTime.setValue(time);
        checkEnableNextButton();
    }

    private void loadTimeSlots(DateSlot date) {
        // TODO: Gọi Repository để lấy list giờ theo ngày được chọn
        List<TimeSlot> mockTimes = new ArrayList<>();
        mockTimes.add(new TimeSlot("08:00 - 08:30", true, false));
        mockTimes.add(new TimeSlot("09:00 - 09:30", true, false));
        mockTimes.add(new TimeSlot("14:00 - 14:30", true, false));
        mockTimes.add(new TimeSlot("15:00 - 15:30", true, false));
        availableTimes.setValue(mockTimes);
    }

    private void checkEnableNextButton() {
        boolean isEnabled = selectedDate.getValue() != null && selectedTime.getValue() != null;
        isNextButtonEnabled.setValue(isEnabled);
    }

    public void filterTimeSlots(String period) {
        List<TimeSlot> allSlots = availableTimes.getValue();
        if (allSlots == null) return;
        
        // Logic lọc Sáng/Chiều
        List<TimeSlot> filteredList = new ArrayList<>();
        for (TimeSlot slot : allSlots) {
            String hourStr = slot.getTimeRange().split(":")[0].trim();
            int hour = Integer.parseInt(hourStr);
            if ("Sáng".equalsIgnoreCase(period) && hour < 12) {
                filteredList.add(slot);
            } else if ("Chiều".equalsIgnoreCase(period) && hour >= 12) {
                filteredList.add(slot);
            }
        }
        availableTimes.setValue(filteredList);
    }

    public void loadDoctorInfo(int doctorId) {
        // TODO: Load thông tin bác sĩ
    }

    public void loadPatientProfile() {
        // TODO: Load thông tin bệnh nhân
    }

    public void createAppointment(int patientId, int doctorId) {
        showLoading.setValue(true);
        // TODO: Thực hiện đăng ký lịch hẹn
    }
}
