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
import android.widget.PopupWindow;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.haui.UniCare.R;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorDetailActivity;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorListActivity;
import com.haui.UniCare.feature.patients.home.adapter.BannerAdapter;
import com.haui.UniCare.feature.patients.home.adapter.DoctorHomeAdapter;
import com.haui.UniCare.feature.patients.home.adapter.SpecialtyAdapter;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.core.utils.AppConstants;
import com.haui.UniCare.data.MockData;
import com.haui.UniCare.feature.patients.profile.ui.FilePerson;

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
    private LinearLayout btnBookDoctor, btnProfile;
    private EditText etSearchHome;

    private RecyclerView rvHomeDoctors;
    private DoctorHomeAdapter doctorHomeAdapter;
    private List<Doctor> homeDoctorList;
    
    private MaterialCardView profileCard;
    private ImageView ivExpandArrow;
    private PopupWindow profilePopupWindow;
    private LinearLayout headerLayout;
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
        btnProfile = view.findViewById(R.id.linearLayout3);
        profileCard = view.findViewById(R.id.profileCard);
        ivExpandArrow = view.findViewById(R.id.ivExpandArrow);

        // Lấy tên người dùng từ SharedPreferences và hiển thị
        displayUserInfo();

        viewPager2 = view.findViewById(R.id.viewPagerBanner);
        layoutIndicator = view.findViewById(R.id.layoutIndicator);

        // Sự kiện click profile để hiện dropdown
        profileCard.setOnClickListener(v -> showProfileDropdown());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FilePerson.class);
            startActivity(intent);
        });

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

        // Load dữ liệu bác sĩ
        loadHomeDoctors();

        // Xử lý sự kiện click chuyển sang Danh sách bác sĩ
        btnBookDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện click cho Specialties
        specialtyadapter.setOnItemClickListener(specialty -> {
            Intent intent = new Intent(getActivity(), DoctorListActivity.class);
            intent.putExtra("specialty_name", specialty.getName());
            startActivity(intent);
        });
    }

    private void showProfileDropdown() {
        if (getContext() == null || profileCard == null) return;

        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.layout_profile_dropdown, null);

        // Tính toán chiều rộng: Lấy chiều rộng của headerLayout
        int width = profileCard.getWidth();

        profilePopupWindow = new PopupWindow(
                popupView,
                width, // Set width bằng headerLayout
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Vì profileCard có lề (padding của headerLayout là 20dp),
        // ta cần dịch ngược lại sang trái để panel khớp hoàn toàn với headerLayout
        int xOffset = - (int) (getContext().getResources().getDisplayMetrics().density);

        popupView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dropdown_in));
        ivExpandArrow.animate().rotation(180).setDuration(300).start();

        profilePopupWindow.setOnDismissListener(() -> {
            ivExpandArrow.animate().rotation(0).setDuration(300).start();
        });

        // Đổ dữ liệu từ SharedPreferences "UserPrefs" (giống FilePerson) vào popup
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        
        TextView tvName = popupView.findViewById(R.id.textView4);
        TextView tvDob = popupView.findViewById(R.id.textView6);
        TextView tvGender = popupView.findViewById(R.id.textView8);
        TextView tvPhone = popupView.findViewById(R.id.textView10);
        TextView tvAddress = popupView.findViewById(R.id.textView12);
        TextView tvEmail = popupView.findViewById(R.id.textView14);

        if (tvName != null) tvName.setText(sharedPref.getString("name", "Nguyễn Văn An"));
        if (tvDob != null) tvDob.setText(sharedPref.getString("dob", "09/11/2005"));
        if (tvGender != null) tvGender.setText(sharedPref.getString("gender", "Nam"));
        if (tvPhone != null) tvPhone.setText(sharedPref.getString("phone", "0392817228"));
        if (tvAddress != null) tvAddress.setText(sharedPref.getString("address", "Chưa cập nhật"));
        if (tvEmail != null) tvEmail.setText(sharedPref.getString("email", "Chưa cập nhật"));

        profilePopupWindow.setElevation(20f);
        // Hiển thị: xOffset giúp nó căn lề sát mép màn hình giống headerLayout
        profilePopupWindow.showAsDropDown(profileCard, xOffset, 10);
    }

    private void loadHomeDoctors() {
        if (AppConstants.USE_MOCK_DATA) {
            homeDoctorList.clear();
            homeDoctorList.addAll(MockData.getMockDoctors());
            doctorHomeAdapter.notifyDataSetChanged();
        } else {
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
            public void onFailure(retrofit2.Call<List<Doctor>> call, Throwable t) {}
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
