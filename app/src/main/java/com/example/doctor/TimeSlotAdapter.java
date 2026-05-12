package com.example.doctor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private Context context;
    private List<String> slots;
    private int selectedPosition = -1;
    private OnSlotSelectedListener listener;

    public interface OnSlotSelectedListener {
        void onSlotSelected(String slot); // null = bỏ chọn
    }

    public TimeSlotAdapter(Context context, List<String> slots, OnSlotSelectedListener listener) {
        this.context  = context;
        this.slots    = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_time_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvSlot.setText(slots.get(position));

        boolean selected = (position == selectedPosition);
        if (selected) {
            holder.card.setCardBackgroundColor(context.getColor(R.color.primary_blue));
            holder.tvSlot.setTextColor(Color.WHITE);
        } else {
            holder.card.setCardBackgroundColor(Color.WHITE);
            holder.tvSlot.setTextColor(context.getColor(R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            int clicked = holder.getAdapterPosition();
            if (clicked == selectedPosition) {
                // Bấm lại → BỎ CHỌN
                selectedPosition = -1;
                notifyItemChanged(clicked);
                if (listener != null) listener.onSlotSelected(null);
            } else {
                int prev = selectedPosition;
                selectedPosition = clicked;
                if (prev != -1) notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                if (listener != null) listener.onSlotSelected(slots.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() { return slots.size(); }

    public String getSelectedSlot() {
        if (selectedPosition >= 0 && selectedPosition < slots.size())
            return slots.get(selectedPosition);
        return null;
    }

    // Bỏ chọn tất cả (khi chuyển ngày)
    public void clearSelection() {
        int prev = selectedPosition;
        selectedPosition = -1;
        if (prev != -1) notifyItemChanged(prev);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvSlot;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card   = itemView.findViewById(R.id.cardSlot);
            tvSlot = itemView.findViewById(R.id.tvSlot);
        }
    }
}