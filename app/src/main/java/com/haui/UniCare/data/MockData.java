package com.haui.UniCare.data;

import com.haui.UniCare.R;
import com.haui.UniCare.data.model.table.Doctor;

import java.util.ArrayList;
import java.util.List;

public class MockData {
    public static List<Doctor> getMockDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        
        Doctor d1 = new Doctor(1, "PGS.TS Nguyễn Văn An", "Tiến sĩ - Bác sĩ", 25, "Bệnh viện Bạch Mai, Hà Nội", R.drawable.doctorbook, "Tim mạch");
        d1.setBio("Chuyên gia hàng đầu về tim mạch can thiệp với hơn 25 năm kinh nghiệm. Từng tu nghiệp tại Pháp và Hoa Kỳ.");
        d1.setConsultationFee(500000);
        doctors.add(d1);

        Doctor d2 = new Doctor(2, "ThS.BS Trần Thu Hà", "Thạc sĩ - Bác sĩ", 12, "Bệnh viện Nhi Trung ương", R.drawable.doctorbook, "Nhi khoa");
        d2.setBio("Bác sĩ Hà chuyên điều trị các bệnh lý hô hấp và tiêu hóa ở trẻ em. Rất tâm lý và được các bé yêu quý.");
        d2.setConsultationFee(300000);
        doctors.add(d2);

        Doctor d3 = new Doctor(3, "BSCKII Lê Hoàng Nam", "Bác sĩ Chuyên khoa II", 18, "Bệnh viện Da liễu Trung ương", R.drawable.doctorbook, "Da liễu");
        d3.setBio("Chuyên điều trị các bệnh về da liễu thẩm mỹ, mụn trứng cá và các bệnh da tự miễn.");
        d3.setConsultationFee(400000);
        doctors.add(d3);

        Doctor d4 = new Doctor(4, "TS.BS Phạm Minh Đức", "Tiến sĩ - Bác sĩ", 20, "Bệnh viện Mắt Trung ương", R.drawable.doctorbook, "Nhãn khoa");
        d4.setBio("Chuyên gia phẫu thuật Phaco và điều trị các tật khúc xạ mắt. Có nhiều công trình nghiên cứu khoa học cấp quốc gia.");
        d4.setConsultationFee(450000);
        doctors.add(d4);

        Doctor d5 = new Doctor(5, "BS Hoàng Thị Mai", "Bác sĩ", 8, "Phòng khám Đa khoa UniCare", R.drawable.doctorbook, "Nha khoa");
        d5.setBio("Bác sĩ Mai chuyên về nha khoa thẩm mỹ và phục hình răng sứ. Luôn tận tâm với khách hàng.");
        d5.setConsultationFee(200000);
        doctors.add(d5);

        return doctors;
    }
}
