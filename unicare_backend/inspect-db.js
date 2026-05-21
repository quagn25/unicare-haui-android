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
        console.error("Error connecting:", err);
        process.exit(1);
    }
    
    console.log("Connected to MySQL successfully!");
    
    const queries = {
        tables: "SHOW TABLES",
        doctors: "SELECT * FROM doctors",
        specialties: "SELECT * FROM specialties",
        appointments: "SELECT * FROM appointments",
        notifications: "SELECT * FROM notifications"
    };
    
    const keys = Object.keys(queries);
    let i = 0;
    
    function runNext() {
        if (i >= keys.length) {
            db.end();
            return;
        }
        const name = keys[i];
        const sql = queries[name];
        console.log(`\n=== TABLE: ${name.toUpperCase()} ===`);
        db.query(sql, (err, results) => {
            if (err) {
                console.error(err);
            } else {
                console.log(results);
            }
            i++;
            runNext();
        });
    }
    
    runNext();
});
