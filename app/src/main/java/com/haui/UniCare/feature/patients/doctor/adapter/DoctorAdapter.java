package com.haui.UniCare.feature.patients.doctor.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Doctor;
import com.haui.UniCare.feature.patients.doctor.ui.DoctorDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private List<Doctor> doctorListFull;

    public DoctorAdapter(List<Doctor> doctorList) {
        this.doctorList = doctorList;
        this.doctorListFull = new ArrayList<>(doctorList);
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        this.doctorListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<Doctor> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(doctorListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Doctor item : doctorListFull) {
                if (item.getName().toLowerCase().contains(filterPattern) || 
                    item.getSpecialties().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        this.doctorList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_list, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        if (doctor == null) return;

        // Hiển thị thông tin text
        holder.tvDegree.setText(doctor.getDegree());
        holder.tvName.setText(doctor.getName());
        holder.tvExperience.setText(doctor.getExperienceText());
        holder.tvAddress.setText(doctor.getAddress());

        // Xử lý ảnh đại diện
        if (doctor.getAvatarUrl() != null && !doctor.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getAvatarUrl())
                    .placeholder(R.drawable.doctorbook)
                    .error(R.drawable.doctorbook)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(doctor.getAvatarResource() != 0 
                    ? doctor.getAvatarResource() : R.drawable.doctorbook);
        }

        // Sự kiện khi bấm nút Đặt lịch ngay mới chuyển sang trang chi tiết bác sĩ
        holder.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DoctorDetailActivity.class);
            intent.putExtra("doctor_data", doctor);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvDegree, tvName, tvExperience, tvAddress;
        Button btnBookNow;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDoctorAvatar);
            tvDegree = itemView.findViewById(R.id.tvDoctorTitle);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvExperience = itemView.findViewById(R.id.tvDoctorExperience);
            tvAddress = itemView.findViewById(R.id.tvDoctorAddress);
            btnBookNow = itemView.findViewById(R.id.btnBooking);
        }
    }
}
