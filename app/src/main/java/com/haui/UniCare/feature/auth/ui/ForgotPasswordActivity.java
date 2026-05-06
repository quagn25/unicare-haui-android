package com.haui.UniCare.feature.auth.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.haui.UniCare.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView tvLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mapping();
        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }
    private void mapping(){
        tvLogin = findViewById(R.id.textView7);
    }
}
