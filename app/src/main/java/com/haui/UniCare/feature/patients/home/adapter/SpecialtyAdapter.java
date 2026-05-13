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

    public SpecialtyAdapter(ArrayList<specialty> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bạn hãy kiểm tra lại ID trong layout activity_specialty.xml
            imageView = itemView.findViewById(R.id.shapeableImageView);
            textView = itemView.findViewById(R.id.textView4);
        }
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Lưu ý: Thông thường item nên có layout riêng (ví dụ: R.layout.item_specialty)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        specialty item = list.get(position);
        holder.imageView.setImageResource(item.getImage());
        holder.textView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
