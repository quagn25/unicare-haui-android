package com.haui.UniCare.feature.patients.appointment.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.datetime.DateSlot;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<DateSlot> dateList;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(DateSlot dateSlot);
    }

    public DateAdapter(List<DateSlot> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_slot, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateSlot dateSlot = dateList.get(position);
        holder.tvDayOfWeek.setText(dateSlot.getDayOfWeek());
        holder.tvDate.setText(dateSlot.getDate());
        holder.tvSlotCount.setText(dateSlot.getAvailableSlots() + " slot");

        // Logic đổi màu khi được chọn (Giống hình tròn xanh ở Ảnh 1)
        if (dateSlot.isSelected()) {
            holder.bgCircle.setBackgroundResource(R.drawable.bg_circle_selected_blue);
            holder.tvDate.setTextColor(Color.WHITE);
        } else {
            holder.bgCircle.setBackgroundResource(R.drawable.bg_circle_unselected_grey);
            holder.tvDate.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            // Reset tất cả về false
            for (DateSlot d : dateList) d.setSelected(false);
            // Set item hiện tại thành true
            dateSlot.setSelected(true);
            notifyDataSetChanged(); // Cập nhật lại UI
            listener.onDateClick(dateSlot); // Trả dữ liệu về Activity/ViewModel
        });
    }

    @Override
    public int getItemCount() { return dateList != null ? dateList.size() : 0; }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDate, tvSlotCount;
        View bgCircle;
        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSlotCount = itemView.findViewById(R.id.tv_slot_count);
            bgCircle = itemView.findViewById(R.id.bg_circle_date);
        }
    }
}