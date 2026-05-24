const mysql = require('mysql2');
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'abc123!',
    database: 'unicare'
});

const run = async () => {
    const promiseQuery = (sql, params) => new Promise((resolve, reject) => {
        db.query(sql, params, (err, res) => err ? reject(err) : resolve(res));
    });

    try {
        await promiseQuery("ALTER TABLE doctors MODIFY user_id INT NULL");
        await promiseQuery("ALTER TABLE doctors DROP INDEX user_id");
    } catch(e) {
        console.log("Index already dropped or constraint exists");
    }

    try {
        const sql = `
        INSERT INTO doctors (id, user_id, specialty_id, name, bio, contact_phone, title, consultation_fee, experience_years) 
        VALUES 
        (21, NULL, 1, 'Vắc-xin Cúm mùa', 'Phòng 201 - UniCare', '19001001', 'Liều nhắc lại', 250000.0, 0),
        (22, NULL, 1, 'Vắc-xin HPV', 'Phòng 105 - UniCare', '19001001', 'Mũi 2/3', 1200000.0, 0),
        (23, NULL, 1, 'Vắc-xin Viêm gan B', 'Phòng 203 - UniCare', '19001001', 'Mũi 3/3', 180000.0, 0),
        (24, NULL, 1, 'Vắc-xin Covid-19', 'Phòng 105 - UniCare', '19001001', 'Mũi nhắc', 0.0, 0),
        (25, NULL, 1, 'Vắc-xin Sởi - Quai bị - Rubella', 'Phòng 201 - UniCare', '19001001', 'Mũi 1', 350000.0, 0) 
        ON DUPLICATE KEY UPDATE name=VALUES(name);
        `;
        await promiseQuery(sql);
        console.log('Seeded virtual vaccine doctors');
    } catch(e) {
        console.error(e);
    }
    db.end();
};

run();
