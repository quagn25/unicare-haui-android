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
    const sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, status, note) VALUES (?, ?, ?, ?, ?)";
    db.query(sql, [patient_id, doctor_id, appointment_datetime, status, note], (err, result) => {
        if (err) {
            console.error("❌ Booking Error:", err.message);
            return res.status(500).json({ message: "Lỗi lưu lịch hẹn" });
        }
        res.status(200).json({ status: "success", message: "Đặt lịch thành công" });
    });
});

// API LẤY LỊCH HẸN CỦA BỆNH NHÂN (JOIN để lấy thông tin bác sĩ)
app.get("/appointments", (req, res) => {
    const { patient_id } = req.query;
    if (!patient_id) return res.status(400).json({ message: "Thiếu patient_id" });

    const sql = `
        SELECT a.*, d.name as doctor_name, d.workplace_address, d.consultation_fee,
               GROUP_CONCAT(s.name SEPARATOR ', ') as specialty_name
        FROM appointments a
        JOIN doctors d ON a.doctor_id = d.id
        LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id
        LEFT JOIN specialties s ON ds.specialty_id = s.id
        WHERE a.patient_id = ?
        GROUP BY a.id
        ORDER BY a.appointment_datetime DESC
    `;
    db.query(sql, [patient_id], (err, results) => {
        if (err) {
            console.error("❌ Fetch Appointments Error:", err.message);
            return res.status(500).json(err);
        }
        res.json(results);
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

                // 4. Gửi mail thật qua Gmail
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


app.get("/", (req, res) => res.send("Server UniCare OK"));

const PORT = process.env.PORT || 3000;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`\n🚀 SERVER ĐANG CHẠY TẠI PORT ${PORT}`);
    console.log(`👉 Link API: http://localhost:${PORT}`);
});
