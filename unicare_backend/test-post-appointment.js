const http = require('http');

const data = JSON.stringify({
  appointment_datetime: '2026-07-10 08:30:00',
  consultation_fee: 0,
  doctor_id: 1,
  id: 0,
  note: 'Test API booking',
  patient_id: 26,
  status: 'PENDING'
});

const options = {
  hostname: 'localhost',
  port: 3000,
  path: '/appointments',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
};

const req = http.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
  res.setEncoding('utf8');
  res.on('data', (chunk) => {
    console.log(`BODY: ${chunk}`);
  });
});

req.on('error', (e) => {
  console.error(`problem with request: ${e.message}`);
});

req.write(data);
req.end();
