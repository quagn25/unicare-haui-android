package com.haui.UniCare.core.network;

import com.haui.UniCare.data.model.LoginRequest;
import com.haui.UniCare.data.model.LoginResponse;
import com.haui.UniCare.data.model.User;

import java.util.List;

import retrofit2.Call;

public class ApiHelper {

    private final ApiService apiService;

    public ApiHelper() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    // =====================
    // GET USERS
    // =====================
    public Call<List<User>> getUsers() {
        return apiService.getUsers();
    }

    // =====================
    // LOGIN
    // =====================
    public Call<LoginResponse> login(String username, String password) {
        return apiService.login(new LoginRequest(username, password));
    }
}
