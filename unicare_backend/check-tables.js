require('dotenv').config();
const mysql = require('mysql2');

const db = mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASS || "",
    database: process.env.DB_NAME || "unicare"
});

db.connect((err) => {
    if (err) {
        console.error("❌ Connection failed:", err);
        process.exit(1);
    }
    
    db.query("SELECT * FROM appointments ORDER BY id DESC LIMIT 5", (err, appointments) => {
        console.log("\n--- RECENT APPOINTMENTS ---");
        console.table(appointments);
        
        db.query("SELECT * FROM notifications ORDER BY id DESC LIMIT 5", (err, notifications) => {
            console.log("\n--- RECENT NOTIFICATIONS ---");
            console.table(notifications);
            db.end();
        });
    });
});
