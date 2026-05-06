const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");
const bodyParser = require("body-parser");

const app = express();

// middleware
app.use(cors());
app.use(bodyParser.json());

// =====================
// KẾT NỐI MYSQL
// =====================
const db = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "abc123!",
    database: "unicare"
});

db.connect((err) => {
    if (err) {
        console.log("❌ MySQL connection error:", err);
    } else {
        console.log("✅ MySQL connected");
    }
});

// =====================
// TEST API
// =====================
app.get("/", (req, res) => {
    res.send("UniCare API is running...");
});

// =====================
// GET USERS (TEST DB)
// =====================
app.get("/users", (req, res) => {
    db.query("SELECT * FROM users", (err, result) => {
        if (err) {
            res.json({ error: err });
        } else {
            res.json(result);
        }
    });
});

// =====================
// LOGIN API
// =====================
app.post("/login", (req, res) => {
    const { username, password } = req.body;

    db.query(
        "SELECT * FROM users WHERE username = ? AND password = ?",
        [username, password],
        (err, result) => {
            if (err) {
                res.json({ status: "error", message: err });
            } else {
                if (result.length > 0) {
                    res.json({
                        status: "success",
                        user: result[0]
                    });
                } else {
                    res.json({
                        status: "fail",
                        message: "Invalid login"
                    });
                }
            }
        }
    );
});

// =====================
// START SERVER
// =====================
const PORT = 3000;

app.listen(PORT, () => {
    console.log(`🚀 Server running on port ${PORT}`);
});