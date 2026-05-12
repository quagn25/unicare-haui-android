package com.example.doctor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorRepository {

    private static DoctorRepository instance;
    private List<Doctor> doctors;

    private DoctorRepository() { initData(); }

    public static DoctorRepository getInstance() {
        if (instance == null) instance = new DoctorRepository();
        return instance;
    }

    private void initData() {
        doctors = new ArrayList<>();

        // ===== 10 BÁC SĨ CŨ =====
        doctors.add(new Doctor(1, "Lê Anh Tuấn", "ThS. BS",
                "31 năm kinh nghiệm",
                "Bác sĩ Niệu Khoa - Nam Khoa - Phẫu thuật viên vi phẫu",
                Arrays.asList("Nam khoa", "Ngoại niệu"),
                "23 Nguyễn Văn Đậu, Phường 5, Quận Phú Nhuận, TP.HCM",
                R.drawable.ic_doctor_placeholder,
                "Phòng khám BacsiXmen: T2-T5 khám 17:30-19:00, T7 sáng 9:00-10:30.\nPhí 500.000đ/lượt.",
                "0963362998", 4.8f, 120, "500.000đ",
                "ThS.BS Lê Anh Tuấn tốt nghiệp ĐH Y Dược TP.HCM, 31 năm kinh nghiệm Niệu khoa và Nam khoa. Từng công tác BV Bình Dân. Chuyên điều trị bệnh lý đường tiết niệu, phẫu thuật nội soi và vi phẫu nam khoa."));

        doctors.add(new Doctor(2, "Đào Bùi Quý Quyền", "TS. BS",
                "23 năm kinh nghiệm",
                "Bác sĩ Nội thận - Ngoại tiết niệu",
                Arrays.asList("Nội thận", "Ngoại tiết niệu"),
                "242 Nguyễn Chí Thanh, Phường 2, Quận 10, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0901234567", 4.7f, 95, "300.000đ",
                "TS.BS Đào Bùi Quý Quyền 23 năm kinh nghiệm Nội thận và Ngoại tiết niệu. Chuyên điều trị sỏi thận, suy thận mạn và bệnh lý đường tiết niệu phức tạp."));

        doctors.add(new Doctor(3, "Nguyễn Thị Bích Đào", "PGS. TS. BS",
                "36 năm kinh nghiệm",
                "Bác sĩ Nội tiết",
                Arrays.asList("Nội tiết"),
                "215F Nguyễn Trãi, Cầu Ông Lãnh, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0912345678", 4.9f, 210, "400.000đ",
                "PGS.TS.BS Nguyễn Thị Bích Đào chuyên gia Nội tiết 36 năm, nguyên Trưởng khoa Nội tiết BV Chợ Rẫy. Chuyên đái tháo đường, tuyến giáp, rối loạn nội tiết tố."));

        doctors.add(new Doctor(4, "Trần Thị Hồng", "BS",
                "15 năm kinh nghiệm",
                "Bác sĩ Nhi khoa",
                Arrays.asList("Nhi khoa"),
                "14 Lý Tự Trọng, Phường Sài Gòn, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0923456789", 4.6f, 88, "200.000đ",
                "BS Trần Thị Hồng 15 năm Nhi khoa, công tác BV Nhi Đồng 2. Khám trẻ sơ sinh đến 16 tuổi."));

        doctors.add(new Doctor(5, "Nguyễn Văn Trung", "ThS. BS",
                "20 năm kinh nghiệm",
                "Bác sĩ Tim mạch",
                Arrays.asList("Tim mạch", "Nội khoa"),
                "201 Nguyễn Chí Thanh, Quận 5, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0934567890", 4.7f, 145, "350.000đ",
                "ThS.BS Nguyễn Văn Trung 20 năm Tim mạch, tu nghiệp Pháp và Nhật. Chuyên tăng huyết áp, suy tim, rối loạn nhịp, bệnh mạch vành."));

        doctors.add(new Doctor(6, "Lê Thị Hà", "BS. CKI",
                "12 năm kinh nghiệm",
                "Bác sĩ Da liễu",
                Arrays.asList("Da liễu"),
                "120 Lê Lợi, Quận 1, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0945678901", 4.5f, 67, "250.000đ",
                "BS.CKI Lê Thị Hà 12 năm Da liễu. Chuyên mụn trứng cá, viêm da, vảy nến, laser thẩm mỹ da."));

        doctors.add(new Doctor(7, "Phạm Văn Hiếu", "TS. BS",
                "25 năm kinh nghiệm",
                "Bác sĩ Chấn thương chỉnh hình",
                Arrays.asList("Chấn thương chỉnh hình", "Ngoại khoa"),
                "86 Bùi Thị Xuân, Quận 1, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0956789012", 4.8f, 132, "400.000đ",
                "TS.BS Phạm Văn Hiếu 25 năm Chấn thương chỉnh hình, nguyên Phó Trưởng khoa BV Chợ Rẫy. Phẫu thuật khớp, cột sống, xương."));

        doctors.add(new Doctor(8, "Vũ Thị Linh", "ThS. BS",
                "18 năm kinh nghiệm",
                "Bác sĩ Sản phụ khoa",
                Arrays.asList("Sản phụ khoa"),
                "284 Cách Mạng Tháng 8, Quận 3, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0967890123", 4.9f, 198, "300.000đ",
                "ThS.BS Vũ Thị Linh 18 năm Sản phụ khoa. Chuyên thai kỳ nguy cơ cao, vô sinh hiếm muộn, siêu âm chẩn đoán."));

        doctors.add(new Doctor(9, "Hoàng Thị Huyền", "BS. CKII",
                "22 năm kinh nghiệm",
                "Bác sĩ Tai Mũi Họng",
                Arrays.asList("Tai Mũi Họng"),
                "527 Sư Vạn Hạnh, Quận 10, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0978901234", 4.6f, 75, "280.000đ",
                "BS.CKII Hoàng Thị Huyền 22 năm Tai Mũi Họng. Chuyên viêm xoang, amidan, polyp mũi, nội soi TMH."));

        doctors.add(new Doctor(10, "Ngô Minh Lộc", "TS. BS",
                "28 năm kinh nghiệm",
                "Bác sĩ Mắt",
                Arrays.asList("Mắt", "Nhãn khoa"),
                "180 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0989012345", 4.7f, 110, "320.000đ",
                "TS.BS Ngô Minh Lộc 28 năm Nhãn khoa, tu nghiệp Mỹ và Singapore. Phẫu thuật đục TTT, glaucoma, laser mắt."));

        // ===== 15 BÁC SĨ MỚI =====
        doctors.add(new Doctor(11, "Trương Quang Minh", "ThS. BS",
                "14 năm kinh nghiệm",
                "Bác sĩ Thần kinh",
                Arrays.asList("Thần kinh", "Nội khoa"),
                "93 Lê Văn Sỹ, Quận 3, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0911222333", 4.6f, 89, "350.000đ",
                "ThS.BS Trương Quang Minh 14 năm Thần kinh học. Chuyên đau đầu mạn, đột quỵ, Parkinson, động kinh và các bệnh lý thần kinh ngoại biên."));

        doctors.add(new Doctor(12, "Đinh Thị Mai", "BS. CKI",
                "10 năm kinh nghiệm",
                "Bác sĩ Dinh dưỡng",
                Arrays.asList("Dinh dưỡng"),
                "215 Nguyễn Xí, Quận Bình Thạnh, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0922333444", 4.5f, 54, "200.000đ",
                "BS.CKI Đinh Thị Mai 10 năm Dinh dưỡng lâm sàng. Chuyên tư vấn dinh dưỡng trẻ em, người cao tuổi, bệnh nhân tiểu đường và bệnh thận mạn."));

        doctors.add(new Doctor(13, "Võ Thanh Hùng", "PGS. TS. BS",
                "32 năm kinh nghiệm",
                "Bác sĩ Ung bướu",
                Arrays.asList("Ung bướu", "Nội khoa"),
                "3 Nơ Trang Long, Quận Bình Thạnh, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0933444555", 4.9f, 167, "500.000đ",
                "PGS.TS.BS Võ Thanh Hùng 32 năm Ung bướu, nguyên Trưởng khoa BV Ung Bướu TP.HCM. Chuyên ung thư phổi, đại tràng, gan, vú."));

        doctors.add(new Doctor(14, "Phan Thị Thanh Xuân", "ThS. BS",
                "16 năm kinh nghiệm",
                "Bác sĩ Tâm thần",
                Arrays.asList("Tâm thần", "Tâm lý"),
                "766 Võ Văn Kiệt, Quận 5, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0944555666", 4.7f, 93, "300.000đ",
                "ThS.BS Phan Thị Thanh Xuân 16 năm Tâm thần học. Chuyên trầm cảm, lo âu, rối loạn giấc ngủ, tâm thần phân liệt và các rối loạn tâm lý."));

        doctors.add(new Doctor(15, "Lâm Quốc Việt", "TS. BS",
                "19 năm kinh nghiệm",
                "Bác sĩ Hô hấp",
                Arrays.asList("Hô hấp", "Nội khoa"),
                "459 Sư Vạn Hạnh, Quận 10, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0955666777", 4.8f, 121, "350.000đ",
                "TS.BS Lâm Quốc Việt 19 năm Hô hấp học. Chuyên hen suyễn, COPD, viêm phổi, nội soi phế quản và điều trị bệnh phổi mạn tính."));

        doctors.add(new Doctor(16, "Nguyễn Hoàng Nam", "BS. CKII",
                "17 năm kinh nghiệm",
                "Bác sĩ Tiêu hóa - Gan mật",
                Arrays.asList("Tiêu hóa", "Gan mật"),
                "118 Hồng Bàng, Quận 5, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0966777888", 4.7f, 103, "320.000đ",
                "BS.CKII Nguyễn Hoàng Nam 17 năm Tiêu hóa và Gan mật. Chuyên viêm loét dạ dày, viêm gan, xơ gan, nội soi tiêu hóa chẩn đoán và can thiệp."));

        doctors.add(new Doctor(17, "Trần Minh Khoa", "ThS. BS",
                "13 năm kinh nghiệm",
                "Bác sĩ Cơ xương khớp",
                Arrays.asList("Cơ xương khớp", "Nội khoa"),
                "34 Tú Xương, Quận 3, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0977888999", 4.5f, 78, "280.000đ",
                "ThS.BS Trần Minh Khoa 13 năm Cơ xương khớp. Chuyên viêm khớp dạng thấp, thoái hóa khớp, gout, loãng xương và các bệnh lý tự miễn khớp."));

        doctors.add(new Doctor(18, "Bùi Thị Phương Anh", "BS. CKI",
                "9 năm kinh nghiệm",
                "Bác sĩ Y học cổ truyền",
                Arrays.asList("Y học cổ truyền"),
                "179 Nam Kỳ Khởi Nghĩa, Quận 3, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0988999000", 4.6f, 62, "180.000đ",
                "BS.CKI Bùi Thị Phương Anh 9 năm Y học cổ truyền. Chuyên châm cứu, bấm huyệt, xoa bóp phục hồi chức năng, điều trị đau mạn tính bằng phương pháp cổ truyền."));

        doctors.add(new Doctor(19, "Đặng Văn Phúc", "PGS. TS. BS",
                "29 năm kinh nghiệm",
                "Bác sĩ Tim mạch can thiệp",
                Arrays.asList("Tim mạch", "Can thiệp tim mạch"),
                "65 Lê Hồng Phong, Quận 10, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0900111222", 4.9f, 189, "600.000đ",
                "PGS.TS.BS Đặng Văn Phúc 29 năm Tim mạch can thiệp. Chuyên đặt stent mạch vành, sửa van tim, điều trị nhồi máu cơ tim cấp và các bệnh lý tim mạch phức tạp."));

        doctors.add(new Doctor(20, "Lý Thị Ngọc Hà", "ThS. BS",
                "11 năm kinh nghiệm",
                "Bác sĩ Thận - Lọc máu",
                Arrays.asList("Nội thận", "Lọc máu"),
                "222 Đinh Tiên Hoàng, Quận Bình Thạnh, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0910222333", 4.6f, 71, "300.000đ",
                "ThS.BS Lý Thị Ngọc Hà 11 năm Thận học và Lọc máu. Chuyên quản lý bệnh nhân thận mạn giai đoạn cuối, lọc máu chu kỳ, ghép thận và điều trị rối loạn điện giải."));

        doctors.add(new Doctor(21, "Phạm Ngọc Dũng", "TS. BS",
                "24 năm kinh nghiệm",
                "Bác sĩ Phẫu thuật tổng quát",
                Arrays.asList("Ngoại khoa", "Phẫu thuật tổng quát"),
                "740 Trần Hưng Đạo, Quận 5, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0920333444", 4.8f, 156, "450.000đ",
                "TS.BS Phạm Ngọc Dũng 24 năm Phẫu thuật tổng quát. Chuyên phẫu thuật nội soi ổ bụng, cắt túi mật, cắt ruột thừa, thoát vị và các phẫu thuật tiêu hóa."));

        doctors.add(new Doctor(22, "Ngô Thị Lan Anh", "BS. CKII",
                "20 năm kinh nghiệm",
                "Bác sĩ Da liễu - Thẩm mỹ",
                Arrays.asList("Da liễu", "Thẩm mỹ da"),
                "48 Pasteur, Quận 1, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0930444555", 4.7f, 134, "400.000đ",
                "BS.CKII Ngô Thị Lan Anh 20 năm Da liễu thẩm mỹ. Chuyên điều trị nám, tàn nhang, sẹo, trẻ hóa da bằng laser, filler, botox và các liệu trình thẩm mỹ cao cấp."));

        doctors.add(new Doctor(23, "Huỳnh Tấn Phát", "ThS. BS",
                "15 năm kinh nghiệm",
                "Bác sĩ Nhi - Tim mạch Nhi",
                Arrays.asList("Nhi khoa", "Tim mạch nhi"),
                "341 Sư Vạn Hạnh, Quận 10, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0940555666", 4.8f, 99, "350.000đ",
                "ThS.BS Huỳnh Tấn Phát 15 năm chuyên Nhi Tim mạch. Chuyên chẩn đoán và điều trị các bệnh tim bẩm sinh, rối loạn nhịp tim trẻ em và quản lý tim mạch nhi khoa."));

        doctors.add(new Doctor(24, "Cao Thị Bích Ngọc", "BS. CKI",
                "8 năm kinh nghiệm",
                "Bác sĩ Tâm lý lâm sàng",
                Arrays.asList("Tâm lý", "Sức khỏe tâm thần"),
                "12 Nguyễn Thị Minh Khai, Quận 1, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0950666777", 4.5f, 47, "250.000đ",
                "BS.CKI Cao Thị Bích Ngọc 8 năm Tâm lý lâm sàng. Chuyên tư vấn tâm lý, trị liệu nhận thức hành vi (CBT), điều trị stress, trầm cảm và các vấn đề tâm lý thanh thiếu niên."));

        doctors.add(new Doctor(25, "Trịnh Công Sơn", "PGS. TS. BS",
                "33 năm kinh nghiệm",
                "Bác sĩ Phẫu thuật Thần kinh",
                Arrays.asList("Phẫu thuật thần kinh", "Ngoại khoa"),
                "108 Trần Hưng Đạo, Quận 1, TP.HCM",
                R.drawable.ic_doctor_placeholder, "",
                "0960777888", 4.9f, 201, "700.000đ",
                "PGS.TS.BS Trịnh Công Sơn 33 năm Phẫu thuật Thần kinh. Nguyên Trưởng khoa Phẫu thuật Thần kinh BV Chợ Rẫy. Chuyên phẫu thuật u não, thoát vị đĩa đệm, chấn thương cột sống và sọ não."));
    }

    public List<Doctor> getHomeDoctors() {
        return doctors.subList(0, Math.min(10, doctors.size())); // chỉ 10 bác sĩ đầu ở trang chủ
    }

    public List<Doctor> getAllDoctors() {
        return doctors; // tất cả 25 bác sĩ khi ấn ">"
    }

    public Doctor getDoctorById(int id) {
        for (Doctor d : doctors) {
            if (d.getId() == id) return d;
        }
        return null;
    }
}