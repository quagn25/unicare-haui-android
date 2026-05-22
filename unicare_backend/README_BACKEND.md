# Hướng dẫn chạy Backend UniCare

Dự án này sử dụng NodeJS và Express.

## 1. Yêu cầu hệ thống
- Đã cài đặt [Node.js](https://nodejs.org/) (phiên bản 14 trở lên).
- Đã cài đặt MySQL.

## 2. Các thư viện sử dụng (Requirements)
Dự án sử dụng các thư viện chính sau (được quản lý trong `package.json`):
- `express`: Framework tạo server.
- `mysql2`: Kết nối cơ sở dữ liệu MySQL.
- `bcryptjs`: Mã hóa (băm) mật khẩu người dùng.
- `nodemailer`: Gửi email OTP.
- `cors`: Cho phép Android App kết nối với Server.
- `dotenv`: Quản lý biến môi trường bảo mật.

## 3. Cách cài đặt và chạy
1. Tải code về máy.
2. Mở Terminal tại thư mục này.
3. Chạy lệnh cài đặt tất cả thư viện:
   ```bash
   npm install
   ```
4. Tạo tệp `.env` dựa trên tệp `.env.example` và điền thông tin của bạn.
5. Chạy server:
   ```bash
   npm start
   ```

## 4. Lưu ý quan trọng
- Không bao giờ chia sẻ tệp `.env` thật của bạn.
- Luôn chạy `npm install` khi bạn tải code từ GitHub về lần đầu tiên.
