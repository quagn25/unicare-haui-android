package com.haui.UniCare.core.network;

import com.haui.UniCare.data.model.table.User;
import com.haui.UniCare.data.model.LoginRequest;
import com.haui.UniCare.data.model.LoginResponse;
import com.haui.UniCare.data.model.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // =====================
    // TEST LẤY USER
    // Node.js: GET /users
    // =====================
    @GET("users")
    Call<List<User>> getUsers();

    // =====================
    // LOGIN
    // Node.js: POST /login
    // =====================
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("register")
    Call<Void> register(@Body RegisterRequest request);
}