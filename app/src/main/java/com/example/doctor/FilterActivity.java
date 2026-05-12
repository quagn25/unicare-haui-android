package com.example.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class FilterActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION  = "filter_location";
    public static final String EXTRA_SPECIALTY = "filter_specialty";
    public static final String EXTRA_PLACE     = "filter_place";

    private RadioGroup rgLocation, rgPlace;
    private ChipGroup  cgSpecialty;
    private String selectedSpecialty = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());

        rgLocation  = findViewById(R.id.rgLocation);
        rgPlace     = findViewById(R.id.rgPlace);
        cgSpecialty = findViewById(R.id.cgSpecialty);

        // Thêm chip chuyên khoa
        String[] specialties = {"Tất cả","Nam khoa","Nhi khoa","Tim mạch","Da liễu",
                "Nội tiết","Tai Mũi Họng","Mắt","Sản phụ khoa","Thần kinh",
                "Ung bướu","Hô hấp","Chấn thương chỉnh hình","Nội thận","Tâm thần"};
        for (String s : specialties) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setCheckable(true);
            chip.setChecked(s.equals("Tất cả"));
            chip.setChipBackgroundColorResource(R.color.chip_background);
            chip.setTextColor(getColor(R.color.chip_text));
            chip.setOnCheckedChangeListener((v, checked) -> {
                if (checked) selectedSpecialty = s;
            });
            cgSpecialty.addView(chip);
        }

        // Nút Xoá bộ lọc
        ((MaterialButton) findViewById(R.id.btnClear)).setOnClickListener(v -> {
            rgLocation.check(R.id.rbAll);
            rgPlace.check(R.id.rbDoctor);
            selectedSpecialty = "Tất cả";
            for (int i = 0; i < cgSpecialty.getChildCount(); i++) {
                Chip c = (Chip) cgSpecialty.getChildAt(i);
                c.setChecked(c.getText().equals("Tất cả"));
            }
        });

        // Nút Áp dụng
        ((MaterialButton) findViewById(R.id.btnApply)).setOnClickListener(v -> {
            RadioButton rbLoc   = findViewById(rgLocation.getCheckedRadioButtonId());
            RadioButton rbPlace = findViewById(rgPlace.getCheckedRadioButtonId());
            Intent result = new Intent();
            result.putExtra(EXTRA_LOCATION,  rbLoc   != null ? rbLoc.getText().toString()   : "Tất cả");
            result.putExtra(EXTRA_SPECIALTY, selectedSpecialty);
            result.putExtra(EXTRA_PLACE,     rbPlace != null ? rbPlace.getText().toString() : "Bác sĩ");
            setResult(RESULT_OK, result);
            finish();
        });
    }
}