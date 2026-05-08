package com.haui.UniCare.core.network;

import android.os.Build;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private static String getBaseUrl() {
        // CÁCH 1: DÙNG NGROK CHO TẤT CẢ (Khuyên dùng để gửi cho bạn bè/thầy giáo)
        // Dù là máy ảo hay máy thật, ở bất cứ đâu cũng sẽ kết nối được về máy tính của bạn.
        return "https://jailbird-twenty-recovery.ngrok-free.dev/";

        /* 
        // CÁCH 2: DÙNG LOCAL CHO MÁY ẢO CỦA BẠN (Chỉ dùng khi bạn tự dev một mình)
        if (isEmulator()) {
            return "http://10.0.2.2:3000/";
        }
        return "https://jailbird-twenty-recovery.ngrok-free.dev/";
        */
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.contains("vbox")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.BOARD.contains("QC_Reference_Phone")
                || Build.MANUFACTURER.contains("Genymotion");
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
