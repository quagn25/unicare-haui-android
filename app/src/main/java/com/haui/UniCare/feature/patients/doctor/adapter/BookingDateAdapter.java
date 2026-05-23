package com.haui.UniCare.feature.patients.doctor.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.BookingDate;
import java.util.List;

public class BookingDateAdapter extends RecyclerView.Adapter<BookingDateAdapter.DateViewHolder> {

    private List<BookingDate> dateList;
    private int selectedPosition = -1;
    private OnDateSelectedListener listener;

    public interface OnDateSelectedListener {
        void onDateSelected(BookingDate date);
    }

    public BookingDateAdapter(List<BookingDate> dateList, OnDateSelectedListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        BookingDate date = dateList.get(position);
        
        if (date.getDate().isEmpty()) {
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.tvDayOfWeek.setText(date.getDayOfWeek());
        holder.tvDate.setText(date.getDate());
        
        boolean hasSlots = date.getSlotCount() != null && !date.getSlotCount().isEmpty();
        
        if (hasSlots) {
            holder.tvSlots.setText(date.getSlotCount());
            holder.tvSlots.setVisibility(View.VISIBLE);
            holder.cardDate.setCardBackgroundColor(Color.WHITE);
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#9CA3AF"));
            holder.tvDate.setTextColor(Color.parseColor("#1F2937"));
            holder.cardDate.setStrokeColor(Color.parseColor("#F3F4F6"));
        } else {
            holder.tvSlots.setVisibility(View.GONE);
            holder.cardDate.setCardBackgroundColor(Color.parseColor("#F9FAFB"));
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#D1D5DB"));
            holder.tvDate.setTextColor(Color.parseColor("#D1D5DB"));
            holder.cardDate.setStrokeColor(Color.TRANSPARENT);
        }

        // Special color for Sunday
        if ("CN".equals(date.getDayOfWeek()) && !hasSlots) {
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#FCA5A5"));
            holder.tvDate.setTextColor(Color.parseColor("#FCA5A5"));
        } else if ("CN".equals(date.getDayOfWeek())) {
            holder.tvDayOfWeek.setTextColor(Color.parseColor("#EF4444"));
            holder.tvDate.setTextColor(Color.parseColor("#EF4444"));
        }

        // Selection UI
        if (position == selectedPosition) {
            holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
            holder.tvDate.setTextColor(Color.WHITE);
            holder.tvDate.setTypeface(null, Typeface.BOLD);
        } else {
            holder.tvDate.setBackgroundResource(0);
            holder.tvDate.setTypeface(null, Typeface.NORMAL);
            // Color is already set above based on hasSlots/Sunday
        }

        holder.itemView.setOnClickListener(v -> {
            if (!hasSlots) return;
            
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDateSelected(date);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList != null ? dateList.size() : 0;
    }

    public BookingDate getSelectedDate() {
        if (selectedPosition != -1 && selectedPosition < dateList.size()) {
            return dateList.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedDate(BookingDate date) {
        for (int i = 0; i < dateList.size(); i++) {
            if (dateList.get(i) == date) {
                int previousSelected = selectedPosition;
                selectedPosition = i;
                if (previousSelected != -1) notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardDate;
        TextView tvDayOfWeek, tvDate, tvSlots;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDate = (MaterialCardView) itemView.findViewById(R.id.cardDate);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayLabel);
            tvDate = itemView.findViewById(R.id.tvDayNum);
            tvSlots = itemView.findViewById(R.id.tvSlots);
        }
    }
}
