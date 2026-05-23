require('dotenv').config();
const mysql = require('mysql2');

const db = mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASS || "",
    database: process.env.DB_NAME || "unicare"
});

db.connect((err) => {
    if (err) { console.error(err); process.exit(1); }
    
    // Check constraints
    db.query("SELECT CONSTRAINT_NAME, CONSTRAINT_TYPE FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_NAME='appointments' AND TABLE_SCHEMA='unicare'", (err, r) => {
        console.log("Constraints:", JSON.stringify(r, null, 2));
        
        // Drop the UNIQUE index on doctor_id + appointment_datetime
        // First drop FK, then recreate as non-unique
        db.query("ALTER TABLE appointments DROP FOREIGN KEY appointments_ibfk_2", (err, r) => {
            if (err) console.log("Drop FK2:", err.message);
            else console.log("Dropped FK2");
            
            db.query("ALTER TABLE appointments DROP INDEX doctor_id", (err, r) => {
                if (err) console.log("Drop index:", err.message);
                else console.log("Dropped UNIQUE index doctor_id");
                
                // Re-add FK without UNIQUE
                db.query("ALTER TABLE appointments ADD CONSTRAINT appointments_ibfk_2 FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE", (err, r) => {
                    if (err) console.log("Re-add FK:", err.message);
                    else console.log("Re-added FK (non-unique)");
                    
                    db.end();
                });
            });
        });
    });
});
