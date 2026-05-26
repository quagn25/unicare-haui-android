const request = require('http'); // wait, it's https
const https = require('https');

const data = JSON.stringify({
  username: 'admin',
  password: '123'
});

const options = {
  hostname: 'jailbird-twenty-recovery.ngrok-free.dev',
  port: 443,
  path: '/login',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length,
    'ngrok-skip-browser-warning': 'true'
  }
};

const req = https.request(options, res => {
  console.log(`statusCode: ${res.statusCode}`);

  res.on('data', d => {
    process.stdout.write(d);
  });
});

req.on('error', error => {
  console.error(error);
});

req.write(data);
req.end();
