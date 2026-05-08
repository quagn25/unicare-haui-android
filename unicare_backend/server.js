require('dotenv').config(); // Tải biến môi trường từ file .env
const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");

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
    else console.log("✅ MySQL Connected (Securely)");
});

// API ĐĂNG KÝ
app.post("/register", (req, res) => {
    const { username, password, role, fullName, dob, gender, phone } = req.body;
    console.log("📩 Đang xử lý đăng ký cho:", username);

    let formattedDob = null;
    if (dob && dob.includes("/")) {
        const parts = dob.split("/");
        formattedDob = `${parts[2]}-${parts[1]}-${parts[0]}`;
    }

    db.beginTransaction((err) => {
        if (err) return res.status(500).json({ message: "Transaction Error" });

        db.query("INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)",
        [username, password, role, phone], (err, results) => {
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
                db.query("INSERT INTO doctors (user_id, name, phone) VALUES (?, ?, ?)",
                [userId, fullName, phone], (err) => {
                    if (err) {
                        console.error("❌ Doctor Error:", err.message);
                        return db.rollback(() => res.status(500).json({ message: "Lỗi lưu thông tin bác sĩ" }));
                    }
                    db.commit(() => {
                        console.log("✅ Đăng ký thành công: " + username);
                        res.status(200).send();
                    });
                });
            }
        });
    });
});

// API ĐĂNG NHẬP
app.post("/login", (req, res) => {
    const { username, password } = req.body;
    db.query("SELECT * FROM users WHERE username = ? AND password = ?", [username, password], (err, result) => {
        if (err) return res.status(500).json({ status: "error", message: err.message });
        if (result.length > 0) {
            res.json({ status: "success", user: result[0] });
        } else {
            res.status(401).json({ status: "fail", message: "Sai tài khoản hoặc mật khẩu" });
        }
    });
});

app.get("/", (req, res) => res.send("Server UniCare OK"));

const PORT = process.env.PORT || 3000;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`\n🚀 SERVER ĐANG CHẠY TẠI PORT ${PORT}`);
    console.log(`👉 Link API: http://localhost:${PORT}`);
});
