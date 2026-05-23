require('dotenv').config();
const mysql = require('mysql2');

console.log("=== CHẨN ĐOÁN KẾT NỐI DATABASE UNICARE ===\n");
console.log("Cấu hình hiện tại đọc từ .env:");
console.log(`- Host: ${process.env.DB_HOST || "localhost"}`);
console.log(`- User: ${process.env.DB_USER || "root"}`);
console.log(`- Password: ${process.env.DB_PASS ? "*****" : "(Trống)"}`);
console.log(`- Database: ${process.env.DB_NAME || "unicare"}\n`);

const db = mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASS || "",
    database: process.env.DB_NAME || "unicare"
});

db.connect((err) => {
    if (err) {
        console.error("❌ KẾT NỐI THẤT BẠI!");
        console.error("--------------------------------------------------");
        console.error(`Mã lỗi: ${err.code}`);
        console.error(`Chi tiết: ${err.message}`);
        console.error("--------------------------------------------------");
        
        if (err.code === 'ECONNREFUSED') {
            console.log("\n👉 GỢI Ý KHẮC PHỤC:");
            console.log("1. Phần mềm MySQL (XAMPP, Laragon, MySQL Server) chưa được bật.");
            console.log("   Hãy mở XAMPP/Laragon và nhấn 'Start' ở dòng MySQL.");
            console.log("2. MySQL đang chạy ở cổng khác cổng mặc định 3306.");
        } else if (err.code === 'ER_ACCESS_DENIED_ERROR') {
            console.log("\n👉 GỢI Ý KHẮC PHỤC:");
            console.log("   Sai tên User hoặc Password đăng nhập MySQL trong file '.env'.");
            console.log("   Hãy mở file 'unicare_backend/.env' và sửa lại 'DB_USER' và 'DB_PASS' khớp với tài khoản MySQL của bạn.");
        } else if (err.code === 'ER_BAD_DB_ERROR') {
            console.log("\n👉 GỢI Ý KHẮC PHỤC:");
            console.log(`   Database '${process.env.DB_NAME || "unicare"}' không tồn tại.`);
            console.log("   Hãy tạo database này bằng cách chạy câu lệnh SQL sau trong MySQL Workbench / phpMyAdmin:");
            console.log(`   CREATE DATABASE ${process.env.DB_NAME || "unicare"};`);
        }
        process.exit(1);
    }
    
    console.log("✅ KẾT NỐI MYSQL THÀNH CÔNG!\n");
    
    db.query("SHOW TABLES", (err, results) => {
        if (err) {
            console.error("❌ Không thể lấy danh sách bảng:", err.message);
            db.end();
            return;
        }
        
        const tables = results.map(row => Object.values(row)[0]);
        console.log(`Danh sách các bảng đang có: [${tables.join(", ")}]\n`);
        
        const requiredTables = ['users', 'patients', 'doctors', 'specialties', 'appointments'];
        let missing = false;
        
        requiredTables.forEach(t => {
            if (!tables.includes(t)) {
                console.error(`❌ THIẾU BẢNG QUAN TRỌNG: '${t}'`);
                missing = true;
            } else {
                console.log(`  ✔ Bảng '${t}' đang hoạt động.`);
            }
        });
        
        if (missing) {
            console.log("\n👉 GỢI Ý KHẮC PHỤC:");
            console.log("   Database của bạn đang thiếu các bảng quan trọng trên.");
            console.log("   Hãy mở công cụ truy vấn SQL của bạn và chạy lại file script SQL tạo bảng để khởi tạo cấu trúc.");
            db.end();
            return;
        }
        
        // Kiểm tra cấu trúc bảng users
        db.query("DESCRIBE users", (err, cols) => {
            if (err) {
                console.error("❌ Không thể kiểm tra bảng users:", err.message);
                db.end();
                return;
            }
            console.log("\n=== CẤU TRÚC BẢNG 'users' ===");
            cols.forEach(c => {
                console.log(`  - Cột: ${c.Field} (${c.Type}) | Null: ${c.Null} | Key: ${c.Key}`);
            });
            
            // Kiểm tra cấu trúc bảng patients
            db.query("DESCRIBE patients", (err, colsPat) => {
                if (err) {
                    console.error("❌ Không thể kiểm tra bảng patients:", err.message);
                    db.end();
                    return;
                }
                console.log("\n=== CẤU TRÚC BẢNG 'patients' ===");
                colsPat.forEach(c => {
                    console.log(`  - Cột: ${c.Field} (${c.Type}) | Null: ${c.Null} | Key: ${c.Key}`);
                });
                
                console.log("\n🎉 CHẨN ĐOÁN HOÀN TẤT: Cấu trúc cơ sở dữ liệu hoàn toàn khớp và sẵn sàng!");
                db.end();
            });
        });
    });
});
