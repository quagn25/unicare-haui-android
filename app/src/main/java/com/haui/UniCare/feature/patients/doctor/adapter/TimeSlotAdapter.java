package com.haui.UniCare.feature.patients.doctor.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.TimeSlot;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

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

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);
        holder.tvSlot.setText(timeSlot.getTimeRange());

        if (position == selectedPosition) {
            holder.cardSlot.setCardBackgroundColor(Color.parseColor("#009688"));
            holder.tvSlot.setTextColor(Color.WHITE);
        } else {
            holder.cardSlot.setCardBackgroundColor(Color.WHITE);
            holder.tvSlot.setTextColor(Color.BLACK);
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
        if (selectedPosition != -1 && selectedPosition < timeSlots.size()) {
            return timeSlots.get(selectedPosition);
        }
        return null;
    }

    public void clearSelection() {
        int previousSelected = selectedPosition;
        selectedPosition = -1;
        if (previousSelected != -1) {
            notifyItemChanged(previousSelected);
        }
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        CardView cardSlot;
        TextView tvSlot;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSlot = itemView.findViewById(R.id.cardSlot);
            tvSlot = itemView.findViewById(R.id.tvSlot);
        }
    }
}
