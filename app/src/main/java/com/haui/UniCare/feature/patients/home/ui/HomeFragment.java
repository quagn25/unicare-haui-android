package com.haui.UniCare.feature.patients.home.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.haui.UniCare.R;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorDetailActivity;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorListActivity;
import com.haui.UniCare.feature.patients.home.adapter.BannerAdapter;
import com.haui.UniCare.feature.patients.home.adapter.DoctorHomeAdapter;
import com.haui.UniCare.feature.patients.home.adapter.SpecialtyAdapter;
import com.haui.UniCare.data.model.table.Doctor;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.MockData;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private LinearLayout layoutIndicator;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private List<Integer> bannerList;

    private RecyclerView recyclerView;
    private SpecialtyAdapter specialtyadapter;
    private ArrayList<specialty> list;
    
    private TextView tvUserNameHome;
    private LinearLayout btnBookDoctor;
    private LinearLayout btnVaccineTab;
    private LinearLayout btnProfileTab;
    private EditText etSearchHome;

    private RecyclerView rvHomeDoctors;
    private DoctorHomeAdapter doctorHomeAdapter;
    private List<Doctor> homeDoctorList;
    
    private View scrollbarContainer;
    private View scrollbarThumb;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ View
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvUserNameHome = view.findViewById(R.id.tvUserNameHome);
        btnBookDoctor = view.findViewById(R.id.linearLayout); 
        btnVaccineTab = view.findViewById(R.id.linearLayout2);
        btnProfileTab = view.findViewById(R.id.linearLayout3);
        etSearchHome = view.findViewById(R.id.etSearchHome);
        
        scrollbarContainer = view.findViewById(R.id.scrollbarContainer);
        scrollbarThumb = view.findViewById(R.id.scrollbarThumb);
        
        // Lấy tên người dùng từ SharedPreferences và hiển thị
        displayUserInfo();

        viewPager2 = view.findViewById(R.id.viewPagerBanner);
        layoutIndicator = view.findViewById(R.id.layoutIndicator);

        // Xử lý sự kiện khi bấm vào ô tìm kiếm
        if (etSearchHome != null) {
            etSearchHome.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DoctorListActivity.class);
                startActivity(intent);
            });
        }

        // Dữ liệu mẫu cho Banner
        bannerList = new ArrayList<>();
        bannerList.add(R.drawable.banner1);
        bannerList.add(R.drawable.banner2);
        bannerList.add(R.drawable.banner3);

        BannerAdapter adapter = new BannerAdapter(bannerList);
        viewPager2.setAdapter(adapter);

        // Thiết lập Indicators
        setupIndicators(bannerList.size());
        setCurrentIndicator(0);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        list = new ArrayList<>();
        specialtyadapter = new SpecialtyAdapter(list, getContext());
        list.add(new specialty("Tổng quát",R.drawable.tongquat));
        list.add(new specialty("Nha khoa",R.drawable.rang));
        list.add(new specialty("Tim mạch",R.drawable.tim));
        list.add(new specialty("Da liễu",R.drawable.dalieu));
        list.add(new specialty("Nhãn khoa",R.drawable.nhankhoa));
        list.add(new specialty("Xét nghiệm",R.drawable.xetnghiem));
        
        recyclerView.setAdapter(specialtyadapter); 
        recyclerView.setNestedScrollingEnabled(false);

        // Khởi tạo RecyclerView Bác sĩ nổi bật
        rvHomeDoctors = view.findViewById(R.id.rvHomeDoctors);
        rvHomeDoctors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        homeDoctorList = new ArrayList<>();
        doctorHomeAdapter = new DoctorHomeAdapter(homeDoctorList);
        rvHomeDoctors.setAdapter(doctorHomeAdapter);

        doctorHomeAdapter.setOnItemClickListener(doctor -> {
            Intent intent = new Intent(getActivity(), DoctorDetailActivity.class);
            intent.putExtra("doctor_data", doctor);
            startActivity(intent);
        });

        // Xử lý thanh trượt (slider progress) khi cuộn danh sách bác sĩ
        rvHomeDoctors.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                int offset = recyclerView.computeHorizontalScrollOffset();
                int extent = recyclerView.computeHorizontalScrollExtent();
                int range = recyclerView.computeHorizontalScrollRange();
                
                int maxOffset = range - extent;
                if (maxOffset > 0 && scrollbarContainer != null && scrollbarThumb != null) {
                    float progress = (float) offset / maxOffset;
                    
                    int trackWidth = scrollbarContainer.getWidth();
                    int thumbWidth = scrollbarThumb.getWidth();
                    int maxTranslate = trackWidth - thumbWidth;
                    
                    if (maxTranslate > 0) {
                        scrollbarThumb.setTranslationX(progress * maxTranslate);
                    }
                }
            }
        });

        // Load dữ liệu bác sĩ (Dùng MockData nếu là bản Debug)
        loadHomeDoctors();

        // Xử lý sự kiện click chuyển sang Danh sách bác sĩ
        btnBookDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click Lịch tiêm (linearLayout2)
        if (btnVaccineTab != null) {
            btnVaccineTab.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("select_tab", "schedule");
                intent.putExtra("active_tab", "vaccine");
                startActivity(intent);
            });
        }

        // Xử lý sự kiện click Hồ sơ (linearLayout3) chuyển thẳng sang ProfileActivity
        if (btnProfileTab != null) {
            btnProfileTab.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.haui.UniCare.feature.patients.profile.ui.ProfileActivity.class);
                startActivity(intent);
            });
        }

        // Xử lý sự kiện click cho Specialties
        specialtyadapter.setOnItemClickListener(specialty -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            intent.putExtra("specialty_name", specialty.getName());
            startActivity(intent);
        });
    }

    private void loadHomeDoctors() {
        if (AppConstants.USE_MOCK_DATA) {
            // Chế độ Dev: Dùng dữ liệu mẫu
            homeDoctorList.clear();
            homeDoctorList.addAll(MockData.getMockDoctors());
            doctorHomeAdapter.notifyDataSetChanged();
        } else {
            // Chế độ Production: Gọi API (ở đây tạm thời chưa có API riêng cho Home, dùng chung API lấy tất cả)
            fetchDoctorsFromServer();
        }
    }

    private void fetchDoctorsFromServer() {
        com.haui.UniCare.core.network.ApiService apiService = com.haui.UniCare.core.network.RetrofitClient.getInstance().create(com.haui.UniCare.core.network.ApiService.class);
        apiService.getDoctors().enqueue(new retrofit2.Callback<List<Doctor>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Doctor>> call, retrofit2.Response<List<Doctor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    homeDoctorList.clear();
                    homeDoctorList.addAll(response.body());
                    doctorHomeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Doctor>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    private void displayUserInfo() {
        if (getContext() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            String fullName = sharedPref.getString("fullName", "Người dùng");
            tvUserNameHome.setText(fullName);
            
            TextView tvAvatarInitials = getView() != null ? getView().findViewById(R.id.tvAvatarInitials) : null;
            if (tvAvatarInitials != null) {
                tvAvatarInitials.setText(getInitials(fullName));
            }
        }
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "U";
        String[] words = fullName.trim().split("\\s+");
        if (words.length == 0) return "U";
        if (words.length == 1) {
            return words[0].substring(0, Math.min(2, words[0].length())).toUpperCase();
        }
        String first = words[0].substring(0, 1);
        String last = words[words.length - 1].substring(0, 1);
        return (first + last).toUpperCase();
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager2 != null && bannerList != null && !bannerList.isEmpty()) {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerList.size();
                viewPager2.setCurrentItem(nextItem, true);
            }
        }
    };

    private void setupIndicators(int count) {
        if (getContext() == null) return;
        
        layoutIndicator.removeAllViews();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);
        
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(requireContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        if (getContext() == null) return;

        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (imageView != null) {
                if (i == index) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active));
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive));
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2500);
    }
}
