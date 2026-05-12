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

import java.util.List;

public class HomeDoctorAdapter extends RecyclerView.Adapter<HomeDoctorAdapter.ViewHolder> {

    private Context context;
    private List<Doctor> doctors;

    public HomeDoctorAdapter(Context context, List<Doctor> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_doctor_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);

        // Chỉ lấy tên cuối: "Bác sĩ Tuấn"
        String[] parts = doctor.getName().split(" ");
        holder.tvName.setText("Bác sĩ " + parts[parts.length - 1]);
        holder.imgAvatar.setImageResource(doctor.getAvatarResId());

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
        TextView tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgDoctorAvatar);
            tvName    = itemView.findViewById(R.id.tvDoctorName);
        }
    }
}