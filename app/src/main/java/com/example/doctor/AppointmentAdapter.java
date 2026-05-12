package com.example.doctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> list;

    public AppointmentAdapter(Context context, List<Appointment> list) {
        this.context = context;
        this.list    = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment a = list.get(position);
        holder.tvDoctorName.setText(a.getDoctorName());
        holder.tvSpecialty .setText(a.getSpecialty());
        holder.tvDate      .setText(a.getDate());
        holder.tvTimeSlot  .setText(a.getTimeSlot());
        holder.tvAddress   .setText(a.getAddress());
        holder.tvFee       .setText("Phí: " + a.getFee() + "/lượt");
        holder.tvStatus    .setText("Chờ xác nhận");
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvDate, tvTimeSlot, tvAddress, tvFee, tvStatus;
        ViewHolder(@NonNull View v) {
            super(v);
            tvDoctorName = v.findViewById(R.id.tvDoctorName);
            tvSpecialty  = v.findViewById(R.id.tvSpecialty);
            tvDate       = v.findViewById(R.id.tvDate);
            tvTimeSlot   = v.findViewById(R.id.tvTimeSlot);
            tvAddress    = v.findViewById(R.id.tvAddress);
            tvFee        = v.findViewById(R.id.tvFee);
            tvStatus     = v.findViewById(R.id.tvStatus);
        }
    }
}