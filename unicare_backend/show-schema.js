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
        console.error("Error:", err);
        process.exit(1);
    }
    
    db.query("SHOW CREATE TABLE appointments", (err, results) => {
        if (err) {
            console.error(err);
        } else {
            console.log(results[0]['Create Table']);
        }
        db.end();
    });
});
