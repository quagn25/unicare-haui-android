package com.haui.UniCare.feature.patients.appointment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments;

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void setData(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        
        holder.tvDoctorName.setText(appointment.doctorName != null ? appointment.doctorName : "Bác sĩ");
        holder.tvStatus.setText(appointment.status);
        holder.tvSpecialty.setText(appointment.specialtyName != null ? appointment.specialtyName : "Chuyên khoa");
        holder.tvAddress.setText(appointment.workplaceAddress != null ? appointment.workplaceAddress : "Địa chỉ đang cập nhật");
        holder.tvFee.setText(String.format("%,.0f VNĐ", appointment.consultationFee));
        
        if (appointment.appointmentDatetime != null && !appointment.appointmentDatetime.isEmpty()) {
            // Giả sử định dạng từ server là "yyyy-MM-dd HH:mm:ss" hoặc tương tự
            String[] parts = appointment.appointmentDatetime.split(" ");
            holder.tvDate.setText(parts[0]);
            if (parts.length > 1) {
                holder.tvTime.setText(parts[1]);
            } else {
                holder.tvTime.setText("");
            }
        } else {
            holder.tvDate.setText("Chưa xác định");
            holder.tvTime.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvStatus, tvDate, tvTime, tvAddress, tvFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTimeSlot);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvFee = itemView.findViewById(R.id.tvFee);
        }
    }
}
