package com.example.doctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorListAdapter adapter;
    private List<Doctor> allDoctors;
    private EditText etSearch;
    private TextView tvFilterInfo;

    private String filterSpecialty = "Tất cả";

    private final ActivityResultLauncher<Intent> filterLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {
                            String sp = result.getData()
                                    .getStringExtra(FilterActivity.EXTRA_SPECIALTY);
                            filterSpecialty = (sp != null) ? sp : "Tất cả";
                            applyFilter(etSearch.getText().toString());

                            tvFilterInfo.setText(
                                    filterSpecialty.equals("Tất cả")
                                            ? "Tất cả bác sĩ"
                                            : filterSpecialty);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        ((ImageButton) findViewById(R.id.btnBack))
                .setOnClickListener(v -> finish());

        tvFilterInfo = findViewById(R.id.tvFilterInfo);
        recyclerView = findViewById(R.id.recyclerViewDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy TẤT CẢ 25 bác sĩ khi vào màn hình này
        allDoctors = DoctorRepository.getInstance().getAllDoctors();
        adapter    = new DoctorListAdapter(this, new ArrayList<>(allDoctors));
        recyclerView.setAdapter(adapter);

        // Tìm kiếm
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {
                applyFilter(s.toString());
            }
        });

        // Bộ lọc
        findViewById(R.id.btnFilter).setOnClickListener(v ->
                filterLauncher.launch(new Intent(this, FilterActivity.class)));
    }

    private void applyFilter(String query) {
        List<Doctor> filtered = new ArrayList<>();
        String q = query.toLowerCase().trim();

        for (Doctor d : allDoctors) {
            boolean matchSearch = q.isEmpty()
                    || d.getName().toLowerCase().contains(q)
                    || d.getRole().toLowerCase().contains(q);
            for (String sp : d.getSpecialties()) {
                if (sp.toLowerCase().contains(q)) { matchSearch = true; break; }
            }

            boolean matchSpecialty = filterSpecialty.equals("Tất cả");
            if (!matchSpecialty) {
                for (String sp : d.getSpecialties()) {
                    if (sp.equalsIgnoreCase(filterSpecialty)) {
                        matchSpecialty = true; break;
                    }
                }
            }

            if (matchSearch && matchSpecialty) filtered.add(d);
        }

        adapter = new DoctorListAdapter(this, filtered);
        recyclerView.setAdapter(adapter);
    }
}