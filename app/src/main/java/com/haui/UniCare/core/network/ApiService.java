package com.haui.UniCare.core.network;

import com.haui.UniCare.data.model.table.User;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.data.model.LoginRequest;
import com.haui.UniCare.data.model.LoginResponse;
import com.haui.UniCare.data.model.RegisterRequest;
import com.haui.UniCare.data.model.SendOtpRequest;
import com.haui.UniCare.data.model.ResetPasswordRequest;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.PatientProfileResponse;
import com.haui.UniCare.data.model.PatientProfileUpdateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("users")
    Call<List<User>> getUsers();

    @GET("doctors")
    Call<List<Doctor>> getDoctors();

    @GET("appointments")
    Call<List<Appointment>> getAppointments(@retrofit2.http.Query("patient_id") int patientId);

    @POST("appointments")
    Call<GenericResponse> createAppointment(@Body Appointment appointment);

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<Void> register(@Body RegisterRequest request);

    @POST("forgot-password/send-otp")
    Call<GenericResponse> sendOtp(@Body SendOtpRequest request);

    @POST("forgot-password/reset")
    Call<GenericResponse> resetPassword(@Body ResetPasswordRequest request);

    @GET("notifications")
    Call<com.haui.UniCare.data.model.NotificationResponse> getNotifications(@retrofit2.http.Query("userId") int userId);

    @POST("notifications/read-all")
    Call<GenericResponse> readAllNotifications(@Body java.util.Map<String, Integer> body);

    @POST("appointments/reschedule")
    Call<GenericResponse> rescheduleAppointment(@Body java.util.Map<String, Integer> body);

    @POST("appointments/cancel")
    Call<GenericResponse> cancelAppointment(@Body java.util.Map<String, Integer> body);

    @POST("appointments/update-details")
    Call<GenericResponse> updateAppointmentDetails(@Body java.util.Map<String, Object> body);

    @GET("patients/profile")
    Call<PatientProfileResponse> getPatientProfile(@retrofit2.http.Query("userId") int userId);

    @POST("patients/profile/update")
    Call<GenericResponse> updatePatientProfile(@Body PatientProfileUpdateRequest request);

    @POST("change-password")
    Call<GenericResponse> changePassword(@Body com.haui.UniCare.data.model.ChangePasswordRequest request);

    @GET("appointments/{id}/details")
    Call<com.haui.UniCare.data.model.AppointmentDetailResponse> getAppointmentDetails(@retrofit2.http.Path("id") int appointmentId);

    @GET("appointments/{id}/medical-record")
    Call<com.haui.UniCare.data.model.MedicalRecordResponse> getMedicalRecord(@retrofit2.http.Path("id") int appointmentId);

    @GET("medical-records/{recordId}/treatment-plans")
    Call<com.haui.UniCare.data.model.TreatmentPlansResponse> getTreatmentPlans(@retrofit2.http.Path("recordId") int recordId);
}