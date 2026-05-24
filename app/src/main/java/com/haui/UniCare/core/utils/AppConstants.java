package com.haui.UniCare.core.utils;

/**
 * Lớp chứa các hằng số dùng chung cho toàn bộ ứng dụng.
 */
public class AppConstants {
    
    /**
     * Cờ để bật/tắt chế độ sử dụng dữ liệu mẫu (Mock Data).
     * set thành true để hiển thị bác sĩ A, B, C, D (dành cho dev/test giao diện).
     * set thành false để lấy dữ liệu thực từ Database thông qua API.
     */
    public static final boolean USE_MOCK_DATA = false; 

    // Các hằng số khác có thể thêm ở đây
    public static final String PREFS_NAME = "UniCarePrefs";
}
