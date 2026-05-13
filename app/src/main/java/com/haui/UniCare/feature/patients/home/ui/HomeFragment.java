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
import com.haui.UniCare.feature.patients.doctor.ui.DoctorListActivity;
import com.haui.UniCare.feature.patients.home.adapter.BannerAdapter;
import com.haui.UniCare.feature.patients.home.adapter.SpecialtyAdapter;

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
    private EditText etSearchHome;

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
        tvUserNameHome = view.findViewById(R.id.tvUserNameHome);
        btnBookDoctor = view.findViewById(R.id.linearLayout); 
        etSearchHome = view.findViewById(R.id.etSearchHome);
        
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

        // Xử lý sự kiện click chuyển sang Danh sách bác sĩ
        btnBookDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            startActivity(intent);
        });
    }

    private void displayUserInfo() {
        if (getContext() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("UniCarePrefs", Context.MODE_PRIVATE);
            String fullName = sharedPref.getString("fullName", "Người dùng");
            tvUserNameHome.setText(fullName);
        }
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
