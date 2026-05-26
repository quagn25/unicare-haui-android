package com.haui.UniCare.feature.patients.home.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.UniCare.R;
import com.haui.UniCare.data.model.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> list;
    private Context context;
    private OnNotificationDeleteListener deleteListener;
    private OnNotificationClickListener clickListener;

    public interface OnNotificationDeleteListener {
        void onDeleteClick(Notification notification);
    }

    public interface OnNotificationClickListener {
        void onItemClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setDeleteListener(OnNotificationDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setClickListener(OnNotificationClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateData(List<Notification> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification item = list.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
        holder.tvTime.setText(getRelativeTime(item.getCreatedAt()));

        // Trạng thái đã đọc/chưa đọc
        if (item.isRead()) {
            holder.cardNotification.setCardBackgroundColor(ColorStateList.valueOf(0xFFFFFFFF)); // Trắng tinh
            holder.viewUnreadDot.setVisibility(View.GONE);
        } else {
            holder.cardNotification.setCardBackgroundColor(ColorStateList.valueOf(0xFFEDF5FF)); // Xanh dương nhạt cho chưa đọc
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
        }

        // Thiết lập icon và màu sắc pastel tương ứng với Type của thông báo
        int containerColor;
        int tintColor;
        int iconRes;

        switch (item.getType() != null ? item.getType() : "") {
            case "LICH_KHAM":
                containerColor = 0x1A0B5CFF; // 10% Blue
                tintColor = 0xFF0B5CFF;
                iconRes = R.drawable.outline_date_range_24;
                break;
            case "TIEM_CHUNG":
                containerColor = 0x1A00B686; // 10% Teal
                tintColor = 0xFF00B686;
                iconRes = R.drawable.ic_action_vaccine;
                break;
            case "KET_QUA":
                containerColor = 0x1AEA580C; // 10% Orange-red
                tintColor = 0xFFEA580C;
                iconRes = R.drawable.ic_action_file;
                break;
            case "UU_DAI":
                containerColor = 0x1AD946EF; // 10% Magenta
                tintColor = 0xFFD946EF;
                iconRes = R.drawable.ic_verified; // Thay thế cho icon tag ưu đãi
                break;
            case "CAP_NHAT":
                containerColor = 0x1A64748B; // 10% Slate/Gray
                tintColor = 0xFF64748B;
                iconRes = R.drawable.ic_verified; // Thay thế cho icon update/system
                break;
            default:
                containerColor = 0x1A0B5CFF;
                tintColor = 0xFF0B5CFF;
                iconRes = R.drawable.ic_verified;
                break;
        }

        if (holder.iconContainer != null) {
            holder.iconContainer.setBackgroundTintList(ColorStateList.valueOf(containerColor));
        }
        holder.imgIcon.setImageResource(iconRes);
        holder.imgIcon.setImageTintList(ColorStateList.valueOf(tintColor));
        
        if (holder.btnDeleteNotification != null) {
            holder.btnDeleteNotification.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(item);
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    private String getRelativeTime(String createdAtStr) {
        if (createdAtStr == null) return "";
        try {
            // NodeJS JSON serialization trả về dạng ISO UTC
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date createdAt;
            try {
                createdAt = sdf.parse(createdAtStr);
            } catch (Exception e) {
                // Thử format MySQL trực tiếp: yyyy-MM-dd HH:mm:ss
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                createdAt = sdf2.parse(createdAtStr);
            }

            if (createdAt == null) return createdAtStr;

            long diff = new Date().getTime() - createdAt.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) {
                return "VỪA XONG";
            } else if (minutes < 60) {
                return minutes + " PHÚT TRƯỚC";
            } else if (hours < 24) {
                return hours + " GIỜ TRƯỚC";
            } else if (days == 1) {
                return "HÔM QUA";
            } else {
                return days + " NGÀY TRƯỚC";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createdAtStr;
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public com.google.android.material.card.MaterialCardView cardNotification;
        public View iconContainer;
        public ImageView imgIcon;
        public ImageView btnDeleteNotification;
        public TextView tvTitle;
        public View viewUnreadDot;
        public TextView tvContent;
        public TextView tvTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNotification = itemView.findViewById(R.id.cardNotification);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            imgIcon = itemView.findViewById(R.id.imgNotificationIcon);
            btnDeleteNotification = itemView.findViewById(R.id.btnDeleteNotification);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
            tvContent = itemView.findViewById(R.id.tvNotificationContent);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
        }
    }
}
