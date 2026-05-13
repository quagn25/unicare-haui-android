package com.haui.UniCare.feature.patients.doctor.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.TimeSlot;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeViewHolder> {

    private List<TimeSlot> timeSlots;
    private int selectedPosition = -1;
    private OnTimeSelectedListener listener;

    public interface OnTimeSelectedListener {
        void onTimeSelected(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(List<TimeSlot> timeSlots, OnTimeSelectedListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    public void updateData(List<TimeSlot> newList) {
        this.timeSlots = newList;
        this.selectedPosition = -1; // Reset selection when switching tabs
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.tvTimeRange.setText(timeSlot.getTimeRange());

        if (position == selectedPosition) {
            holder.tvTimeRange.setBackgroundResource(R.drawable.bg_time_selected);
            holder.tvTimeRange.setTextColor(Color.WHITE);
        } else {
            holder.tvTimeRange.setBackgroundResource(R.drawable.bg_chip);
            holder.tvTimeRange.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onTimeSelected(timeSlot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots != null ? timeSlots.size() : 0;
    }

    public TimeSlot getSelectedTime() {
        if (selectedPosition != -1) return timeSlots.get(selectedPosition);
        return null;
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeRange;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeRange = itemView.findViewById(R.id.tv_time_range);
        }
    }
}
