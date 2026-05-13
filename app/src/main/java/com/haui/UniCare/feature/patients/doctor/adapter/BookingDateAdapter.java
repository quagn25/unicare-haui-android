package com.haui.UniCare.feature.patients.doctor.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.tvDayOfWeek.setText(date.getDayOfWeek());
        holder.tvDate.setText(date.getDate());
        holder.tvSlots.setText(date.getSlotCount());

        if (position == selectedPosition) {
            holder.tvDate.setBackgroundResource(R.drawable.bg_date_selected);
            holder.tvDate.setTextColor(Color.WHITE);
        } else {
            holder.tvDate.setBackgroundResource(0);
            holder.tvDate.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
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
        return dateList.size();
    }

    public BookingDate getSelectedDate() {
        if (selectedPosition != -1) return dateList.get(selectedPosition);
        return null;
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDate, tvSlots;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSlots = itemView.findViewById(R.id.tv_slots);
        }
    }
}
