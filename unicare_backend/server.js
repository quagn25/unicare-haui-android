require('dotenv').config();
const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");
const nodemailer = require("nodemailer");
const bcrypt = require("bcryptjs");

const app = express();

app.use(express.json());
app.use(cors());

// Log mọi request để debug
app.use((req, res, next) => {
    console.log(`>>> [${req.method}] ${req.url}`);
    next();
});

// KẾT NỐI DATABASE DÙNG BIẾN MÔI TRƯỜNG
const db = mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASS || "",
    database: process.env.DB_NAME || "unicare"
});

db.connect((err) => {
    if (err) console.error("❌ MySQL Error:", err.message);
    else {
        console.log("✅ MySQL Connected (Securely)");
        // Tạo bảng otps nếu chưa có
        const createOtpTable = `
            CREATE TABLE IF NOT EXISTS otps (
                id INT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(255) NOT NULL,
                otp VARCHAR(6) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        `;
        db.query(createOtpTable, (err) => {
            if (err) console.error("❌ Lỗi tạo bảng otps:", err.message);
        });

        // Tự động kiểm tra và thêm cột 'note' vào bảng appointments nếu chưa có
        db.query("SHOW COLUMNS FROM appointments LIKE 'note'", (err, colResults) => {
            if (!err && colResults && colResults.length === 0) {
                db.query("ALTER TABLE appointments ADD COLUMN note TEXT NULL", (alterErr) => {
                    if (alterErr) console.error("❌ Lỗi thêm cột note vào bảng appointments:", alterErr.message);
                    else console.log("🌱 Đã thêm cột note vào bảng appointments thành công!");
                });
            }
        });

        // Tự động sửa lỗi schema nếu bảng notifications cũ thiếu cột 'content'
        db.query("SHOW COLUMNS FROM notifications LIKE 'content'", (err, colResults) => {
            if (!err && colResults && colResults.length === 0) {
                console.log("⚠️ Bảng notifications cũ thiếu cột 'content'. Tiến hành xoá bảng để tự động khởi tạo lại...");
                db.query("DROP TABLE IF EXISTS notifications", (dropErr) => {
                    if (dropErr) console.error("❌ Lỗi xoá bảng notifications:", dropErr.message);
                    createAndSeedNotifications();
                });
            } else {
                createAndSeedNotifications();
            }
        });

        function createAndSeedNotifications() {
            const createNotificationTable = `
                CREATE TABLE IF NOT EXISTS notifications (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    title VARCHAR(255) NOT NULL,
                    content TEXT NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    is_read TINYINT(1) DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            `;
            db.query(createNotificationTable, (err) => {
                if (err) {
                    console.error("❌ Lỗi tạo bảng notifications:", err.message);
                } else {
                    // Kiểm tra xem có dữ liệu chưa
                    db.query("SELECT COUNT(*) AS count FROM notifications", (err, results) => {
                        if (!err && results && results[0] && results[0].count === 0) {
                        // Bảng trống, tiến hành seed dữ liệu mẫu tương tự ảnh chụp
                        const now = new Date();
                        const seedQuery = `
                            INSERT INTO notifications (title, content, type, is_read, created_at) VALUES
                            ('Nhắc lịch khám', 'Bạn có lịch khám Tim mạch vào 09:00 ngày mai với BS. Nguyễn Văn An.', 'LICH_KHAM', 0, ?),
                            ('Sắp đến lịch tiêm', 'Vắc-xin Cúm mùa - 08:30 ngày 22/05/2026 tại Phòng 201.', 'TIEM_CHUNG', 0, ?),
                            ('Kết quả xét nghiệm', 'Kết quả xét nghiệm máu của bạn đã có. Nhấn để xem chi tiết.', 'KET_QUA', 0, ?),
                            ('Ưu đãi tháng 5', 'Giảm 20% gói khám tổng quát cho khách hàng thân thiết.', 'UU_DAI', 1, ?),
                            ('Cập nhật ứng dụng', 'Phiên bản 2.4 đã có. Trải nghiệm giao diện mới mượt mà hơn.', 'CAP_NHAT', 1, ?)
                        `;
                        const date1 = new Date(now.getTime() - 2 * 60 * 60 * 1000); // 2 giờ trước
                        const date2 = new Date(now.getTime() - 5 * 60 * 60 * 1000); // 5 giờ trước
                        const date3 = new Date(now.getTime() - 24 * 60 * 60 * 1000); // 1 ngày trước
                        const date4 = new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000); // 2 ngày trước
                        const date5 = new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000); // 3 ngày trước
                        
                        db.query(seedQuery, [date1, date2, date3, date4, date5], (err) => {
                            if (err) console.error("❌ Lỗi seed notifications:", err.message);
                            else console.log("🌱 Đã seed dữ liệu thông báo mẫu thành công!");
                        });
                    }
                });
            }
        });
        }

        // 3. Tiến hành cập nhật/seed dữ liệu bác sĩ và vắc xin để khớp ảnh mẫu
        db.query("UPDATE doctors SET name = 'Nguyễn Văn An', title = 'ThS. BS', workplace_address = 'UniCare - Phòng 105', bio = 'Tim mạch' WHERE id = 1", (err) => {
            if (err) console.error("❌ Lỗi update doctor 1:", err.message);
        });
        
        db.query("UPDATE doctors SET name = 'Trần Thị Bình', title = 'BS', workplace_address = 'UniCare - Phòng 203', bio = 'Nha khoa' WHERE id = 2", (err) => {
            if (err) console.error("❌ Lỗi update doctor 2:", err.message);
        });
        
        db.query("UPDATE doctors SET name = 'Phạm Quốc Dũng', title = 'TS. BS', workplace_address = 'UniCare - Phòng 101', bio = 'Khám tổng quát' WHERE id = 4", (err) => {
            if (err) console.error("❌ Lỗi update doctor 4:", err.message);
        });
        
        // Seed các bác sĩ vắc-xin ảo
        const seedVaccineDoctors = `
            INSERT IGNORE INTO doctors (id, name, title, workplace_address, bio, experience_years, consultation_fee, is_active) VALUES
            (21, 'Vắc-xin Cúm mùa', 'Liều nhắc lại', 'Phòng 201 - UniCare', 'Tiêm chủng', 10, 0, 1),
            (22, 'Vắc-xin HPV', 'Mũi 2/3', 'Phòng 105 - UniCare', 'Tiêm chủng', 10, 0, 1),
            (23, 'Vắc-xin Viêm gan B', 'Mũi 3/3', 'Phòng 203 - UniCare', 'Tiêm chủng', 10, 0, 1),
            (24, 'Vắc-xin Covid-19', 'Mũi nhắc', 'Phòng 105 - UniCare', 'Tiêm chủng', 10, 0, 1),
            (25, 'Vắc-xin Sởi - Quai bị - Rubella', 'Mũi 1', 'Phòng 201 - UniCare', 'Tiêm chủng', 10, 0, 1)
        `;
        db.query(seedVaccineDoctors, (err) => {
            if (err) console.error("❌ Lỗi seed vaccine doctors:", err.message);
            else console.log("🌱 Đã seed hoặc cập nhật danh sách bác sĩ/vắc xin thành công!");
        });
    }
});

// CẤU HÌNH GỬI MAIL
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

// API ĐĂNG KÝ
app.post("/register", async (req, res) => {
    const { username, password, role, fullName, dob, gender, phone } = req.body;
    console.log("📩 Đang xử lý đăng ký cho:", username);

    try {
        // Băm mật khẩu (Salt round = 10)
        const hashedPassword = await bcrypt.hash(password, 10);

        let formattedDob = null;
        if (dob && dob.includes("/")) {
            const parts = dob.split("/");
            formattedDob = `${parts[2]}-${parts[1]}-${parts[0]}`;
        }

        db.beginTransaction((err) => {
            if (err) return res.status(500).json({ message: "Transaction Error" });

            db.query("INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)",
            [username, hashedPassword, role, phone], (err, results) => {
                if (err) {
                    console.error("❌ User Error:", err.message);
                    return db.rollback(() => res.status(400).json({ message: "Lỗi tạo tài khoản: " + err.message }));
                }

                const userId = results.insertId;
                if (role === "PATIENT") {
                    db.query("INSERT INTO patients (user_id, full_name, dob, gender, phone, address) VALUES (?, ?, ?, ?, ?, ?)",
                    [userId, fullName, formattedDob, gender, phone, "Chưa cập nhật"], (err) => {
                        if (err) {
                            console.error("❌ Patient Error:", err.message);
                            return db.rollback(() => res.status(500).json({ message: "Lỗi lưu thông tin bệnh nhân" }));
                        }
                        db.commit(() => {
                            console.log("✅ Đăng ký thành công: " + username);
                            res.status(200).send();
                        });
                    });
                } else {
                    // Logic cho Bác sĩ (mặc định gắn vào chuyên khoa ID = 1)
                    db.query("INSERT INTO doctors (user_id, name, phone, title, experience_years, consultation_fee, bio) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    [userId, fullName, phone, "Bác sĩ", 1, 200000, "Chưa cập nhật tiểu sử"], (err, results) => {
                        if (err) {
                            console.error("❌ Doctor Error:", err.message);
                            return db.rollback(() => res.status(500).json({ message: "Lỗi lưu thông tin bác sĩ" }));
                        }
                        
                        const doctorId = results.insertId;
                        db.query("INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES (?, ?)", [doctorId, 1], (err) => {
                            db.commit(() => {
                                console.log("✅ Đăng ký Bác sĩ thành công: " + username);
                                res.status(200).send();
                            });
                        });
                    });
                }
            });
        });
    } catch (error) {
        res.status(500).json({ message: "Lỗi mã hóa mật khẩu" });
    }
});

// API ĐĂNG NHẬP
app.post("/login", (req, res) => {
    const { username, password } = req.body;

    const query = `
        SELECT u.*, p.full_name
        FROM users u
        LEFT JOIN patients p ON u.id = p.user_id
        WHERE u.username = ?
    `;

    db.query(query, [username], async (err, result) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        
        if (result.length > 0) {
            const user = result[0];
            // So sánh mật khẩu đã băm
            const isMatch = await bcrypt.compare(password, user.password);
            
            if (isMatch) {
                res.json({ status: "success", user: user });
            } else {
                res.status(401).json({ status: "fail", message: "Sai tài khoản hoặc mật khẩu" });
            }
        } else {
            res.status(401).json({ status: "fail", message: "Sai tài khoản hoặc mật khẩu" });
        }
    });
});

// Route lấy danh sách bác sĩ
app.get('/doctors', (req, res) => {
    const sql = `
        SELECT d.*, GROUP_CONCAT(s.name SEPARATOR ', ') as specialties
        FROM doctors d
        LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id
        LEFT JOIN specialties s ON ds.specialty_id = s.id
        GROUP BY d.id
    `;
    db.query(sql, (err, results) => {
        if (err) return res.status(500).json(err);
        res.json(results);
    });
});

// API ĐẶT LỊCH HẸN
app.post("/appointments", (req, res) => {
    const { patient_id, doctor_id, appointment_datetime, status, note } = req.body;
    
    // Resolve patient_id (if it is user_id, map to patients.id)
    db.query("SELECT id FROM patients WHERE user_id = ?", [patient_id], (err, patientResults) => {
        let actualPatientId = patient_id;
        if (!err && patientResults && patientResults.length > 0) {
            actualPatientId = patientResults[0].id;
        }

        const sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, status, note) VALUES (?, ?, ?, ?, ?)";
        db.query(sql, [actualPatientId, doctor_id, appointment_datetime, status, note], (err, result) => {
            if (err) {
                console.error("❌ Booking Error:", err.message);
                return res.status(500).json({ message: "Lỗi lưu lịch hẹn" });
            }
            res.status(200).json({ status: "success", message: "Đặt lịch thành công" });
        });
    });
});

// API LẤY LỊCH HẸN CỦA BỆNH NHÂN (Có tự động seed nếu chưa có dữ liệu)
app.get("/appointments", (req, res) => {
    const { patient_id } = req.query;
    if (!patient_id) return res.status(400).json({ message: "Thiếu patient_id" });

    // Resolve patient_id (if it is user_id, map to patients.id)
    db.query("SELECT id FROM patients WHERE user_id = ?", [patient_id], (err, patientResults) => {
        let actualPatientId = patient_id;
        if (!err && patientResults && patientResults.length > 0) {
            actualPatientId = patientResults[0].id;
        }

        // Hàm thực hiện lấy dữ liệu sau khi đảm bảo đã có dữ liệu trong DB
        const fetchAppointments = () => {
            const sql = `
                SELECT a.*, d.name as doctor_name, d.title as doctor_title, d.bio as doctor_bio, d.workplace_address, d.consultation_fee,
                       GROUP_CONCAT(s.name SEPARATOR ', ') as specialty_name
                FROM appointments a
                JOIN doctors d ON a.doctor_id = d.id
                LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id
                LEFT JOIN specialties s ON ds.specialty_id = s.id
                WHERE a.patient_id = ? AND a.status != 'CANCELLED'
                GROUP BY a.id
                ORDER BY CASE WHEN a.status = 'PENDING' THEN 0 ELSE 1 END, a.appointment_datetime ASC
            `;
            db.query(sql, [actualPatientId], (err, results) => {
                if (err) {
                    console.error("❌ Fetch Appointments Error:", err.message);
                    return res.status(500).json(err);
                }
                res.json(results);
            });
        };

        // Kiểm tra xem bệnh nhân này đã có cuộc hẹn nào chưa
        db.query("SELECT COUNT(*) AS count FROM appointments WHERE patient_id = ?", [actualPatientId], (err, countResults) => {
            if (err) {
                console.error("❌ Check Appointments Count Error:", err.message);
                return res.status(500).json(err);
            }

            if (countResults[0].count === 0) {
                console.log(`🌱 Chưa có lịch hẹn cho bệnh nhân ${actualPatientId}. Tiến hành tự động seed dữ liệu mẫu.`);
                const seedAppointmentsQuery = `
                    INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, status, note) VALUES
                    (?, 1, '2026-06-01 09:00:00', 'PENDING', 'Khám Tim mạch'),
                    (?, 2, '2026-06-02 14:30:00', 'PENDING', 'Khám Nha khoa'),
                    (?, 4, '2026-03-12 08:30:00', 'COMPLETED', 'Khám tổng quát'),
                    (?, 21, '2026-05-22 08:30:00', 'PENDING', 'Liều nhắc lại'),
                    (?, 22, '2026-06-10 09:00:00', 'PENDING', 'Mũi 2/3'),
                    (?, 23, '2026-07-05 14:00:00', 'PENDING', 'Mũi 3/3'),
                    (?, 24, '2026-03-12 10:00:00', 'COMPLETED', 'Mũi nhắc'),
                    (?, 25, '2026-01-20 08:00:00', 'COMPLETED', 'Mũi 1')
                `;
                db.query(seedAppointmentsQuery, [actualPatientId, actualPatientId, actualPatientId, actualPatientId, actualPatientId, actualPatientId, actualPatientId, actualPatientId], (err) => {
                    if (err) {
                        console.error("❌ Seed Appointments Error:", err.message);
                        // Vẫn trả về rỗng thay vì bị crash nếu lỗi seed xảy ra
                        return res.json([]);
                    }
                    console.log("✅ Seed lịch hẹn mẫu thành công!");
                    fetchAppointments();
                });
            } else {
                fetchAppointments();
            }
        });
    });
});

// API ĐỔI LỊCH HẸN (+7 ngày)
app.post("/appointments/reschedule", (req, res) => {
    const { appointmentId } = req.body;
    if (!appointmentId) return res.status(400).json({ message: "Thiếu appointmentId" });

    const sql = "UPDATE appointments SET appointment_datetime = DATE_ADD(appointment_datetime, INTERVAL 7 DAY) WHERE id = ?";
    db.query(sql, [appointmentId], (err, result) => {
        if (err) {
            console.error("❌ Reschedule Error:", err.message);
            return res.status(500).json({ message: "Lỗi dời lịch hẹn" });
        }
        res.json({ status: "success", message: "Đổi lịch thành công" });
    });
});

// API CẬP NHẬT CHI TIẾT LỊCH HẸN (Đổi ngày/giờ và note)
app.post("/appointments/update-details", (req, res) => {
    const { appointmentId, appointment_datetime, note } = req.body;
    if (!appointmentId) return res.status(400).json({ message: "Thiếu appointmentId" });

    const sql = "UPDATE appointments SET appointment_datetime = ?, note = ? WHERE id = ?";
    db.query(sql, [appointment_datetime, note, appointmentId], (err, result) => {
        if (err) {
            console.error("❌ Update Details Error:", err.message);
            return res.status(500).json({ message: "Lỗi cập nhật lịch hẹn" });
        }
        res.json({ status: "success", message: "Cập nhật lịch hẹn thành công" });
    });
});

// API HUỶ LỊCH HẸN
app.post("/appointments/cancel", (req, res) => {
    const { appointmentId } = req.body;
    if (!appointmentId) return res.status(400).json({ message: "Thiếu appointmentId" });

    const sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ?";
    db.query(sql, [appointmentId], (err, result) => {
        if (err) {
            console.error("❌ Cancel Error:", err.message);
            return res.status(500).json({ message: "Lỗi huỷ lịch hẹn" });
        }
        res.json({ status: "success", message: "Huỷ lịch hẹn thành công" });
    });
});

// Route lấy danh sách users (nếu cần)
app.get('/users', (req, res) => {
    db.query("SELECT * FROM users", (err, results) => {
        if (err) return res.status(500).json(err);
        res.json(results);
    });
});

// API GỬI MÃ OTP QUÊN MẬT KHẨU
app.post("/forgot-password/send-otp", (req, res) => {
    const { email } = req.body;

    // 1. Kiểm tra email có tồn tại trong hệ thống không
    db.query("SELECT id, username FROM users WHERE email = ?", [email], (err, results) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        if (results.length === 0) {
            return res.status(404).json({ status: "fail", message: "Email không tồn tại trong hệ thống" });
        }

        const username = results[0].username;
        // 2. Tạo mã OTP ngẫu nhiên 6 chữ số
        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        // 3. Lưu OTP vào database (xóa OTP cũ của email này nếu có)
        db.query("DELETE FROM otps WHERE email = ?", [email], () => {
            db.query("INSERT INTO otps (email, otp) VALUES (?, ?)", [email, otp], (err) => {
                if (err) return res.status(500).json({ status: "error", message: "Lỗi lưu OTP" });

                // 4. Kiểm tra xem có cấu hình Email thật không, nếu không cấu hình thì bật CHẾ ĐỘ DEMO
                const isDemoMode = !process.env.EMAIL_USER || 
                                   process.env.EMAIL_USER === "your_email@gmail.com" || 
                                   !process.env.EMAIL_PASS || 
                                   process.env.EMAIL_PASS.includes("your_app_password");

                if (isDemoMode) {
                    console.log("\n=============================================");
                    console.log("🔒 [CHẾ ĐỘ DEMO - KHÔNG CÓ CẤU HÌNH GMAIL THẬT]");
                    console.log(`🔑 OTP ĐỂ LẤY LẠI MẬT KHẨU CỦA ${email} LÀ:`);
                    console.log(`👉 ${otp} 👈`);
                    console.log("=============================================\n");

                    return res.json({ 
                        status: "success", 
                        message: "Mã OTP đã được tạo (Vui lòng xem log server NodeJS để lấy mã)",
                        username: username // Gửi kèm username về cho App
                    });
                }

                // Gửi mail thật qua Gmail
                const mailOptions = {
                    from: `"UniCare Support" <${process.env.EMAIL_USER}>`,
                    to: email,
                    subject: "Mã OTP đặt lại mật khẩu UniCare",
                    text: `Mã OTP của bạn là: ${otp}. Mã có hiệu lực trong 5 phút.`
                };

                transporter.sendMail(mailOptions, (error, info) => {
                    if (error) {
                        console.error("❌ Mail Error:", error);
                        return res.status(500).json({ status: "error", message: "Không thể gửi email: " + error.message });
                    }
                    console.log("✅ OTP sent to:", email);
                    res.json({ 
                        status: "success", 
                        message: "Mã OTP đã được gửi",
                        username: username // Gửi kèm username về cho App
                    });
                });
            });
        });
    });
});

// API ĐẶT LẠI MẬT KHẨU
app.post("/forgot-password/reset", async (req, res) => {
    const { username, email, otp, password } = req.body;

    try {
        // 1. Kiểm tra OTP có khớp và còn hiệu lực không
        const sql = "SELECT * FROM otps WHERE email = ? AND otp = ? AND created_at > NOW() - INTERVAL 5 MINUTE";
        db.query(sql, [email, otp], async (err, results) => {
            if (err) return res.status(500).json({ status: "error", message: err.message });
            if (results.length === 0) {
                return res.status(400).json({ status: "fail", message: "Mã OTP không đúng hoặc đã hết hạn" });
            }

            // 2. Băm mật khẩu mới
            const hashedPassword = await bcrypt.hash(password, 10);

            // 3. Cập nhật mật khẩu mới cho user
            db.query("UPDATE users SET password = ? WHERE username = ? AND email = ?", 
            [hashedPassword, username, email], (err, updateResult) => {
                if (err) return res.status(500).json({ status: "error", message: err.message });
                
                if (updateResult.affectedRows > 0) {
                    db.query("DELETE FROM otps WHERE email = ?", [email]);
                    res.json({ status: "success", message: "Đổi mật khẩu thành công" });
                } else {
                    res.status(404).json({ status: "fail", message: "Thông tin tài khoản không khớp" });
                }
            });
        });
    } catch (error) {
        res.status(500).json({ message: "Lỗi mã hóa mật khẩu" });
    }
});


// API LẤY DANH SÁCH THÔNG BÁO
app.get("/notifications", (req, res) => {
    const userId = req.query.userId || 0;
    
    // Lấy thông báo cá nhân (user_id = userId) HOẶC thông báo chung (user_id IS NULL hoặc user_id = 0)
    const sql = `
        SELECT * FROM notifications 
        WHERE user_id = ? OR user_id IS NULL OR user_id = 0 
        ORDER BY created_at DESC
    `;
    
    db.query(sql, [userId], (err, results) => {
        if (err) {
            console.error("❌ Lỗi lấy thông báo:", err.message);
            return res.status(500).json({ status: "error", message: err.message });
        }
        res.json({
            status: "success",
            data: results
        });
    });
});

// API ĐÁNH DẤU ĐỌC HẾT TẤT CẢ THÔNG BÁO
app.post("/notifications/read-all", (req, res) => {
    const { userId } = req.body;
    
    // Đánh dấu tất cả thông báo của người dùng này hoặc thông báo chung là đã đọc
    const sql = `
        UPDATE notifications 
        SET is_read = 1 
        WHERE user_id = ? OR user_id IS NULL OR user_id = 0
    `;
    
    db.query(sql, [userId || 0], (err, results) => {
        if (err) {
            console.error("❌ Lỗi đọc hết thông báo:", err.message);
            return res.status(500).json({ status: "error", message: err.message });
        }
        res.json({
            status: "success",
            message: "Đã đánh dấu đọc tất cả thông báo"
        });
    });
});


app.get("/", (req, res) => res.send("Server UniCare OK"));

const PORT = process.env.PORT || 3000;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`\n🚀 SERVER ĐANG CHẠY TẠI PORT ${PORT}`);
    console.log(`👉 Link API: http://localhost:${PORT}`);
});
