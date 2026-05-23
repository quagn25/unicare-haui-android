const mysql = require('mysql2');
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'abc123!',
    database: 'unicare'
});

db.query('SELECT u.username, u.email, p.full_name as p_full_name, p.dob, p.gender, p.address, p.phone_number FROM users u LEFT JOIN patients p ON u.id = p.user_id WHERE u.id = 26', (err, results) => {
    if(err) console.error(err);
    else console.log(JSON.stringify(results, null, 2));
    db.end();
});
