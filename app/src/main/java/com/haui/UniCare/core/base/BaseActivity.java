package com.haui.UniCare.core.base;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// Nếu bạn đã tạo file LoadingDialog trong common_ui
import com.haui.UniCare.core.common_ui.LoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Loading Dialog dùng chung
        // (Bạn cần có 1 class LoadingDialog kế thừa từ Dialog hoặc AlertDialog)
        loadingDialog = new LoadingDialog(this);
    }

    /**
     * Hiển thị vòng xoay loading (thường gọi khi bắt đầu call API)
     */
    public void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * Tắt vòng xoay loading (gọi khi call API xong, dù thành công hay thất bại)
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Hiển thị thông báo lỗi chung
     */
    public void showError(String message) {
        // Tạm thời dùng Toast, bạn có thể thay bằng ErrorDialog của bạn
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Tránh lỗi window leak khi Activity bị hủy mà Dialog vẫn đang hiện
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}