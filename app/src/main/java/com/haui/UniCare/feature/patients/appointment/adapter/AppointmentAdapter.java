package com.haui.UniCare.feature.patients.appointment.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Appointment;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentActionListener actionListener;

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void setActionListener(OnAppointmentActionListener actionListener) {
        this.actionListener = actionListener;
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
        
        // Parse ngày tháng và thời gian
        String dateStr = appointment.appointmentDatetime;
        String monthText = "Tháng 6";
        String dayText = "1";
        String timeText = "09:00";
        
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                // Chuẩn hóa định dạng ISO-8601 sang "YYYY-MM-DD HH:mm:ss"
                dateStr = dateStr.replace("T", " ");
                if (dateStr.contains(".")) {
                    dateStr = dateStr.substring(0, dateStr.indexOf("."));
                }
                dateStr = dateStr.replace("Z", "").trim();

                if (dateStr.contains(" ")) {
                    String[] parts = dateStr.split(" ");
                    String datePart = parts[0]; // yyyy-MM-dd
                    String timePart = parts[1]; // HH:mm:ss
                    
                    if (timePart.contains(":")) {
                        String[] timeParts = timePart.split(":");
                        if (timeParts.length >= 2) {
                            String hour = timeParts[0];
                            String minute = timeParts[1];
                            if (hour.length() == 1) {
                                hour = "0" + hour;
                            }
                            if (minute.length() == 1) {
                                minute = "0" + minute;
                            }
                            timeText = hour + ":" + minute;
                        }
                    }
                    
                    if (datePart.contains("-")) {
                        String[] dateParts = datePart.split("-");
                        if (dateParts.length >= 3) {
                            int m = Integer.parseInt(dateParts[1]);
                            int d = Integer.parseInt(dateParts[2]);
                            monthText = "Tháng " + m;
                            dayText = String.valueOf(d);
                        }
                    }
                } else if (dateStr.contains("-")) {
                    String[] dateParts = dateStr.split("-");
                    if (dateParts.length >= 3) {
                        int m = Integer.parseInt(dateParts[1]);
                        String dayStr = dateParts[2];
                        if (dayStr.contains(" ")) {
                            dayStr = dayStr.split(" ")[0];
                        }
                        int d = Integer.parseInt(dayStr.trim());
                        monthText = "Tháng " + m;
                        dayText = String.valueOf(d);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        holder.tvVaccineMonth.setText(monthText);
        holder.tvVaccineDay.setText(dayText);
        
        // Phân loại Doctor vs Vaccine dựa trên tiền tố "Vắc-xin"
        boolean isVaccine = appointment.doctorName != null && appointment.doctorName.startsWith("Vắc-xin");
        
        if (isVaccine) {
            // Thiết lập vaccine item
            holder.tvVaccineName.setText(appointment.doctorName);
            holder.ivVaccineExtraIcon.setImageResource(R.drawable.ic_action_vaccine);
            
            String doseInfo = appointment.doctorTitle != null ? appointment.doctorTitle : 
                             (appointment.note != null ? appointment.note : "Liều nhắc lại");
            holder.tvVaccineNote.setText(doseInfo);
        } else {
            // Thiết lập doctor item
            String bio = appointment.doctorBio != null ? appointment.doctorBio : 
                         (appointment.specialtyName != null ? appointment.specialtyName : "Tim mạch");
            holder.tvVaccineName.setText("Khám " + bio);
            holder.ivVaccineExtraIcon.setImageResource(R.drawable.outline_person_24);
            
            String docTitle = appointment.doctorTitle != null ? appointment.doctorTitle : "BS";
            String docName = appointment.doctorName != null ? appointment.doctorName : "Nguyễn Văn An";
            holder.tvVaccineNote.setText(docTitle + ". " + docName);
        }
        
        // Location
        String location = appointment.workplaceAddress != null ? appointment.workplaceAddress : "UniCare - Phòng 105";
        holder.tvVaccineLocation.setText(location);
        
        // Time
        holder.tvVaccineTime.setText(timeText);
        
        // Xử lý status badge và các nút điều khiển
        String status = appointment.status;
        if ("PENDING".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status) || "Sắp tới".equalsIgnoreCase(status)) {
            holder.tvVaccineBadge.setText("Sắp tới");
            holder.tvVaccineBadge.setBackgroundResource(R.drawable.bg_status_upcoming);
            holder.tvVaccineBadge.setTextColor(Color.parseColor("#D97706")); // Orange/Peach text
            holder.layoutButtonsContainer.setVisibility(View.VISIBLE);
        } else {
            holder.tvVaccineBadge.setText("Hoàn tất");
            holder.tvVaccineBadge.setBackgroundResource(R.drawable.bg_status_completed);
            holder.tvVaccineBadge.setTextColor(Color.parseColor("#10B981")); // Dark green text
            holder.layoutButtonsContainer.setVisibility(View.GONE);
        }
        
        // Gán sự kiện click cho các nút
        holder.btnVaccineReschedule.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onReschedule(appointment);
            }
        });
        
        holder.btnVaccineCancel.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onCancel(appointment);
            }
        });

        // Bấm vào item để xem chi tiết thông tin lượt khám
        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, com.haui.UniCare.feature.patients.appointment.ui.AppointmentDetailActivity.class);
            intent.putExtra("appointment_data", appointment);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointments != null ? appointments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVaccineMonth, tvVaccineDay, tvVaccineName, tvVaccineBadge;
        TextView tvVaccineTime, tvVaccineLocation, tvVaccineNote;
        ImageView ivVaccineExtraIcon;
        View btnVaccineReschedule, btnVaccineCancel, layoutButtonsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVaccineMonth = itemView.findViewById(R.id.tv_vaccine_month);
            tvVaccineDay = itemView.findViewById(R.id.tv_vaccine_day);
            tvVaccineName = itemView.findViewById(R.id.tv_vaccine_name);
            tvVaccineBadge = itemView.findViewById(R.id.tv_vaccine_badge);
            tvVaccineTime = itemView.findViewById(R.id.tv_vaccine_time);
            tvVaccineLocation = itemView.findViewById(R.id.tv_vaccine_location);
            tvVaccineNote = itemView.findViewById(R.id.tv_vaccine_note);
            ivVaccineExtraIcon = itemView.findViewById(R.id.iv_vaccine_extra_icon);
            btnVaccineReschedule = itemView.findViewById(R.id.btn_vaccine_reschedule);
            btnVaccineCancel = itemView.findViewById(R.id.btn_vaccine_cancel);
            layoutButtonsContainer = itemView.findViewById(R.id.layout_buttons_container);
        }
    }
}
