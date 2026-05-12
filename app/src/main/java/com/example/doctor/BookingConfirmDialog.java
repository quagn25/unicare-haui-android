package com.example.doctor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class BookingConfirmDialog extends Dialog {

    private Doctor doctor;
    private String dayLabel, monthYear, timeSlot;
    private int dayNum;

    public BookingConfirmDialog(Context context, Doctor doctor,
                                String dayLabel, int dayNum,
                                String monthYear, String timeSlot) {
        super(context);
        this.doctor    = doctor;
        this.dayLabel  = dayLabel;
        this.dayNum    = dayNum;
        this.monthYear = monthYear;
        this.timeSlot  = timeSlot;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_booking_confirm);

        if (getWindow() != null) {
            getWindow().setLayout(
                    (int)(getContext().getResources().getDisplayMetrics().widthPixels * 0.9f),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ((TextView) findViewById(R.id.tvDoctorName))
                .setText(doctor.getTitle() + " " + doctor.getName());
        ((TextView) findViewById(R.id.tvDate))
                .setText(dayLabel + ", ngày " + dayNum + " tháng " + monthYear);
        ((TextView) findViewById(R.id.tvTimeSlot))
                .setText(timeSlot);
        ((TextView) findViewById(R.id.tvAddress))
                .setText(doctor.getAddress());
        ((TextView) findViewById(R.id.tvFee))
                .setText("Phí tư vấn: " + doctor.getConsultFee() + "/lượt");

        ((MaterialButton) findViewById(R.id.btnConfirm)).setOnClickListener(v -> {
            // Lưu lịch khám vào AppointmentManager
            Appointment appt = new Appointment(
                    doctor.getId(),
                    doctor.getTitle() + " " + doctor.getName(),
                    doctor.getSpecialties().isEmpty() ? "" : doctor.getSpecialties().get(0),
                    dayLabel + ", ngày " + dayNum + " tháng " + monthYear,
                    timeSlot,
                    doctor.getAddress(),
                    doctor.getConsultFee()
            );
            AppointmentManager.getInstance().addAppointment(appt);

            Toast.makeText(getContext(), "Đặt lịch thành công!", Toast.LENGTH_LONG).show();
            dismiss();
        });

        ((MaterialButton) findViewById(R.id.btnCancel)).setOnClickListener(v -> dismiss());
    }
}