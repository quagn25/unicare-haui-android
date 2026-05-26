package com.haui.UniCare.feature.patients.appointment.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.haui.UniCare.R;

public class VaccineNoteActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvVaccineTitle, tvPreNote, tvPostNote;
    private Button btnUnderstand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_note);

        btnBack = findViewById(R.id.btn_back);
        tvVaccineTitle = findViewById(R.id.tv_vaccine_title);
        tvPreNote = findViewById(R.id.tv_pre_vaccine_note);
        tvPostNote = findViewById(R.id.tv_post_vaccine_note);
        btnUnderstand = findViewById(R.id.btn_understand);

        btnBack.setOnClickListener(v -> finish());
        btnUnderstand.setOnClickListener(v -> finish());

        String vaccineName = getIntent().getStringExtra("vaccine_name");
        if (vaccineName == null || vaccineName.isEmpty()) {
            vaccineName = "Vắc-xin (Chưa rõ loại)";
        }
        
        tvVaccineTitle.setText(vaccineName);
        generateMockNotes(vaccineName.toLowerCase());
    }

    private void generateMockNotes(String vName) {
        String preNote = "";
        String postNote = "";

        if (vName.contains("cúm") || vName.contains("influenza")) {
            preNote = "• Đảm bảo bạn không đang bị sốt cao hoặc mắc bệnh nhiễm trùng cấp tính.\n" +
                      "• Ăn uống đầy đủ trước khi tiêm, không để bụng đói.\n" +
                      "• Mặc áo tay ngắn hoặc áo rộng rãi để thuận tiện cho việc tiêm vào bắp tay.\n" +
                      "• Báo cho bác sĩ nếu bạn bị dị ứng nặng với trứng (do một số loại vắc-xin cúm được nuôi cấy trên phôi gà).";
            
            postNote = "• Ở lại cơ sở y tế theo dõi 30 phút sau khi tiêm.\n" +
                       "• Có thể xuất hiện triệu chứng nhẹ như sưng đau tại chỗ tiêm, sốt nhẹ, hoặc uể oải. Đây là phản ứng bình thường và tự hết sau 1-2 ngày.\n" +
                       "• Uống nhiều nước, ăn nhiều hoa quả.\n" +
                       "• Nếu sốt trên 38.5 độ C, có thể dùng thuốc hạ sốt Paracetamol. KHÔNG dùng Aspirin.\n" +
                       "• Nếu có dấu hiệu khó thở, phát ban toàn thân, hãy đến ngay cơ sở y tế gần nhất.";
        } else if (vName.contains("hpv") || vName.contains("ung thư cổ tử cung")) {
            preNote = "• Không cần nhịn ăn trước khi tiêm.\n" +
                      "• Không tiêm nếu đang mang thai hoặc có tiền sử sốc phản vệ với liều tiêm HPV trước đó.\n" +
                      "• Nếu đang mắc bệnh cấp tính hoặc sốt, nên lùi lịch tiêm đến khi khỏe lại.\n" +
                      "• Tuân thủ đúng phác đồ (2 hoặc 3 mũi tùy độ tuổi và loại vắc-xin) để đạt hiệu quả cao nhất.";

            postNote = "• Theo dõi 30 phút tại điểm tiêm.\n" +
                       "• Tránh mang vác vật nặng bằng tay vừa được tiêm trong vòng 24 giờ.\n" +
                       "• Phản ứng phụ thường gặp: đau, đỏ, sưng ở vị trí tiêm; đau đầu; sốt nhẹ. Bạn có thể chườm mát tại chỗ tiêm để giảm đau.\n" +
                       "• Duy trì chế độ sinh hoạt bình thường. Nhớ cài đặt nhắc nhở cho mũi tiêm tiếp theo (nếu có).";
        } else if (vName.contains("viêm gan b") || vName.contains("hepatitis b")) {
            preNote = "• Vắc-xin viêm gan B rất an toàn. Vui lòng mang theo kết quả xét nghiệm HBsAg và Anti-HBs (nếu có) để bác sĩ tư vấn.\n" +
                      "• Ăn no vừa phải, tinh thần thoải mái.\n" +
                      "• Nếu bạn đang điều trị bệnh lý suy giảm miễn dịch, hãy báo trước cho bác sĩ.";

            postNote = "• Ngồi lại theo dõi 30 phút.\n" +
                       "• Hạn chế uống rượu bia trong 2-3 ngày đầu sau tiêm để gan không phải hoạt động quá sức.\n" +
                       "• Vết tiêm có thể hơi ê ẩm, hãy giữ sạch sẽ, không xoa bóp dầu nóng hay đắp lá lên vết tiêm.\n" +
                       "• Ghi nhớ lịch tiêm mũi nhắc lại (thường là sau 1 tháng và 6 tháng).";
        } else if (vName.contains("covid")) {
            preNote = "• Ngủ đủ giấc vào đêm trước ngày tiêm.\n" +
                      "• Không uống rượu bia hoặc các chất kích thích trước ngày tiêm.\n" +
                      "• Chuẩn bị sẵn giấy tờ tùy thân và sổ tiêm chủng/app điện tử.\n" +
                      "• Khai báo thành thật tiền sử dị ứng, bệnh nền và các loại thuốc đang sử dụng.";

            postNote = "• Theo dõi sức khỏe chặt chẽ trong vòng 7 ngày đầu, đặc biệt là 3 ngày đầu tiên.\n" +
                       "• Có thể gặp các phản ứng: sốt, ớn lạnh, đau mỏi cơ, mệt mỏi. Hãy uống nhiều nước và dùng Paracetamol nếu sốt > 38.5 độ.\n" +
                       "• Tuyệt đối KHÔNG bôi đắp bất cứ thứ gì lên vết tiêm.\n" +
                       "• Tránh vận động thể thao cường độ cao trong vòng 3 ngày.\n" +
                       "• Báo ngay cho y tế nếu thấy: đau tức ngực, khó thở, nhịp tim đập nhanh, hoặc phát ban lan rộng.";
        } else {
            preNote = "• Vui lòng ăn nhẹ trước khi tiêm, không để bụng quá đói.\n" +
                      "• Mang theo sổ tiêm chủng hoặc hồ sơ y tế cũ (nếu có).\n" +
                      "• Thông báo cho bác sĩ nếu bạn đang mang thai, cho con bú, hoặc có tiền sử dị ứng thuốc/thức ăn.\n" +
                      "• Giữ tinh thần thoải mái, mặc trang phục rộng rãi.";

            postNote = "• Theo dõi phản ứng cơ thể trong 30 phút tại cơ sở y tế và 24h tiếp theo tại nhà.\n" +
                       "• Giữ vùng tiêm sạch sẽ, khô ráo. Không xoa dầu gió hay đắp các bài thuốc dân gian.\n" +
                       "• Ăn uống đầy đủ dinh dưỡng, bổ sung nhiều vitamin C.\n" +
                       "• Nếu có dấu hiệu bất thường như sốt cao liên tục không hạ, co giật, khó thở, nôn mửa, phải đến ngay bệnh viện.";
        }

        tvPreNote.setText(preNote);
        tvPostNote.setText(postNote);
    }
}
