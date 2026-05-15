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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDoctorAvatar);
            tvName = itemView.findViewById(R.id.tvDoctorName);
        }
    }
}
