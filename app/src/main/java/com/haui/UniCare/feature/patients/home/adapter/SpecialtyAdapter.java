package com.haui.UniCare.feature.patients.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.feature.patients.home.ui.specialty;

import java.util.ArrayList;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyAdapter.SpecialtyViewHolder> {

    private ArrayList<specialty> list;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(specialty specialty);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SpecialtyAdapter(ArrayList<specialty> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public com.google.android.material.card.MaterialCardView circleContainer;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.shapeableImageView);
            textView = itemView.findViewById(R.id.textView4);
            circleContainer = itemView.findViewById(R.id.circleContainer);
        }
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        specialty item = list.get(position);
        holder.imageView.setImageResource(item.getImage());
        holder.textView.setText(item.getName());

        // Set dynamic border color based on specialty (using premium bright clinical colors)
        int strokeColor;
        switch (item.getName() != null ? item.getName() : "") {
            case "Tổng quát":
                strokeColor = 0xFF00B686; // Clinical Teal
                break;
            case "Nha khoa":
                strokeColor = 0xFF0066FF; // Dental Blue
                break;
            case "Tim mạch":
                strokeColor = 0xFFEF4444; // Cardiology Red
                break;
            case "Da liễu":
                strokeColor = 0xFFD946EF; // Dermatology Pink
                break;
            case "Nhãn khoa":
                strokeColor = 0xFFD97706; // Ophthalmology Orange-gold
                break;
            case "Xét nghiệm":
                strokeColor = 0xFFEA580C; // Laboratory Red-orange
                break;
            default:
                strokeColor = 0xFF0066FF;
                break;
        }
        if (holder.circleContainer != null) {
            holder.circleContainer.setCardBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            holder.circleContainer.setStrokeColor(android.content.res.ColorStateList.valueOf(strokeColor));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
