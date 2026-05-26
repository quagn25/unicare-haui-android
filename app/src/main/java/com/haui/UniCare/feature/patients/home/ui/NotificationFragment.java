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
import com.haui.UniCare.data.MockData;
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

public class NotificationFragment extends Fragment implements NotificationAdapter.OnNotificationDeleteListener, NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private TextView tvUnreadCount;
    private MaterialCardView btnReadAll;

    // Filters Tabs
    private MaterialCardView tabAll, tabLichKham, tabTiemChung, tabKetQua;
    private TextView tvTabAll, tvTabLichKham, tvTabTiemChung, tvTabKetQua;

    private NotificationAdapter adapter;
    private List<Notification> allNotifications = new ArrayList<>();
    private List<Notification> filteredNotifications = new ArrayList<>();

    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
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

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    fetchNotifications();
                    swipeRefreshLayout.setRefreshing(false);
                }, 1000);
            });
        }
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        adapter = new NotificationAdapter(filteredNotifications, getContext());
        adapter.setDeleteListener(this);
        adapter.setClickListener(this);
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
        tabAll.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabAll.setTextColor(Color.parseColor("#475569"));
        tabLichKham.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabLichKham.setTextColor(Color.parseColor("#475569"));
        tabTiemChung.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabTiemChung.setTextColor(Color.parseColor("#475569"));
        tabKetQua.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTabKetQua.setTextColor(Color.parseColor("#475569"));

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
        if (AppConstants.USE_MOCK_DATA) {
            allNotifications.clear();
            allNotifications.addAll(MockData.getMockNotifications());
            updateUnreadCountHeader();
            applyFilter();
            return;
        }

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

        if (getActivity() instanceof com.haui.UniCare.MainActivity) {
            ((com.haui.UniCare.MainActivity) getActivity()).updateNotificationBadge(unreadCount);
        }
    }

    private void readAllNotifications() {
        if (AppConstants.USE_MOCK_DATA) {
            for (Notification item : allNotifications) item.setIsRead(1);
            updateUnreadCountHeader();
            applyFilter();
            Toast.makeText(getContext(), "Đã đánh dấu đọc tất cả (Mock Mode)", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("userId", userId);
        apiService.readAllNotifications(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    for (Notification item : allNotifications) item.setIsRead(1);
                    updateUnreadCountHeader();
                    applyFilter();
                    Toast.makeText(getContext(), "Đã đánh dấu đọc tất cả thông báo", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void onItemClick(Notification notification) {
        if (notification.getIsRead() == 1) {
            return; // Already read
        }

        if (AppConstants.USE_MOCK_DATA) {
            notification.setIsRead(1);
            adapter.notifyDataSetChanged();
            updateUnreadCountHeader();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("notificationId", notification.getId());
        apiService.readNotification(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    notification.setIsRead(1);
                    adapter.notifyDataSetChanged();
                    updateUnreadCountHeader();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                // Ignore error
            }
        });
    }

    @Override
    public void onDeleteClick(Notification notification) {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_custom_confirm);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        android.view.View btnCloseIcon = dialog.findViewById(R.id.btnCloseIcon);
        if (btnCloseIcon != null) {
            btnCloseIcon.setOnClickListener(btnV -> dialog.dismiss());
        }

        android.widget.TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Xác nhận xóa");
        
        android.widget.TextView tvMessage = dialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText("Bạn có chắc chắn muốn xóa thông báo này không?");

        com.google.android.material.button.MaterialButton btnPrimary = dialog.findViewById(R.id.btnPrimary);
        btnPrimary.setText("Đồng ý");
        btnPrimary.setOnClickListener(btnV -> {
            dialog.dismiss();
            performDeleteNotification(notification);
        });

        com.google.android.material.button.MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Hủy");
        btnCancel.setOnClickListener(btnV -> dialog.dismiss());

        dialog.show();
    }

    private void performDeleteNotification(Notification notification) {
        if (AppConstants.USE_MOCK_DATA) {
            MockData.removeMockNotification(notification.getId());
            
            // Xóa bằng tay trong list do không có method removeIf trên API cũ
            for (int i = 0; i < allNotifications.size(); i++) {
                if (allNotifications.get(i).getId() == notification.getId()) {
                    allNotifications.remove(i);
                    break;
                }
            }
            
            updateUnreadCountHeader();
            applyFilter();
            Toast.makeText(getContext(), "Đã xóa thông báo (Mock Mode)", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Map<String, Integer> body = new HashMap<>();
        body.put("notificationId", notification.getId());
        apiService.deleteNotification(body).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call, @NonNull Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < allNotifications.size(); i++) {
                        if (allNotifications.get(i).getId() == notification.getId()) {
                            allNotifications.remove(i);
                            break;
                        }
                    }
                    updateUnreadCountHeader();
                    applyFilter();
                    Toast.makeText(getContext(), "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa thông báo", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
