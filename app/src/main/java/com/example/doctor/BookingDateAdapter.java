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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingDateAdapter extends RecyclerView.Adapter<BookingDateAdapter.ViewHolder> {

    private Context context;
    private List<DateItem> dates;
    private int selectedPosition = -1;
    private Calendar currentMonth;
    private Runnable onDateChanged; // callback khi đổi ngày

    public BookingDateAdapter(Context context, List<DateItem> dates, Runnable onDateChanged) {
        this.context       = context;
        this.dates         = dates;
        this.currentMonth  = Calendar.getInstance();
        this.onDateChanged = onDateChanged;
    }

    public void updateData(List<DateItem> newDates, Calendar month) {
        this.dates        = newDates;
        this.selectedPosition = -1;
        this.currentMonth = (Calendar) month.clone();
        notifyDataSetChanged();
    }

    public Calendar getCurrentMonth() {
        return (Calendar) currentMonth.clone();
    }

    public String getCurrentMonthFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy", new Locale("vi"));
        return sdf.format(currentMonth.getTime());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_booking_date, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateItem item = dates.get(position);

        if (item.getDayNum() == 0) {
            holder.cardDate.setVisibility(View.INVISIBLE);
            return;
        }
        holder.cardDate.setVisibility(View.VISIBLE);
        holder.tvDayLabel.setText(item.getDayLabel());
        holder.tvDayNum.setText(String.valueOf(item.getDayNum()));

        if (item.getSlots() > 0) {
            holder.tvSlots.setVisibility(View.VISIBLE);
            holder.tvSlots.setText(item.getSlots() + " slot");
        } else {
            holder.tvSlots.setVisibility(View.GONE);
        }

        boolean isSelected = (position == selectedPosition);
        boolean isDisabled = item.isDisabled();

        if (isSelected) {
            holder.cardDate.setCardBackgroundColor(context.getColor(R.color.primary_blue));
            holder.tvDayLabel.setTextColor(Color.WHITE);
            holder.tvDayNum.setTextColor(Color.WHITE);
        } else if (isDisabled) {
            holder.cardDate.setCardBackgroundColor(Color.parseColor("#F3F4F6"));
            holder.tvDayLabel.setTextColor(Color.parseColor("#D1D5DB"));
            holder.tvDayNum.setTextColor(Color.parseColor("#D1D5DB"));
        } else {
            holder.cardDate.setCardBackgroundColor(Color.WHITE);
            holder.tvDayLabel.setTextColor(context.getColor(R.color.text_secondary));
            holder.tvDayNum.setTextColor(context.getColor(R.color.text_primary));
        }

        if (!isDisabled) {
            holder.itemView.setOnClickListener(v -> {
                int prev = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                if (prev != -1) notifyItemChanged(prev);
                notifyItemChanged(selectedPosition);
                if (onDateChanged != null) onDateChanged.run();
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() { return dates.size(); }

    public DateItem getSelectedDate() {
        if (selectedPosition >= 0 && selectedPosition < dates.size())
            return dates.get(selectedPosition);
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardDate;
        TextView tvDayLabel, tvDayNum, tvSlots;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDate   = itemView.findViewById(R.id.cardDate);
            tvDayLabel = itemView.findViewById(R.id.tvDayLabel);
            tvDayNum   = itemView.findViewById(R.id.tvDayNum);
            tvSlots    = itemView.findViewById(R.id.tvSlots);
        }
    }

    public static class DateItem {
        private String dayLabel;
        private int dayNum;
        private int slots;
        private boolean disabled;

        public DateItem(String dayLabel, int dayNum, int slots) {
            this(dayLabel, dayNum, slots, false);
        }
        public DateItem(String dayLabel, int dayNum, int slots, boolean disabled) {
            this.dayLabel = dayLabel; this.dayNum = dayNum;
            this.slots = slots;      this.disabled = disabled;
        }
        public String getDayLabel()  { return dayLabel; }
        public int getDayNum()       { return dayNum; }
        public int getSlots()        { return slots; }
        public boolean isDisabled()  { return disabled; }
    }
}