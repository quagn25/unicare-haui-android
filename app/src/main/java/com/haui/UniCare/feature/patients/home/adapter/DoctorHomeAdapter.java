package com.haui.UniCare.feature.patients.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Doctor;

import java.util.List;

public class DoctorHomeAdapter extends RecyclerView.Adapter<DoctorHomeAdapter.ViewHolder> {

    private final List<Doctor> doctorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Doctor doctor);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public DoctorHomeAdapter(List<Doctor> doctorList) {
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        if (doctor == null) return;

        holder.tvName.setText(doctor.getName());
        
        // Bind degree and specialty
        String degree = doctor.getDegree();
        String spec = doctor.getSpecialties();
        String fullSpec = "";
        if (degree != null && !degree.isEmpty()) {
            fullSpec += degree;
        }
        if (spec != null && !spec.isEmpty()) {
            if (!fullSpec.isEmpty()) {
                fullSpec += " ";
            }
            fullSpec += spec;
        }
        if (fullSpec.isEmpty()) {
            fullSpec = "Bác sĩ";
        }
        holder.tvSpecialty.setText(fullSpec);

        // Bind experience text
        holder.tvExperience.setText(doctor.getExperienceYears() + " năm KN");

        // Bind mock dynamic rating (e.g. 4.9, 4.8, 4.7) based on doctor ID
        double rating = 4.9 - (doctor.getId() % 3) * 0.1;
        holder.tvRating.setText(String.format(java.util.Locale.getDefault(), "%.1f", rating));
        
        // Load avatar using Glide
        if (doctor.getAvatarUrl() != null && !doctor.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(doctor.getAvatarUrl())
                    .placeholder(R.drawable.ic_doctor_placeholder)
                    .error(R.drawable.ic_doctor_placeholder)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(doctor.getAvatarResource() != 0 
                    ? doctor.getAvatarResource() : R.drawable.ic_doctor_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList.clear();
        this.doctorList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgAvatar;
        TextView tvName;
        TextView tvSpecialty;
        TextView tvRating;
        TextView tvExperience;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDoctorAvatar);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvDoctorSpecialty);
            tvRating = itemView.findViewById(R.id.tvDoctorRating);
            tvExperience = itemView.findViewById(R.id.tvDoctorExperience);
        }
    }
}
