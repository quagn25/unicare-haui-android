package com.haui.UniCare.core.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.haui.UniCare.core.common_ui.LoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Kích hoạt Edge-to-Edge cho tất cả Activity kế thừa từ BaseActivity
        EdgeToEdge.enable(this);
        
        loadingDialog = new LoadingDialog(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applyEdgeToEdge();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        applyEdgeToEdge();
    }

    /**
     * Tự động áp dụng xử lý Insets để nội dung tràn lên Status Bar
     * nhưng vẫn giữ padding cho Navigation Bar ở dưới.
     */
    private void applyEdgeToEdge() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            // Lấy View con đầu tiên của content frame (là layout của Activity)
            View content = null;
            if (rootView instanceof android.view.ViewGroup && ((android.view.ViewGroup) rootView).getChildCount() > 0) {
                content = ((android.view.ViewGroup) rootView).getChildAt(0);
            }

            View targetView = content != null ? content : rootView;

            ViewCompat.setOnApplyWindowInsetsListener(targetView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                // Lưu ý: Tùy vào layout mà việc set padding ở đây có thể gây lệch.
                // Nếu muốn tràn hoàn toàn, ta không set padding top.
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    public void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}