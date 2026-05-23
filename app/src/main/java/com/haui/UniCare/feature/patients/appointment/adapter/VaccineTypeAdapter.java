package com.haui.UniCare.feature.patients.appointment.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.haui.UniCare.R;
import com.haui.UniCare.data.model.VaccineType;
import java.util.List;
import java.util.Locale;

public class VaccineTypeAdapter extends RecyclerView.Adapter<VaccineTypeAdapter.ViewHolder> {

    private List<VaccineType> vaccineList;
    private int selectedPosition = -1;
    private OnVaccineSelectedListener listener;

    public interface OnVaccineSelectedListener {
        void onVaccineSelected(VaccineType vaccine);
    }

    public VaccineTypeAdapter(List<VaccineType> vaccineList, OnVaccineSelectedListener listener) {
        this.vaccineList = vaccineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccine_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaccineType vaccine = vaccineList.get(position);

        holder.tvVaccineName.setText(vaccine.getName().replace("Vắc-xin ", ""));
        holder.tvDoseInfo.setText(vaccine.getDoseInfo());

        if (vaccine.getPrice() == 0) {
            holder.tvVaccinePrice.setText("Miễn phí");
            holder.tvVaccinePrice.setTextColor(Color.parseColor("#10B981")); // Green for free
        } else {
            holder.tvVaccinePrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", vaccine.getPrice()));
            holder.tvVaccinePrice.setTextColor(Color.parseColor("#0B5CFF")); // Standard theme blue
        }

        // Selection State Styling
        if (position == selectedPosition) {
            holder.cardVaccineType.setStrokeColor(Color.parseColor("#0B5CFF"));
            holder.cardVaccineType.setStrokeWidth(4); // 2dp
            holder.ivChecked.setVisibility(View.VISIBLE);
        } else {
            holder.cardVaccineType.setStrokeColor(Color.parseColor("#E5E7EB"));
            holder.cardVaccineType.setStrokeWidth(2); // 1dp
            holder.ivChecked.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onVaccineSelected(vaccine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vaccineList != null ? vaccineList.size() : 0;
    }

    public VaccineType getSelectedVaccine() {
        if (selectedPosition != -1 && selectedPosition < vaccineList.size()) {
            return vaccineList.get(selectedPosition);
        }
        return null;
    }

    public void setSelectedPosition(int position) {
        int prevSelected = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(prevSelected);
        notifyItemChanged(selectedPosition);
        if (listener != null && position != -1 && position < vaccineList.size()) {
            listener.onVaccineSelected(vaccineList.get(position));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardVaccineType;
        TextView tvVaccineName, tvDoseInfo, tvVaccinePrice;
        ImageView ivVaccineIcon, ivChecked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardVaccineType = itemView.findViewById(R.id.cardVaccineType);
            tvVaccineName = itemView.findViewById(R.id.tvVaccineName);
            tvDoseInfo = itemView.findViewById(R.id.tvDoseInfo);
            tvVaccinePrice = itemView.findViewById(R.id.tvVaccinePrice);
            ivVaccineIcon = itemView.findViewById(R.id.ivVaccineIcon);
            ivChecked = itemView.findViewById(R.id.ivChecked);
        }
    }
}
