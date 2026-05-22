package com.haui.UniCare.feature.patients.home.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.model.GenericResponse;
import com.haui.UniCare.data.model.Notification;
import com.haui.UniCare.data.model.NotificationResponse;
import com.haui.UniCare.feature.patients.home.adapter.NotificationAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotifications;
    private TextView tvUnreadCount;
    private MaterialCardView btnReadAll;

    // Filters Tabs
    private MaterialCardView tabAll, tabLichKham, tabTiemChung, tabKetQua;
    private TextView tvTabAll, tvTabLichKham, tvTabTiemChung, tvTabKetQua;

    private NotificationAdapter adapter;
    private List<Notification> allNotifications = new ArrayList<>();
    private List<Notification> filteredNotifications = new ArrayList<>();

    private String currentFilter = "ALL"; // ALL, LICH_KHAM, TIEM_CHUNG, KET_QUA
    private int userId = 0;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Get stored user ID from preferences
        if (getActivity() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE);
            userId = sharedPref.getInt("userId", 0);
        }

        initViews(view);
        setupRecyclerView();
        setupEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNotifications();
    }

    private void initViews(View view) {
        rvNotifications = view.findViewById(R.id.rvNotifications);
        tvUnreadCount = view.findViewById(R.id.tvUnreadCount);
        btnReadAll = view.findViewById(R.id.btnReadAll);

        tabAll = view.findViewById(R.id.tabAll);
        tabLichKham = view.findViewById(R.id.tabLichKham);
        tabTiemChung = view.findViewById(R.id.tabTiemChung);
        tabKetQua = view.findViewById(R.id.tabKetQua);

        tvTabAll = view.findViewById(R.id.tvTabAll);
        tvTabLichKham = view.findViewById(R.id.tvTabLichKham);
        tvTabTiemChung = view.findViewById(R.id.tvTabTiemChung);
        tvTabKetQua = view.findViewById(R.id.tvTabKetQua);
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        adapter = new NotificationAdapter(filteredNotifications, getContext());
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(adapter);
    }

    private void setupEvents() {
        tabAll.setOnClickListener(v -> selectTab("ALL"));
        tabLichKham.setOnClickListener(v -> selectTab("LICH_KHAM"));
        tabTiemChung.setOnClickListener(v -> selectTab("TIEM_CHUNG"));
        tabKetQua.setOnClickListener(v -> selectTab("KET_QUA"));

        btnReadAll.setOnClickListener(v -> readAllNotifications());
    }

    private void selectTab(String filter) {
        if (!currentFilter.equals(filter)) {
            currentFilter = filter;
            updateTabUI();
            applyFilter();
        }
    }

    private void updateTabUI() {
        // Reset all tabs to default unselected style
        tabAll.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabAll.setTextColor(Color.parseColor("#475569"));

        tabLichKham.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabLichKham.setTextColor(Color.parseColor("#475569"));

        tabTiemChung.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabTiemChung.setTextColor(Color.parseColor("#475569"));

        tabKetQua.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabKetQua.setTextColor(Color.parseColor("#475569"));

        // Highlight selected tab visually
        switch (currentFilter) {
            case "ALL":
                tabAll.setCardBackgroundColor(Color.parseColor("#0B5CFF"));
                tvTabAll.setTextColor(Color.WHITE);
                break;
            case "LICH_KHAM":
                tabLichKham.setCardBackgroundColor(Color.parseColor("#0B5CFF"));
                tvTabLichKham.setTextColor(Color.WHITE);
                break;
            case "TIEM_CHUNG":
                tabTiemChung.setCardBackgroundColor(Color.parseColor("#0B5CFF"));
                tvTabTiemChung.setTextColor(Color.WHITE);
                break;
            case "KET_QUA":
                tabKetQua.setCardBackgroundColor(Color.parseColor("#0B5CFF"));
                tvTabKetQua.setTextColor(Color.WHITE);
                break;
        }
    }

    private void applyFilter() {
        filteredNotifications.clear();
        if ("ALL".equals(currentFilter)) {
            filteredNotifications.addAll(allNotifications);
        } else {
            for (Notification item : allNotifications) {
                if (item.getType() != null && item.getType().equalsIgnoreCase(currentFilter)) {
                    filteredNotifications.add(item);
                }
            }
        }
        adapter.updateData(filteredNotifications);
    }

    private void fetchNotifications() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getNotifications(userId).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    NotificationResponse notifResponse = response.body();
                    if ("success".equals(notifResponse.getStatus()) && notifResponse.getData() != null) {
                        allNotifications.clear();
                        allNotifications.addAll(notifResponse.getData());
                        
                        updateUnreadCountHeader();
                        applyFilter();
                    }
                } else {
                    Log.e("NotificationFragment", "API failed to fetch notifications: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Log.e("NotificationFragment", "Error calling getNotifications: " + t.getMessage());
            }
        });
    }

    private void updateUnreadCountHeader() {
        int unreadCount = 0;
        for (Notification item : allNotifications) {
            if (item.getIsRead() == 0) {
                unreadCount++;
            }
        }
        tvUnreadCount.setText(unreadCount + " thông báo chưa đọc");
    }

    private void readAllNotifications() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("userId", userId);

        apiService.readAllNotifications(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful()) {
                    // Update all local notifications as read
                    for (Notification item : allNotifications) {
                        item.setIsRead(1);
                    }
                    updateUnreadCountHeader();
                    applyFilter();
                    Toast.makeText(getContext(), "Đã đánh dấu đọc tất cả thông báo", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("NotificationFragment", "API failed to mark read-all: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Log.e("NotificationFragment", "Error calling read-all: " + t.getMessage());
            }
        });
    }
}