package com.haui.UniCare.feature.patients.record.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.TreatmentPlan;

import java.util.List;

public class TreatmentPlanAdapter extends RecyclerView.Adapter<TreatmentPlanAdapter.ViewHolder> {

    private List<TreatmentPlan> planList;

    public TreatmentPlanAdapter(List<TreatmentPlan> planList) {
        this.planList = planList;
    }

    public void setData(List<TreatmentPlan> planList) {
        this.planList = planList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treatment_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TreatmentPlan plan = planList.get(position);

        holder.tvMedicineName.setText(plan.medicineName != null ? plan.medicineName : "Tên thuốc");
        
        String methodText = (plan.method != null ? plan.method : "Uống") + " - " + plan.timesPerDay + " lần / ngày";
        holder.tvMethod.setText(methodText);

        holder.tvPurpose.setText(plan.purpose != null && !plan.purpose.isEmpty() ? plan.purpose : "Không rõ mục đích");
        holder.tvGuide.setText(plan.guide != null && !plan.guide.isEmpty() ? plan.guide : "Uống theo chỉ định");
    }

    @Override
    public int getItemCount() {
        return planList != null ? planList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvMethod, tvPurpose, tvGuide;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tv_medicine_name);
            tvMethod = itemView.findViewById(R.id.tv_method);
            tvPurpose = itemView.findViewById(R.id.tv_purpose);
            tvGuide = itemView.findViewById(R.id.tv_guide);
        }
    }
}
