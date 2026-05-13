package com.example.doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> doctors;

    public DoctorListAdapter(Context context, List<Doctor> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_doctor_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);

        if (holder.tvDoctorTitle   != null) holder.tvDoctorTitle.setText(doctor.getTitle());
        if (holder.tvDoctorName    != null) holder.tvDoctorName.setText(doctor.getName());
        if (holder.tvDoctorExperience != null) holder.tvDoctorExperience.setText(doctor.getExperience());
        if (holder.tvDoctorAddress != null) holder.tvDoctorAddress.setText(doctor.getAddress());
        if (holder.imgAvatar       != null) holder.imgAvatar.setImageResource(doctor.getAvatarResId());

        // Chip chuyên khoa
        if (holder.chipGroup != null) {
            holder.chipGroup.removeAllViews();
            for (String s : doctor.getSpecialties()) {
                Chip chip = new Chip(context);
                chip.setText(s);
                chip.setClickable(false);
                chip.setCheckable(false);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setTextColor(context.getColor(R.color.chip_text));
                chip.setTextSize(12f);
                holder.chipGroup.addView(chip);
            }
        }

        // Nút đặt lịch → mở DoctorDetailActivity
        if (holder.btnBooking != null) {
            holder.btnBooking.setOnClickListener(v -> {
                Intent intent = new Intent(context, DoctorDetailActivity.class);
                intent.putExtra("doctor_id", doctor.getId());
                intent.putExtra("scroll_to_booking", true);
                context.startActivity(intent);
            });
        }

        // Click item → xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctor_id", doctor.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return doctors.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvDoctorTitle, tvDoctorName, tvDoctorExperience, tvDoctorAddress;
        ChipGroup chipGroup;
        MaterialButton btnBooking;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Dùng null check để không crash nếu id không tồn tại
            imgAvatar         = itemView.findViewById(R.id.imgDoctorAvatar);
            tvDoctorTitle     = itemView.findViewById(R.id.tvDoctorTitle);
            tvDoctorName      = itemView.findViewById(R.id.tvDoctorName);
            tvDoctorExperience= itemView.findViewById(R.id.tvDoctorExperience);
            tvDoctorAddress   = itemView.findViewById(R.id.tvDoctorAddress);
            chipGroup         = itemView.findViewById(R.id.chipGroupSpecialties);
            btnBooking        = itemView.findViewById(R.id.btnBooking);
        }
    }
}