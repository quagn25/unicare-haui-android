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
}