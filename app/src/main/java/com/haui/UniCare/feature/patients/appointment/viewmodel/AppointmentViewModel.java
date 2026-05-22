package com.haui.UniCare.feature.patients.appointment.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.haui.UniCare.core.base.BaseViewModel;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.table.Appointment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> isBookingSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getIsBookingSuccess() {
        return isBookingSuccess;
    }

    public void createAppointment(int patientId, int doctorId, String datetime, String note) {
        showLoading.setValue(true);
        
        Appointment appointment = new Appointment();
        appointment.patientId = patientId;
        appointment.doctorId = doctorId;
        appointment.appointmentDatetime = datetime;
        appointment.status = "PENDING";
        appointment.note = note;

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.createAppointment(appointment).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                showLoading.setValue(false);
                if (response.isSuccessful()) {
                    isBookingSuccess.setValue(true);
                } else {
                    isBookingSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                showLoading.setValue(false);
                isBookingSuccess.setValue(false);
            }
        });
    }

    public void updateAppointment(int appointmentId, String datetime, String note) {
        showLoading.setValue(true);
        
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("appointmentId", appointmentId);
        body.put("appointment_datetime", datetime);
        body.put("note", note);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.updateAppointmentDetails(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                showLoading.setValue(false);
                if (response.isSuccessful()) {
                    isBookingSuccess.setValue(true);
                } else {
                    isBookingSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                showLoading.setValue(false);
                isBookingSuccess.setValue(false);
            }
        });
    }
}
