package com.haui.UniCare.data.model.table;

import com.google.gson.annotations.SerializedName;

public class TreatmentPlan implements java.io.Serializable {
    public int id;

    @SerializedName("record_id")
    public int recordId;

    @SerializedName("medicine_name")
    public String medicineName;

    public String method;

    @SerializedName("times_per_day")
    public int timesPerDay;

    public String purpose;
    public String guide;

    public TreatmentPlan() {}

    public TreatmentPlan(int id, int recordId, String medicineName, String method, int timesPerDay, String purpose, String guide) {
        this.id = id;
        this.recordId = recordId;
        this.medicineName = medicineName;
        this.method = method;
        this.timesPerDay = timesPerDay;
        this.purpose = purpose;
        this.guide = guide;
    }
}
