package com.haui.UniCare.feature.patients.record.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.core.common_ui.LoadingDialog;
import com.haui.UniCare.core.network.ApiService;
import com.haui.UniCare.core.network.RetrofitClient;
import com.haui.UniCare.data.model.TreatmentPlansResponse;
import com.haui.UniCare.data.model.table.TreatmentPlan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreatmentPlanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rcvTreatmentPlans;
    private LinearLayout layoutEmptyPlans;
    private LoadingDialog loadingDialog;

    private TreatmentPlanAdapter adapter;
    private List<TreatmentPlan> planList = new ArrayList<>();
    private int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_plan);

        recordId = getIntent().getIntExtra("record_id", -1);

        loadingDialog = new LoadingDialog(this);
        initViews();
        setupRecyclerView();
        setupEvents();

        if (recordId <= 0) {
            showEmptyState();
        } else {
            loadTreatmentPlans();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rcvTreatmentPlans = findViewById(R.id.rcv_treatment_plans);
        layoutEmptyPlans = findViewById(R.id.layout_empty_plans);
    }

    private void setupRecyclerView() {
        adapter = new TreatmentPlanAdapter(planList);
        rcvTreatmentPlans.setLayoutManager(new LinearLayoutManager(this));
        rcvTreatmentPlans.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTreatmentPlans() {
        loadingDialog.showLoading();
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getTreatmentPlans(recordId).enqueue(new Callback<TreatmentPlansResponse>() {
            @Override
            public void onResponse(Call<TreatmentPlansResponse> call, Response<TreatmentPlansResponse> response) {
                loadingDialog.hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    List<TreatmentPlan> serverList = response.body().data;
                    if (serverList != null && !serverList.isEmpty()) {
                        planList.clear();
                        planList.addAll(serverList);
                        adapter.setData(planList);
                        showDataState();
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<TreatmentPlansResponse> call, Throwable t) {
                loadingDialog.hideLoading();
                showEmptyState();
                Toast.makeText(TreatmentPlanActivity.this, "Không thể tải phác đồ từ máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        layoutEmptyPlans.setVisibility(View.VISIBLE);
        rcvTreatmentPlans.setVisibility(View.GONE);
    }

    private void showDataState() {
        layoutEmptyPlans.setVisibility(View.GONE);
        rcvTreatmentPlans.setVisibility(View.VISIBLE);
    }
}
