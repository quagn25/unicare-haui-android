require('dotenv').config();
const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");
const nodemailer = require("nodemailer");
const bcrypt = require("bcryptjs");

const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: process.env.EMAIL_USER || "",
        pass: process.env.EMAIL_PASS || ""
    }
});

const app = express();

app.use(express.json());
app.use(cors());

// Log mọi request để dễ debug
app.use((req, res, next) => {
    console.log(`>>> [${req.method}] ${req.url} - Body:`, req.body);
    next();
});

// KẾT NỐI DATABASE DÙNG POOL
const db = mysql.createPool({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASS || "",
    database: process.env.DB_NAME || "unicare",
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// Khởi tạo và kiểm tra Schema
db.getConnection((err, connection) => {
    if (err) {
        console.error("❌ KHÔNG THỂ KẾT NỐI DATABASE:", err.message);
        return;
    }
    console.log("✅ Kết nối Database thành công!");
    connection.release();

    // Tự động tạo bảng otps
    db.query(`CREATE TABLE IF NOT EXISTS otps (
        id INT AUTO_INCREMENT PRIMARY KEY,
        email VARCHAR(255) NOT NULL,
        otp VARCHAR(6) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )`);

    // Tự động tạo bảng medical_records nếu chưa có
    db.query(`
        CREATE TABLE IF NOT EXISTS medical_records (
            id INT AUTO_INCREMENT PRIMARY KEY,
            patient_id INT NOT NULL,
            doctor_id INT NOT NULL,
            appointment_id INT UNIQUE,
            visit_date DATE NOT NULL,
            diagnosis TEXT NOT NULL,
            prescription TEXT,
            doctor_notes TEXT
        )
    `);

    // Tự động tạo bảng treatment_plans nếu chưa có
    db.query(`
        CREATE TABLE IF NOT EXISTS treatment_plans (
            id INT AUTO_INCREMENT PRIMARY KEY,
            record_id INT NOT NULL,
            medicine_name VARCHAR(100) NOT NULL,
            method VARCHAR(100) NOT NULL,
            times_per_day INT NOT NULL,
            purpose TEXT,
            guide TEXT
        )
    `);

    // Kiểm tra và thêm cột email vào users nếu thiếu
    db.query("SHOW COLUMNS FROM users LIKE 'email'", (err, results) => {
        if (!err && results.length === 0) {
            db.query("ALTER TABLE users ADD COLUMN email VARCHAR(255) NULL", () => {
                console.log("🌱 Đã bổ sung cột email vào bảng users");
            });
        }
    });

    // Seed dữ liệu mẫu cho bệnh án & phác đồ điều trị nếu bảng rỗng
    db.query("SELECT COUNT(*) AS count FROM medical_records", (err, mrRes) => {
        if (!err && mrRes && mrRes[0].count === 0) {
            console.log("🌱 Database chưa có medical_records. Tiến hành seed dữ liệu bệnh án mẫu...");
            db.query("SELECT id FROM appointments WHERE id = 12", (err, appRes) => {
                if (!err && appRes && appRes.length === 0) {
                    db.query("INSERT IGNORE INTO appointments (id, patient_id, doctor_id, appointment_datetime, status, note) VALUES (12, 3, 1, '2026-05-27 09:00:00', 'PENDING', 'Da mẩn đỏ')");
                }
            });

            const insertRecordSql = `
                INSERT INTO medical_records (id, patient_id, doctor_id, appointment_id, visit_date, diagnosis, prescription, doctor_notes)
                VALUES (1, 3, 1, 12, '2026-05-27', 'Viêm da cơ địa', '- Thuốc bôi ngoài da Hydrocortisone\\n- Uống Loratadin 10mg', 'Tái khám sau 7 ngày')
            `;
            db.query(insertRecordSql, (err, recordResult) => {
                if (!err && recordResult) {
                    const recordId = recordResult.insertId || 1;
                    console.log("✅ Đã seed medical_record ID:", recordId);
                    const insertPlansSql = `
                        INSERT INTO treatment_plans (record_id, medicine_name, method, times_per_day, purpose, guide)
                        VALUES 
                        (?, 'Hydrocortisone', 'Bôi ngoài da', 2, 'Giảm mẩn đỏ, ngứa da', 'Thoa một lớp mỏng lên vùng da bị tổn thương vào buổi sáng và tối.'),
                        (?, 'Loratadin 10mg', 'Uống', 1, 'Chống dị ứng', 'Uống 1 viên sau khi ăn tối.')
                    `;
                    db.query(insertPlansSql, [recordId, recordId], (err) => {
                        if (err) console.error("❌ Lỗi seed treatment_plans:", err.message);
                        else console.log("✅ Đã seed treatment_plans mẫu thành công!");
                    });
                } else {
                    console.error("❌ Lỗi seed medical_records:", err ? err.message : "unknown");
                }
            });
        }
    });
});

// --- API ĐĂNG NHẬP ---
app.post("/login", (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ status: "fail", message: "Vui lòng nhập tài khoản và mật khẩu" });
    }

    // Truy vấn linh hoạt theo Username hoặc Email
    const query = `
        SELECT u.*, p.full_name, p.id as patient_id
        FROM users u
        LEFT JOIN patients p ON u.id = p.user_id
        WHERE u.username = ? OR u.email = ?
    `;

    db.query(query, [username, username], async (err, result) => {
        if (err) {
            console.error("❌ LỖI SQL LOGIN:", err);
            return res.status(500).json({
                status: "error",
                message: "Lỗi cơ sở dữ liệu",
                sqlError: err.message
            });
        }

        try {
            if (result && result.length > 0) {
                const user = result[0];
                const inputPassword = String(password);
                const dbPassword = String(user.password || "");

                if (!dbPassword) {
                    return res.status(401).json({ status: "fail", message: "Tài khoản chưa có mật khẩu" });
                }

                let isMatch = false;
                if (dbPassword.startsWith("$2")) {
                    isMatch = await bcrypt.compare(inputPassword, dbPassword);
                } else {
                    isMatch = (inputPassword === dbPassword);
                }

                if (isMatch) {
                    delete user.password; // Không gửi mật khẩu về App
                    res.json({ status: "success", user: user });
                } else {
                    res.status(401).json({ status: "fail", message: "Mật khẩu không chính xác" });
                }
            } else {
                res.status(401).json({ status: "fail", message: "Tài khoản không tồn tại" });
            }
        } catch (error) {
            console.error("❌ LỖI XỬ LÝ LOGIN:", error);
            res.status(500).json({ status: "error", message: "Lỗi hệ thống: " + error.message });
        }
    });
});

// --- API ĐĂNG KÝ ---
app.post("/register", async (req, res) => {
    const { username, password, role, fullName, dob, gender, phone, email } = req.body;
    const userEmail = email || phone;

    try {
        const hashedPassword = await bcrypt.hash(String(password), 10);
        let formattedDob = null;
        if (dob && dob.includes("/")) {
            const parts = dob.split("/");
            formattedDob = `${parts[2]}-${parts[1]}-${parts[0]}`;
        }

        db.getConnection((err, conn) => {
            if (err) return res.status(500).json({ message: "Lỗi kết nối DB" });

            conn.beginTransaction((err) => {
                if (err) { conn.release(); return res.status(500).json({ message: "Lỗi transaction" }); }

                conn.query("INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)",
                [username, hashedPassword, role, userEmail], (err, results) => {
                    if (err) {
                        return conn.rollback(() => { conn.release(); res.status(400).json({ message: "Tài khoản/Email đã tồn tại" }); });
                    }

                    const userId = results.insertId;
                    if (role === "PATIENT") {
                        conn.query("INSERT INTO patients (user_id, full_name, dob, gender, phone, address) VALUES (?, ?, ?, ?, ?, ?)",
                        [userId, fullName, formattedDob, gender, phone, "Chưa cập nhật"], (err) => {
                            if (err) return conn.rollback(() => { conn.release(); res.status(500).json({ message: "Lỗi lưu thông tin bệnh nhân" }); });
                            conn.commit(() => { conn.release(); res.status(200).send(); });
                        });
                    } else {
                        conn.query("INSERT INTO doctors (user_id, name, phone, title, experience_years, consultation_fee, bio) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        [userId, fullName, phone, "Bác sĩ", 1, 200000, "Chưa cập nhật"], (err, dRes) => {
                            if (err) return conn.rollback(() => { conn.release(); res.status(500).json({ message: "Lỗi lưu bác sĩ" }); });
                            conn.query("INSERT INTO doctor_specialties (doctor_id, specialty_id) VALUES (?, ?)", [dRes.insertId, 1], (err) => {
                                conn.commit(() => { conn.release(); res.status(200).send(); });
                            });
                        });
                    }
                });
            });
        });
    } catch (e) { res.status(500).json({ message: "Lỗi server" }); }
});

// --- QUÊN MẬT KHẨU ---
app.post("/forgot-password/send-otp", (req, res) => {
    const { email } = req.body;
    db.query("SELECT username, email FROM users WHERE email = ? OR username = ?", [email, email], (err, results) => {
        if (err || results.length === 0) return res.status(404).json({ status: "fail", message: "Email không tồn tại" });

        const user = results[0];
        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        db.query("DELETE FROM otps WHERE email = ?", [user.email], () => {
            db.query("INSERT INTO otps (email, otp) VALUES (?, ?)", [user.email, otp], (err) => {
                const isDemo = !process.env.EMAIL_USER || process.env.EMAIL_USER.includes("your_email");
                if (isDemo) {
                    console.log(`\n🔑 OTP CHO ${user.email} LÀ: ${otp}\n`);
                    return res.json({ status: "success", message: "Xem OTP trong log server", username: user.username, email: user.email });
                }
                transporter.sendMail({
                    from: `"UniCare" <${process.env.EMAIL_USER}>`,
                    to: user.email,
                    subject: "OTP Reset Password",
                    text: `Mã OTP của bạn là: ${otp}`
                }, (e) => {
                    if (e) {
                        console.error("❌ Mail Error (Fallback to DEMO mode):", e);
                        console.log("\n=============================================");
                        console.log("🔒 [CHẾ ĐỘ CỨU HỘ - GỬI GMAIL THẤT BẠI]");
                        console.log(`🔑 OTP ĐỂ LẤY LẠI MẬT KHẨU CỦA ${user.email} LÀ:`);
                        console.log(`👉 ${otp} 👈`);
                        console.log("=============================================\n");
                        return res.json({ status: "success", message: "Tạo OTP thành công (Lỗi gửi Mail, xem log server)", username: user.username, email: user.email });
                    }
                    res.json({ status: "success", message: "OTP đã gửi thành công", username: user.username, email: user.email });
                });
            });
        });
    });
});

app.post("/forgot-password/reset", async (req, res) => {
    const { username, email, otp, password } = req.body;
    db.query("SELECT * FROM otps WHERE email = ? AND otp = ? AND created_at > NOW() - INTERVAL 5 MINUTE", [email, otp], async (err, results) => {
        if (err || results.length === 0) return res.status(400).json({ message: "OTP sai hoặc hết hạn" });
        const hash = await bcrypt.hash(String(password), 10);
        db.query("UPDATE users SET password = ? WHERE username = ?", [hash, username], (err) => {
            if (err) return res.status(500).json({ message: "Lỗi cập nhật" });
            db.query("DELETE FROM otps WHERE email = ?", [email]);
            res.json({ status: "success", message: "Thành công" });
        });
    });
});

app.get('/doctors', (req, res) => {
    db.query(`SELECT d.*, GROUP_CONCAT(s.name SEPARATOR ', ') as specialties FROM doctors d LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id LEFT JOIN specialties s ON ds.specialty_id = s.id GROUP BY d.id`, (err, resu) => {
        if (err) return res.status(500).json(err); res.json(resu);
    });
});
app.get("/appointments", (req, res) => {
    db.query("SELECT id FROM patients WHERE user_id = ?", [req.query.patient_id], (err, pRes) => {
        let pId = (pRes && pRes.length > 0) ? pRes[0].id : req.query.patient_id;
        const sql = `SELECT a.*, d.name as doctor_name, d.title as doctor_title, d.bio as doctor_bio, d.workplace_address, d.consultation_fee, GROUP_CONCAT(s.name SEPARATOR ', ') as specialty_name FROM appointments a JOIN doctors d ON a.doctor_id = d.id LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id LEFT JOIN specialties s ON ds.specialty_id = s.id WHERE a.patient_id = ? AND a.status != 'CANCELLED' GROUP BY a.id ORDER BY CASE WHEN a.status = 'PENDING' THEN 0 ELSE 1 END, a.appointment_datetime ASC`;
        db.query(sql, [pId], (err, results) => { if (err) return res.status(500).json(err); res.json(results); });
    });
});
app.get("/patients/profile", (req, res) => {
    db.query(`SELECT p.id as patientId, p.user_id, p.full_name, p.dob, p.gender, p.phone, p.address, u.email, u.username FROM patients p JOIN users u ON p.user_id = u.id WHERE p.user_id = ?`, [req.query.userId], (err, results) => {
        if (err || results.length === 0) return res.status(404).json({ status: "fail" });
        
        let profile = results[0];
        if (profile.dob) {
            const dateObj = new Date(profile.dob);
            if (!isNaN(dateObj.getTime())) {
                const day = String(dateObj.getDate()).padStart(2, '0');
                const month = String(dateObj.getMonth() + 1).padStart(2, '0');
                const year = dateObj.getFullYear();
                profile.dob = `${day}/${month}/${year}`;
            }
        }
        
        if (profile.gender === 'MALE') profile.gender = 'Nam';
        else if (profile.gender === 'FEMALE') profile.gender = 'Nữ';
        else if (profile.gender === 'OTHER') profile.gender = 'Khác';

        res.json({ status: "success", profile: profile });
    });
});

app.post("/patients/profile/update", (req, res) => {
    const { userId, fullName, dob, gender, phone, address, email } = req.body;
    if (!userId) return res.status(400).json({ status: "fail", message: "Thiếu userId" });

    let dbGender = 'OTHER';
    if (gender === 'Nam') dbGender = 'MALE';
    else if (gender === 'Nữ') dbGender = 'FEMALE';

    let formattedDob = dob;
    if (dob && dob.includes("/")) {
        const parts = dob.split("/");
        formattedDob = `${parts[2]}-${parts[1]}-${parts[0]}`;
    }

    const updatePatientSql = `
        UPDATE patients 
        SET full_name = ?, dob = ?, gender = ?, phone = ?, address = ? 
        WHERE user_id = ?
    `;

    const updateUserSql = `
        UPDATE users 
        SET email = ? 
        WHERE id = ?
    `;

    db.query(updatePatientSql, [fullName, formattedDob, dbGender, phone, address, userId], (err, result) => {
        if (err) {
            console.error("❌ Lỗi cập nhật patients:", err.message);
            return res.status(500).json({ status: "error", message: err.message });
        }

        db.query(updateUserSql, [email, userId], (err2, result2) => {
            if (err2) {
                console.error("❌ Lỗi cập nhật users (email):", err2.message);
                return res.status(500).json({ status: "error", message: err2.message });
            }

            // --- THÊM THÔNG BÁO TẠI ĐÂY ---
            const notifTitle = "Cập nhật hồ sơ thành công";
            const notifContent = "Thông tin cá nhân của bạn đã được cập nhật thành công trên hệ thống UniCare.";
            db.query(
                "INSERT INTO notifications (user_id, title, content, type, is_read) VALUES (?, ?, ?, ?, 0)",
                [userId, notifTitle, notifContent, "KET_QUA"],
                (notifErr) => {
                    if (notifErr) console.error("❌ Notification Error:", notifErr.message);
                }
            );

            res.json({
                status: "success",
                message: "Cập nhật thông tin hồ sơ thành công"
            });
        });
    });
});

app.get("/appointments/:id/details", (req, res) => {
    const appointmentId = req.params.id;
    const sql = `
        SELECT a.*, 
               d.name as doctor_name, d.title as doctor_title, d.bio as doctor_bio, d.workplace_address, d.consultation_fee,
               GROUP_CONCAT(s.name SEPARATOR ', ') as specialty_name,
               p.full_name as patient_name, p.dob as patient_dob, p.phone as patient_phone
        FROM appointments a
        JOIN doctors d ON a.doctor_id = d.id
        LEFT JOIN doctor_specialties ds ON d.id = ds.doctor_id
        LEFT JOIN specialties s ON ds.specialty_id = s.id
        JOIN patients p ON a.patient_id = p.id
        WHERE a.id = ?
        GROUP BY a.id
    `;
    db.query(sql, [appointmentId], (err, results) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        if (results.length === 0) return res.status(404).json({ status: "fail", message: "Không tìm thấy lịch hẹn" });
        res.json({ status: "success", data: results[0] });
    });
});

app.get("/appointments/:id/medical-record", (req, res) => {
    const appointmentId = req.params.id;
    const sql = `
        SELECT mr.*, p.full_name as patient_name, p.dob as patient_dob, u.email as patient_email
        FROM medical_records mr
        JOIN patients p ON mr.patient_id = p.id
        JOIN users u ON p.user_id = u.id
        WHERE mr.appointment_id = ?
    `;
    db.query(sql, [appointmentId], (err, results) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        if (results.length === 0) return res.status(404).json({ status: "fail", message: "Chưa có bệnh án" });
        res.json({ status: "success", data: results[0] });
    });
});

app.get("/medical-records/:recordId/treatment-plans", (req, res) => {
    const recordId = req.params.recordId;
    const sql = `
        SELECT * 
        FROM treatment_plans 
        WHERE record_id = ?
    `;
    db.query(sql, [recordId], (err, results) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        res.json({ status: "success", data: results });
    });
});

app.post("/appointments", (req, res) => {
    const { patient_id, doctor_id, appointment_datetime, status, note } = req.body;

    db.query("SELECT id, user_id FROM patients WHERE user_id = ?", [patient_id], (err, patientResults) => {
        let actualPatientId = patient_id;
        let userId = patient_id;
        if (!err && patientResults && patientResults.length > 0) {
            actualPatientId = patientResults[0].id;
            userId = patientResults[0].user_id;
        }

        const sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_datetime, status, note) VALUES (?, ?, ?, ?, ?)";
        db.query(sql, [actualPatientId, doctor_id, appointment_datetime, status || 'PENDING', note], (err, result) => {
            if (err) {
                console.error("❌ Booking Error:", err.message);
                return res.status(500).json({ status: "error", message: "Lỗi lưu lịch hẹn: " + err.message });
            }

            db.query("SELECT name, title FROM doctors WHERE id = ?", [doctor_id], (docErr, docResults) => {
                let doctorText = "Bác sĩ";
                if (!docErr && docResults && docResults.length > 0) {
                    doctorText = `${docResults[0].title || 'BS'}. ${docResults[0].name}`;
                }

                let formattedTime = appointment_datetime;
                try {
                    formattedTime = appointment_datetime.replace("T", " ").replace("Z", "");
                } catch (e) {}

                const isVaccine = (note || '').toLowerCase().includes("tiêm chủng");
                const notifType = isVaccine ? "TIEM_CHUNG" : "LICH_KHAM";
                const actionText = isVaccine ? "tiêm chủng" : "khám";

                const notifTitle = "Đặt lịch thành công";
                const notifContent = `Bạn đã đặt lịch ${actionText} thành công với ${doctorText} vào lúc ${formattedTime}. Vui lòng chờ xác nhận.`;
                db.query(
                    "INSERT INTO notifications (user_id, title, content, type, is_read) VALUES (?, ?, ?, ?, 0)",
                    [userId, notifTitle, notifContent, notifType],
                    (notifErr) => {
                        if (notifErr) console.error("❌ Notification insert error:", notifErr.message);
                        res.status(200).json({ status: "success", message: "Đặt lịch thành công" });
                    }
                );
            });
        });
    });
});

app.post("/appointments/cancel", (req, res) => {
    const { appointmentId } = req.body;
    if (!appointmentId) return res.status(400).json({ status: "fail", message: "Thiếu ID cuộc hẹn" });

    const getAppSql = `
        SELECT a.patient_id, a.appointment_datetime, a.note, d.name as doc_name, d.title as doc_title, p.user_id
        FROM appointments a
        JOIN doctors d ON a.doctor_id = d.id
        JOIN patients p ON a.patient_id = p.id
        WHERE a.id = ?
    `;
    db.query(getAppSql, [appointmentId], (err, results) => {
        if (err || results.length === 0) {
            db.query("UPDATE appointments SET status = 'CANCELLED' WHERE id = ?", [appointmentId], (cancelErr) => {
                if (cancelErr) return res.status(500).json({ status: "error", message: cancelErr.message });
                return res.json({ status: "success", message: "Đã hủy lịch hẹn" });
            });
            return;
        }

        const appData = results[0];
        const doctorText = `${appData.doc_title || 'BS'}. ${appData.doc_name}`;
        
        let formattedTime = appData.appointment_datetime;
        try {
            if (formattedTime instanceof Date) {
                const d = formattedTime;
                formattedTime = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}:${String(d.getSeconds()).padStart(2,'0')}`;
            } else {
                formattedTime = String(formattedTime).replace("T", " ").replace("Z", "").substring(0, 19);
            }
        } catch (e) {}

        db.query("DELETE FROM appointments WHERE id = ?", [appointmentId], (cancelErr) => {
            if (cancelErr) return res.status(500).json({ status: "error", message: cancelErr.message });

            const isVaccine = (appData.note || '').toLowerCase().includes("tiêm chủng");
            const notifType = isVaccine ? "TIEM_CHUNG" : "LICH_KHAM";
            const actionText = isVaccine ? "tiêm chủng" : "khám";

            const notifTitle = "Lịch hẹn đã hủy";
            const notifContent = `Bạn đã hủy thành công lịch hẹn ${actionText} với ${doctorText} vào lúc ${formattedTime}.`;
            db.query(
                "INSERT INTO notifications (user_id, title, content, type, is_read) VALUES (?, ?, ?, ?, 0)",
                [appData.user_id, notifTitle, notifContent, notifType],
                (notifErr) => {
                    if (notifErr) console.error("❌ Cancel Notification insert error:", notifErr.message);
                    res.json({ status: "success", message: "Hủy lịch thành công" });
                }
            );
        });
    });
});

app.post("/appointments/reschedule", (req, res) => {
    const { appointmentId } = req.body;
    if (!appointmentId) return res.status(400).json({ status: "fail", message: "Thiếu ID cuộc hẹn" });

    const getAppSql = `
        SELECT a.patient_id, a.appointment_datetime, a.note, d.name as doc_name, d.title as doc_title, p.user_id
        FROM appointments a
        JOIN doctors d ON a.doctor_id = d.id
        JOIN patients p ON a.patient_id = p.id
        WHERE a.id = ?
    `;
    db.query(getAppSql, [appointmentId], (err, results) => {
        const updateSql = "UPDATE appointments SET appointment_datetime = DATE_ADD(appointment_datetime, INTERVAL 7 DAY) WHERE id = ?";
        db.query(updateSql, [appointmentId], (updateErr) => {
            if (updateErr) return res.status(500).json({ status: "error", message: updateErr.message });

            if (results.length > 0) {
                const appData = results[0];
                const doctorText = `${appData.doc_title || 'BS'}. ${appData.doc_name}`;
                const newDate = new Date(new Date(appData.appointment_datetime).getTime() + 7 * 24 * 60 * 60 * 1000);
                let formattedTime = newDate.toISOString().replace("T", " ").substring(0, 19);

                const isVaccine = (appData.note || '').toLowerCase().includes("tiêm chủng");
                const notifType = isVaccine ? "TIEM_CHUNG" : "LICH_KHAM";

                const notifTitle = "Đổi lịch thành công";
                const notifContent = `Lịch hẹn của bạn với ${doctorText} đã được đổi sang lúc ${formattedTime} (+7 ngày).`;
                db.query(
                    "INSERT INTO notifications (user_id, title, content, type, is_read) VALUES (?, ?, ?, ?, 0)",
                    [appData.user_id, notifTitle, notifContent, notifType],
                    (notifErr) => {
                        if (notifErr) console.error("❌ Reschedule Notification insert error:", notifErr.message);
                    }
                );
            }
            res.json({ status: "success", message: "Đổi lịch thành công (+7 ngày)" });
        });
    });
});

app.post("/appointments/update-details", (req, res) => {
    const { appointmentId, appointment_datetime, note } = req.body;
    if (!appointmentId) return res.status(400).json({ status: "fail", message: "Thiếu ID cuộc hẹn" });

    const getAppSql = `
        SELECT a.patient_id, a.note as old_note, d.name as doc_name, d.title as doc_title, p.user_id
        FROM appointments a
        JOIN doctors d ON a.doctor_id = d.id
        JOIN patients p ON a.patient_id = p.id
        WHERE a.id = ?
    `;
    db.query(getAppSql, [appointmentId], (err, results) => {
        const updateSql = "UPDATE appointments SET appointment_datetime = ?, note = ? WHERE id = ?";
        db.query(updateSql, [appointment_datetime, note, appointmentId], (updateErr) => {
            if (updateErr) return res.status(500).json({ status: "error", message: updateErr.message });

            if (results.length > 0) {
                const appData = results[0];
                const doctorText = `${appData.doc_title || 'BS'}. ${appData.doc_name}`;

                const finalNote = note || appData.old_note || '';
                const isVaccine = finalNote.toLowerCase().includes("tiêm chủng");
                const notifType = isVaccine ? "TIEM_CHUNG" : "LICH_KHAM";
                const actionText = isVaccine ? "tiêm chủng" : "khám";

                const notifTitle = "Thay đổi lịch hẹn thành công";
                const notifContent = `Thông tin lịch hẹn ${actionText} với ${doctorText} đã được thay đổi thành công sang lúc ${appointment_datetime}.`;
                db.query(
                    "INSERT INTO notifications (user_id, title, content, type, is_read) VALUES (?, ?, ?, ?, 0)",
                    [appData.user_id, notifTitle, notifContent, notifType],
                    (notifErr) => {
                        if (notifErr) console.error("❌ Update Notification insert error:", notifErr.message);
                    }
                );
            }
            res.json({ status: "success", message: "Cập nhật lịch hẹn thành công" });
        });
    });
});

app.get("/notifications", (req, res) => {
    const userId = req.query.userId;
    if (!userId) return res.status(400).json({ status: "fail", message: "Thiếu userId" });

    const sql = `
        SELECT * FROM notifications 
        WHERE user_id = ? OR user_id IS NULL
        ORDER BY created_at DESC
        LIMIT 50
    `;
    db.query(sql, [userId], (err, results) => {
        if (err) {
            console.error("❌ Lỗi lấy notifications:", err.message);
            return res.status(500).json({ status: "error", message: err.message });
        }
        res.json({ status: "success", data: results });
    });
});

app.post("/notifications/read-all", (req, res) => {
    const { userId } = req.body;
    if (!userId) return res.status(400).json({ status: "fail", message: "Thiếu userId" });

    db.query(
        "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0",
        [userId],
        (err, result) => {
            if (err) {
                console.error("❌ Lỗi đọc tất cả notifications:", err.message);
                return res.status(500).json({ status: "error", message: err.message });
            }
            res.json({ status: "success", message: "Đã đánh dấu đọc tất cả thông báo" });
        }
    );
});

// --- ĐỔI MẬT KHẨU (Khi đã đăng nhập) ---
app.post("/change-password", (req, res) => {
    const { userId, currentPassword, newPassword } = req.body;

    if (!userId || !currentPassword || !newPassword) {
        return res.status(400).json({ status: "fail", message: "Thiếu thông tin bắt buộc" });
    }

    if (newPassword.length < 6) {
        return res.status(400).json({ status: "fail", message: "Mật khẩu mới phải có ít nhất 6 ký tự" });
    }

    // 1. Lấy mật khẩu hiện tại từ DB
    db.query("SELECT password FROM users WHERE id = ?", [userId], (err, results) => {
        if (err) {
            console.error("❌ Lỗi truy vấn change-password:", err.message);
            return res.status(500).json({ status: "error", message: "Lỗi server" });
        }
        if (results.length === 0) {
            return res.status(404).json({ status: "fail", message: "Người dùng không tồn tại" });
        }

        const hashedPassword = results[0].password;

        // 2. So sánh mật khẩu hiện tại
        bcrypt.compare(currentPassword, hashedPassword, (compareErr, isMatch) => {
            if (compareErr) {
                return res.status(500).json({ status: "error", message: "Lỗi xác thực mật khẩu" });
            }
            if (!isMatch) {
                return res.status(401).json({ status: "fail", message: "Mật khẩu hiện tại không đúng" });
            }

            // 3. Hash mật khẩu mới và cập nhật DB
            bcrypt.hash(newPassword, 10, (hashErr, newHashedPassword) => {
                if (hashErr) {
                    return res.status(500).json({ status: "error", message: "Lỗi mã hóa mật khẩu" });
                }

                db.query(
                    "UPDATE users SET password = ? WHERE id = ?",
                    [newHashedPassword, userId],
                    (updateErr) => {
                        if (updateErr) {
                            console.error("❌ Lỗi cập nhật mật khẩu:", updateErr.message);
                            return res.status(500).json({ status: "error", message: "Không thể cập nhật mật khẩu" });
                        }
                        console.log(`✅ Đổi mật khẩu thành công cho userId: ${userId}`);
                        res.json({ status: "success", message: "Đổi mật khẩu thành công" });
                    }
                );
            });
        });
    });
});

app.get("/patients/profile", (req, res) => {
    const userId = req.query.userId;
    if (!userId) return res.status(400).json({ status: "fail", message: "Missing userId" });

    const query = `
        SELECT u.username, u.email, p.full_name as p_full_name, p.dob, p.gender, p.address, p.phone
        FROM users u
        LEFT JOIN patients p ON u.id = p.user_id
        WHERE u.id = ?
    `;

    db.query(query, [userId], (err, results) => {
        if (err) return res.status(500).json({ status: "error", message: "Lỗi DB", error: err.message });
        if (results.length === 0) return res.status(404).json({ status: "fail", message: "Không tìm thấy user" });

        const data = results[0];
        res.json({
            status: "success",
            profile: {
                full_name: data.p_full_name,
                username: data.username,
                email: data.email,
                phone: data.phone,
                dob: data.dob,
                gender: data.gender,
                address: data.address
            }
        });
    });
});

app.get("/", (req, res) => res.send("Server UniCare OK"));

const PORT = process.env.PORT || 3000;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`\n🚀 SERVER ĐANG CHẠY TẠI PORT ${PORT}`);
});
