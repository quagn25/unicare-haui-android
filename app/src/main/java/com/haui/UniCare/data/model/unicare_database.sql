CREATE DATABASE IF NOT EXISTS unicare;
USE unicare;

-- 1. USERS (AUTH)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. SPECIALTIES
CREATE TABLE specialties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. DOCTORS
CREATE TABLE doctors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    specialty_id INT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    education TEXT,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (specialty_id) REFERENCES specialties(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. PATIENTS
CREATE TABLE patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender ENUM('MALE','FEMALE','OTHER') NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address TEXT NOT NULL,
    medical_history TEXT,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 5. APPOINTMENTS (FIXED)
CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,

    appointment_datetime DATETIME NOT NULL,

    status ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED')
        DEFAULT 'PENDING',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE CASCADE,

    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
        ON DELETE CASCADE,

    -- chống trùng lịch bác sĩ
    UNIQUE (doctor_id, appointment_datetime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. MEDICAL RECORDS
CREATE TABLE medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_id INT UNIQUE,

    visit_date DATE NOT NULL,
    diagnosis TEXT NOT NULL,
    prescription TEXT,
    doctor_notes TEXT,

    FOREIGN KEY (patient_id) REFERENCES patients(id)
        ON DELETE CASCADE,

    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
        ON DELETE CASCADE,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. TREATMENT PLANS
CREATE TABLE treatment_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,

    medicine_name VARCHAR(100) NOT NULL,
    method VARCHAR(100) NOT NULL,
    times_per_day INT NOT NULL,

    purpose TEXT,
    guide TEXT,

    FOREIGN KEY (record_id) REFERENCES medical_records(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. NOTIFICATIONS
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,

    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    is_read BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. INDEXES (OPTIMIZATION)
CREATE INDEX idx_appointments_patient ON appointments(patient_id);
CREATE INDEX idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX idx_appointments_datetime ON appointments(appointment_datetime);

CREATE INDEX idx_records_patient ON medical_records(patient_id);
CREATE INDEX idx_records_doctor ON medical_records(doctor_id);

CREATE INDEX idx_doctors_user ON doctors(user_id);
CREATE INDEX idx_patients_user ON patients(user_id);