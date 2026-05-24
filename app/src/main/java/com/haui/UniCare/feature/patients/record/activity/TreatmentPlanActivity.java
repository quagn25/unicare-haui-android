package com.haui.UniCare.feature.patients.record.activity;

import com.haui.UniCare.core.base.BaseActivity;

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

public class TreatmentPlanActivity extends BaseActivity {

    private ImageView btnBack;
    private RecyclerView rcvTreatmentPlans;
    private LinearLayout layoutEmptyPlans;
    private LoadingDialog loadingDialog;

    private TreatmentPlanAdapter adapter;
    private List<TreatmentPlan> planList = new ArrayList<>();
    private int recordId;
    private String specialtyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_plan);

        recordId = getIntent().getIntExtra("record_id", -1);
        specialtyName = getIntent().getStringExtra("specialty_name");

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
        generateMockTreatmentPlan();
        if (planList.isEmpty()) {
            layoutEmptyPlans.setVisibility(View.VISIBLE);
            rcvTreatmentPlans.setVisibility(View.GONE);
        } else {
            showDataState();
        }
    }

    private void generateMockTreatmentPlan() {
        planList.clear();
        String specialty = specialtyName != null ? specialtyName.toLowerCase() : "";

        if (specialty.contains("tim mạch")) {
            planList.add(new TreatmentPlan(1, recordId, "Bisoprolol 5mg", "Uống", 1, "Kiểm soát nhịp tim, hạ huyết áp", "Uống vào buổi sáng, sau khi ăn."));
            planList.add(new TreatmentPlan(2, recordId, "Aspirin 81mg", "Uống", 1, "Chống kết tập tiểu cầu", "Uống sau bữa ăn tối."));
            planList.add(new TreatmentPlan(3, recordId, "Khám chuyên khoa", "Thực hiện", 0, "Siêu âm tim, ECG", "Thực hiện định kỳ 1 tháng/lần."));
        } else if (specialty.contains("nhi")) {
            planList.add(new TreatmentPlan(1, recordId, "Paracetamol 250mg", "Uống", 3, "Hạ sốt, giảm đau", "Uống khi trẻ sốt > 38.5 độ. Cách nhau 4-6 tiếng."));
            planList.add(new TreatmentPlan(2, recordId, "Oresol", "Uống", 3, "Bù nước và điện giải", "Pha đúng tỷ lệ, uống thay nước lọc."));
            planList.add(new TreatmentPlan(3, recordId, "Chế độ dinh dưỡng", "Thực hiện", 0, "Nâng cao thể trạng", "Chia nhỏ bữa ăn, thức ăn mềm, dễ tiêu."));
        } else if (specialty.contains("da liễu")) {
            planList.add(new TreatmentPlan(1, recordId, "Fucidin 2%", "Bôi ngoài da", 2, "Điều trị nhiễm khuẩn da", "Bôi lớp mỏng lên vùng da bị tổn thương sau khi làm sạch."));
            planList.add(new TreatmentPlan(2, recordId, "Cetirizine 10mg", "Uống", 1, "Giảm ngứa, chống dị ứng", "Uống buổi tối trước khi đi ngủ."));
            planList.add(new TreatmentPlan(3, recordId, "Chăm sóc da", "Thực hiện", 0, "Phục hồi da", "Tránh cào gãi, giữ vệ sinh vùng da bệnh."));
        } else if (specialty.contains("sản") || specialty.contains("phụ khoa")) {
            planList.add(new TreatmentPlan(1, recordId, "Vitamin tổng hợp cho bà bầu", "Uống", 1, "Bổ sung vitamin, khoáng chất", "Uống sau ăn sáng."));
            planList.add(new TreatmentPlan(2, recordId, "Sắt + Axit Folic", "Uống", 1, "Phòng ngừa thiếu máu", "Uống lúc đói, kèm nước cam (nếu có)."));
            planList.add(new TreatmentPlan(3, recordId, "Khám định kỳ", "Thực hiện", 0, "Theo dõi thai kỳ", "Siêu âm theo chỉ định bác sĩ."));
        } else if (specialty.contains("nội tiết")) {
            planList.add(new TreatmentPlan(1, recordId, "Metformin 500mg", "Uống", 2, "Kiểm soát đường huyết", "Uống ngay trong hoặc sau bữa ăn."));
            planList.add(new TreatmentPlan(2, recordId, "Chế độ ăn", "Thực hiện", 0, "Kiểm soát đường huyết", "Hạn chế tinh bột, đồ ngọt. Tăng cường rau xanh."));
            planList.add(new TreatmentPlan(3, recordId, "Theo dõi", "Thực hiện", 0, "Đánh giá hiệu quả", "Đo đường huyết tại nhà lúc đói."));
        } else if (specialty.contains("tiêu hóa")) {
            planList.add(new TreatmentPlan(1, recordId, "Omeprazole 20mg", "Uống", 1, "Giảm tiết axit dạ dày", "Uống trước bữa ăn sáng 30 phút."));
            planList.add(new TreatmentPlan(2, recordId, "Phosphalugel", "Uống", 2, "Bảo vệ niêm mạc dạ dày", "Uống khi có cơn đau hoặc sau ăn 1-2 giờ."));
            planList.add(new TreatmentPlan(3, recordId, "Lối sống", "Thực hiện", 0, "Hỗ trợ điều trị", "Không thức khuya, kiêng rượu bia, đồ cay nóng."));
        } else if (specialty.contains("thần kinh")) {
            planList.add(new TreatmentPlan(1, recordId, "Gabapentin 300mg", "Uống", 1, "Giảm đau thần kinh", "Uống buổi tối trước khi đi ngủ."));
            planList.add(new TreatmentPlan(2, recordId, "Vitamin 3B (B1, B6, B12)", "Uống", 2, "Hỗ trợ thần kinh", "Uống sau bữa ăn."));
            planList.add(new TreatmentPlan(3, recordId, "Phục hồi chức năng", "Thực hiện", 0, "Cải thiện vận động", "Tập vật lý trị liệu theo hướng dẫn."));
        } else if (specialty.contains("răng hàm mặt") || specialty.contains("nha khoa")) {
            planList.add(new TreatmentPlan(1, recordId, "Ibuprofen 400mg", "Uống", 2, "Giảm đau, kháng viêm", "Uống sau khi ăn no, chỉ khi đau."));
            planList.add(new TreatmentPlan(2, recordId, "Nước súc miệng Chlorhexidine", "Súc miệng", 2, "Kháng khuẩn vùng miệng", "Súc miệng sau khi đánh răng, không nuốt."));
            planList.add(new TreatmentPlan(3, recordId, "Vệ sinh răng miệng", "Thực hiện", 0, "Phòng ngừa sâu răng", "Đánh răng ngày 2 lần, dùng chỉ nha khoa."));
        } else if (specialty.contains("mắt") || specialty.contains("nhãn khoa")) {
            planList.add(new TreatmentPlan(1, recordId, "Nước mắt nhân tạo (Refresh Tears)", "Nhỏ mắt", 4, "Giảm khô mắt", "Nhỏ mỗi bên 1 giọt khi thấy khô, mỏi mắt."));
            planList.add(new TreatmentPlan(2, recordId, "Kính bảo hộ", "Đeo", 0, "Bảo vệ mắt", "Đeo kính râm khi ra nắng, hạn chế nhìn màn hình quá lâu."));
        } else {
            planList.add(new TreatmentPlan(1, recordId, "Theo dõi sức khỏe", "Thực hiện", 1, "Đánh giá tình trạng", "Theo dõi triệu chứng tại nhà."));
            planList.add(new TreatmentPlan(2, recordId, "Chế độ sinh hoạt", "Thực hiện", 0, "Nâng cao sức khỏe", "Ăn uống đầy đủ, nghỉ ngơi hợp lý."));
            planList.add(new TreatmentPlan(3, recordId, "Tái khám", "Thực hiện", 0, "Đánh giá lại", "Tái khám sau 1 tuần hoặc khi có dấu hiệu bất thường."));
        }
        
        adapter.setData(planList);
    }

    private void showDataState() {
        layoutEmptyPlans.setVisibility(View.GONE);
        rcvTreatmentPlans.setVisibility(View.VISIBLE);
    }
}
