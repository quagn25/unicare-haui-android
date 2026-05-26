package com.haui.UniCare.data;

import com.haui.UniCare.R;
import com.haui.UniCare.data.model.Notification;
import com.haui.UniCare.data.model.table.Appointment;
import com.haui.UniCare.data.model.table.Doctor;

import java.util.ArrayList;
import java.util.List;

public class MockData {
    private static List<Appointment> mockAppointments = new ArrayList<>();
    private static List<Notification> mockNotifications = new ArrayList<>();

    static {
        // Khởi tạo một số dữ liệu mẫu ban đầu
        // Khám Tim mạch (Sắp tới)
        Appointment a1 = new Appointment(1, 1, 1, "2026-06-01 09:00:00", "PENDING", "2026-05-15");
        a1.doctorName = "Nguyễn Văn An";
        a1.doctorTitle = "ThS. BS";
        a1.doctorBio = "Tim mạch";
        a1.workplaceAddress = "UniCare - Phòng 105";
        
        // Khám Nha khoa (Sắp tới)
        Appointment a2 = new Appointment(2, 1, 2, "2026-06-02 14:30:00", "PENDING", "2026-05-15");
        a2.doctorName = "Trần Thị Bình";
        a2.doctorTitle = "BS";
        a2.doctorBio = "Nha khoa";
        a2.workplaceAddress = "UniCare - Phòng 203";
        
        // Vắc-xin Cúm mùa
        Appointment v1 = new Appointment(4, 1, 21, "2026-05-22 08:30:00", "PENDING", "2026-05-15");
        v1.doctorName = "Vắc-xin Cúm mùa";
        v1.doctorTitle = "Liều nhắc lại";
        v1.workplaceAddress = "Phòng 201 - UniCare";

        mockAppointments.add(a1);
        mockAppointments.add(a2);
        mockAppointments.add(v1);

        // Khởi tạo thông báo mẫu
        Notification n1 = new Notification();
        n1.setId(1);
        n1.setTitle("Tạo tài khoản thành công");
        n1.setContent("Chào mừng bạn đến với UniCare. Tài khoản của bạn đã được tạo thành công!");
        n1.setType("ALL");
        n1.setIsRead(0);
        n1.setCreatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        mockNotifications.add(n1);
    }

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

        return doctors;
    }

    public static List<Appointment> getMockAppointments() {
        return mockAppointments;
    }

    public static void addMockAppointment(Appointment appointment) {
        mockAppointments.add(0, appointment);
    }

    public static void removeMockAppointment(int id) {
        mockAppointments.removeIf(a -> a.id == id);
    }

    public static List<Notification> getMockNotifications() {
        return mockNotifications;
    }

    public static void addMockNotification(Notification notification) {
        notification.setId(mockNotifications.size() + 1);
        notification.setCreatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
        mockNotifications.add(0, notification);
    }

    public static void removeMockNotification(int id) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mockNotifications.removeIf(n -> n.getId() == id);
        } else {
            for (int i = 0; i < mockNotifications.size(); i++) {
                if (mockNotifications.get(i).getId() == id) {
                    mockNotifications.remove(i);
                    break;
                }
            }
        }
    }
}
