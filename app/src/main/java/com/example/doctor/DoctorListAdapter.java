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

import com.example.doctor.R;
import com.example.doctor.Doctor;
import com.example.doctor.DoctorDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

/**
 * Adapter cho danh sách đầy đủ bác sĩ (DoctorListActivity)
 */
public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> doctors;

    public DoctorListAdapter(Context context, List<Doctor> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public DoctorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_doctor_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListAdapter.ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);

        holder.tvTitle.setText(doctor.getTitle());
        holder.tvName.setText(doctor.getName());
        holder.tvExperience.setText(doctor.getExperience());
        holder.tvAddress.setText(doctor.getAddress());
        holder.imgAvatar.setImageResource(doctor.getAvatarResId());

        // Thêm chip chuyên khoa (an toàn null)
        holder.chipGroupSpecialties.removeAllViews();
        if (doctor.getSpecialties() != null) {
            for (String specialty : doctor.getSpecialties()) {
                Chip chip = new Chip(context);
                chip.setText(specialty);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.chip_background);

                // Fix crash với API thấp
                chip.setTextColor(context.getResources().getColor(R.color.chip_text));

                chip.setTextSize(12f);
                holder.chipGroupSpecialties.addView(chip);
            }
        }

        // Nút đặt lịch
        holder.btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctor_id", doctor.getId());
            intent.putExtra("scroll_to_booking", true);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // tránh crash context
            context.startActivity(intent);
        });

        // Click item -> xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctor_id", doctor.getId());

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return doctors != null ? doctors.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvTitle, tvName, tvExperience, tvAddress;
        ChipGroup chipGroupSpecialties;
        MaterialButton btnBooking;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDoctorAvatar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            chipGroupSpecialties = itemView.findViewById(R.id.chipGroupSpecialties);
            btnBooking = itemView.findViewById(R.id.btnBooking);
        }
    }
}