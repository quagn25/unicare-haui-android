const http = require('http');

function testRequest(method, path, body, label) {
    return new Promise((resolve) => {
        const bodyStr = body ? JSON.stringify(body) : null;
        const options = {
            hostname: 'localhost',
            port: 3000,
            path: path,
            method: method,
            headers: {
                'Content-Type': 'application/json',
                ...(bodyStr ? { 'Content-Length': Buffer.byteLength(bodyStr) } : {})
            }
        };

        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', (chunk) => { data += chunk; });
            res.on('end', () => {
                console.log(`\n[${label}] STATUS: ${res.statusCode}`);
                try {
                    console.log('BODY:', JSON.stringify(JSON.parse(data), null, 2).substring(0, 300));
                } catch { console.log('BODY:', data.substring(0, 300)); }
                resolve();
            });
        });
        req.on('error', (e) => { console.error(`[${label}] ERROR:`, e.message); resolve(); });
        if (bodyStr) req.write(bodyStr);
        req.end();
    });
}

async function main() {
    // Test GET /notifications
    await testRequest('GET', '/notifications?userId=26', null, 'GET /notifications');

    // Test POST /appointments
    await testRequest('POST', '/appointments', {
        appointment_datetime: '2026-07-15 09:00:00',
        doctor_id: 2,
        note: 'Test booking',
        patient_id: 26,
        status: 'PENDING'
    }, 'POST /appointments');

    // Wait a moment for the DB to update
    await new Promise(r => setTimeout(r, 500));

    // Test GET /notifications again to see new notification
    await testRequest('GET', '/notifications?userId=26', null, 'GET /notifications (after booking)');

    // Test POST /appointments/cancel
    await testRequest('POST', '/appointments/cancel', { appointmentId: 41 }, 'POST /appointments/cancel');

    // Test notifications after cancel
    await testRequest('GET', '/notifications?userId=26', null, 'GET /notifications (after cancel)');

    // Test POST /notifications/read-all
    await testRequest('POST', '/notifications/read-all', { userId: 26 }, 'POST /notifications/read-all');

    console.log('\n✅ All tests done!');
}

main();
