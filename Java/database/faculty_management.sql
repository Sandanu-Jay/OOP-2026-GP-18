-- ============================================================================
-- UNIVERSITY FACULTY MANAGEMENT SYSTEM - DATABASE INITIALIZATION SCRIPT
-- Compliant with UGC Commission Circular No. 12-2024 Grading Scheme
-- Supports: Admin, Lecturer, Technical Officer, Undergraduate (Student)
-- ============================================================================

DROP DATABASE IF EXISTS faculty_management_db;
CREATE DATABASE faculty_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE faculty_management_db;

-- ----------------------------------------------------------------------------
-- 0. DEPARTMENTS TABLE (4 Faculty Departments per ER Diagram)
-- ----------------------------------------------------------------------------
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

INSERT INTO departments (department_id, department_name) VALUES
(1, 'Engineering Technology'),
(2, 'Information and communication Technology'),
(3, 'Bio System Technology'),
(4, 'Multidisciplinary Studies');

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE (Base entity for authentication and inheritance)
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255) DEFAULT '',
    role ENUM('ADMIN', 'LECTURER', 'TECHNICAL_OFFICER', 'STUDENT') NOT NULL,
    profile_image VARCHAR(255) DEFAULT 'default_avatar.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. STUDENTS TABLE (Extends User)
-- ----------------------------------------------------------------------------
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    reg_no VARCHAR(30) NOT NULL UNIQUE,
    department_id INT,
    department VARCHAR(100) NOT NULL,
    batch VARCHAR(20) NOT NULL,
    student_status ENUM('NORMAL', 'REPEAT', 'BATCH_MISSED') DEFAULT 'NORMAL',
    FOREIGN KEY (student_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. LECTURERS TABLE (Extends User)
-- ----------------------------------------------------------------------------
CREATE TABLE lecturers (
    lecturer_id INT PRIMARY KEY,
    employee_no VARCHAR(50),
    department_id INT,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    FOREIGN KEY (lecturer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. TECHNICAL OFFICERS TABLE (Extends User)
-- ----------------------------------------------------------------------------
CREATE TABLE technical_officers (
    officer_id INT PRIMARY KEY,
    employee_no VARCHAR(50),
    department_id INT,
    department VARCHAR(100) NOT NULL,
    FOREIGN KEY (officer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. COURSES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    credit INT NOT NULL DEFAULT 3,
    department_id INT,
    department VARCHAR(100) NOT NULL,
    semester INT NOT NULL DEFAULT 1,
    lecturer_id INT,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. COURSE MATERIALS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE course_materials (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    file_type VARCHAR(50) DEFAULT 'PDF',
    file_path_or_url VARCHAR(255),
    uploaded_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. ENROLLMENTS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL DEFAULT '2023/2024',
    status ENUM('ENROLLED', 'REPEAT') DEFAULT 'ENROLLED',
    UNIQUE KEY unique_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. ATTENDANCE TABLE (Tracks 15 Theory & 15 Practical sessions)
-- ----------------------------------------------------------------------------
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
    session_number INT NOT NULL,
    session_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT') NOT NULL DEFAULT 'PRESENT',
    UNIQUE KEY unique_session_att (student_id, course_id, session_type, session_number),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 9. MEDICAL RECORDS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE medical_records (
    medical_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    session_type ENUM('THEORY', 'PRACTICAL') NOT NULL,
    session_number INT NOT NULL,
    medical_date DATE NOT NULL,
    reason TEXT,
    document_info VARCHAR(255) NOT NULL,
    is_approved ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comments TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 10. MARKS TABLE (UGC Circular 12-2024 Scheme)
-- ----------------------------------------------------------------------------
CREATE TABLE marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    quiz_mark DECIMAL(5,2) DEFAULT 0.00,
    mid_mark DECIMAL(5,2) DEFAULT 0.00,
    assignment_mark DECIMAL(5,2) DEFAULT 0.00,
    ca_mark DECIMAL(5,2) DEFAULT 0.00,
    final_exam_mark DECIMAL(5,2) DEFAULT 0.00,
    total_mark DECIMAL(5,2) DEFAULT 0.00,
    grade VARCHAR(5) DEFAULT 'E',
    gpv DECIMAL(3,2) DEFAULT 0.00,
    UNIQUE KEY unique_student_course_mark (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 11. NOTICES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE notices (
    notice_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_audience ENUM('ALL', 'STUDENTS', 'LECTURERS', 'OFFICERS') DEFAULT 'ALL',
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_pinned BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (posted_by) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 12. TIMETABLES TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE timetables (
    timetable_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id INT,
    department VARCHAR(100) NOT NULL,
    semester INT NOT NULL DEFAULT 1,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    course_id INT NOT NULL,
    venue VARCHAR(50) NOT NULL,
    lecturer_id INT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- SAMPLE DATA INSERTIONS
-- Password Hash Reference (SHA-256):
-- admin123   -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- lec123     -> e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b
-- to123      -> ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542
-- student123 -> 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b
-- ============================================================================

-- 1. Insert 1 Admin
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, address, role) VALUES
(1, 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Faculty Administrator', 'admin@faculty.ac.lk', '+94 11 234 5678', 'Faculty Admin Complex, University Campus', 'ADMIN');

-- 2. Insert 5 Lecturers
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, address, role) VALUES
(2, 'lec_kamal', 'e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b', 'Dr. Kamal Silva', 'kamal.silva@faculty.ac.lk', '+94 77 123 4567', 'No. 12, Galle Road, Colombo 03', 'LECTURER'),
(3, 'lec_nimal', 'e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b', 'Prof. Nimal Perera', 'nimal.perera@faculty.ac.lk', '+94 77 234 5678', 'No. 45, Kandy Road, Peradeniya', 'LECTURER'),
(4, 'lec_sunil', 'e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b', 'Dr. Sunil Fernando', 'sunil.fernando@faculty.ac.lk', '+94 77 345 6789', 'No. 88, Negombo Road, Kurunegala', 'LECTURER'),
(5, 'lec_priya', 'e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b', 'Dr. Priyantha Dias', 'priyantha.dias@faculty.ac.lk', '+94 77 456 7890', 'No. 19, Temple Road, Matara', 'LECTURER'),
(6, 'lec_anura', 'e09a46489b39fc28b3813b1101afc8300b59322474104431216f91cf774c306b', 'Dr. Anura Jayawardena', 'anura.j@faculty.ac.lk', '+94 77 567 8901', 'No. 31, Station Road, Gampaha', 'LECTURER');

INSERT INTO lecturers (lecturer_id, employee_no, department_id, department, designation) VALUES
(2, 'EMP/LEC/001', 2, 'Information and communication Technology', 'Senior Lecturer Gr. I'),
(3, 'EMP/LEC/002', 2, 'Information and communication Technology', 'Professor / Head of Department'),
(4, 'EMP/LEC/003', 1, 'Engineering Technology', 'Senior Lecturer Gr. II'),
(5, 'EMP/LEC/004', 3, 'Bio System Technology', 'Lecturer (Probationary)'),
(6, 'EMP/LEC/005', 4, 'Multidisciplinary Studies', 'Senior Lecturer Gr. I');

-- 3. Insert 4 Technical Officers
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, address, role) VALUES
(7, 'to_saman', 'ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542', 'Mr. Saman Wickrama', 'saman.to@faculty.ac.lk', '+94 71 111 2233', 'Faculty Technical Wing A', 'TECHNICAL_OFFICER'),
(8, 'to_ruwan', 'ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542', 'Mr. Ruwan Bandara', 'ruwan.to@faculty.ac.lk', '+94 71 222 3344', 'Faculty Technical Wing B', 'TECHNICAL_OFFICER'),
(9, 'to_chathura', 'ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542', 'Mr. Chathura Kumara', 'chathura.to@faculty.ac.lk', '+94 71 333 4455', 'Engineering Workshop Lab 01', 'TECHNICAL_OFFICER'),
(10, 'to_dilani', 'ab27b9a100edf52a1e939157af0180c4e0d5c936567a6269a0687db670af6542', 'Ms. Dilani Weerasinghe', 'dilani.to@faculty.ac.lk', '+94 71 444 5566', 'Bio System Lab Facility', 'TECHNICAL_OFFICER');

INSERT INTO technical_officers (officer_id, employee_no, department_id, department) VALUES
(7, 'EMP/TO/001', 2, 'Information and communication Technology'),
(8, 'EMP/TO/002', 1, 'Engineering Technology'),
(9, 'EMP/TO/003', 3, 'Bio System Technology'),
(10, 'EMP/TO/004', 4, 'Multidisciplinary Studies');

-- 4. Insert 20 Undergraduates (16 Normal, 2 Repeat, 2 Batch-Missed)
INSERT INTO users (user_id, username, password_hash, full_name, email, phone, address, role) VALUES
(11, 'stu_chamindu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Chamindu Dilhara', 'chamindu@student.ac.lk', '+94 70 123 4001', 'No 15, Lake Road, Colombo', 'STUDENT'),
(12, 'stu_kavindi', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Kavindi Tharushika', 'kavindi@student.ac.lk', '+94 70 123 4002', 'No 24, Hill Street, Kandy', 'STUDENT'),
(13, 'stu_kasun', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Kasun Madushanka', 'kasun@student.ac.lk', '+94 70 123 4003', 'No 08, Beach Road, Galle', 'STUDENT'),
(14, 'stu_nethmi', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Nethmi Hansika', 'nethmi@student.ac.lk', '+94 70 123 4004', 'No 33, School Lane, Kurunegala', 'STUDENT'),
(15, 'stu_sandun', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Sandun Prasanna', 'sandun@student.ac.lk', '+94 70 123 4005', 'No 45, Temple Road, Anuradhapura', 'STUDENT'),
(16, 'stu_ishani', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Ishani Sewwandi', 'ishani@student.ac.lk', '+94 70 123 4006', 'No 17, Main Street, Ratnapura', 'STUDENT'),
(17, 'stu_dinuka', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Dinuka Nuwan', 'dinuka@student.ac.lk', '+94 70 123 4007', 'No 60, Circular Road, Badulla', 'STUDENT'),
(18, 'stu_tharushi', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Tharushi Nimanthi', 'tharushi@student.ac.lk', '+94 70 123 4008', 'No 21, New Town, Polonnaruwa', 'STUDENT'),
(19, 'stu_hasitha', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Hasitha Dhananjaya', 'hasitha@student.ac.lk', '+94 70 123 4009', 'No 99, Green Park, Matale', 'STUDENT'),
(20, 'stu_malsha', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Malsha Oshadi', 'malsha@student.ac.lk', '+94 70 123 4010', 'No 14, River View, Kalutara', 'STUDENT'),
(21, 'stu_praveen', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Praveen Chathuranga', 'praveen@student.ac.lk', '+94 70 123 4011', 'No 03, Church Street, Negombo', 'STUDENT'),
(22, 'stu_achalya', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Achalya Fernando', 'achalya@student.ac.lk', '+94 70 123 4012', 'No 76, Flower Road, Panadura', 'STUDENT'),
(23, 'stu_dumindu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Dumindu Jayakody', 'dumindu@student.ac.lk', '+94 70 123 4013', 'No 51, Station Road, Kegalle', 'STUDENT'),
(24, 'stu_hiruni', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Hiruni Gamage', 'hiruni@student.ac.lk', '+94 70 123 4014', 'No 18, Lake Crescent, Gampaha', 'STUDENT'),
(25, 'stu_sahan', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Sahan Gunawardena', 'sahan@student.ac.lk', '+94 70 123 4015', 'No 88, Park Avenue, Nuwara Eliya', 'STUDENT'),
(26, 'stu_buddhi', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Buddhika Lakshan', 'buddhi@student.ac.lk', '+94 70 123 4016', 'No 29, Coastal Highway, Hambantota', 'STUDENT'),
(27, 'stu_lahiru', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Lahiru Vimukthi', 'lahiru@student.ac.lk', '+94 70 123 4017', 'No 40, High Level Road, Maharagama', 'STUDENT'),
(28, 'stu_rashmi', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Rashmi Dinelka', 'rashmi@student.ac.lk', '+94 70 123 4018', 'No 11, Temple Avenue, Nugegoda', 'STUDENT'),
(29, 'stu_nadeesha', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Nadeesha Kaluarachchi', 'nadeesha@student.ac.lk', '+94 70 123 4019', 'No 65, Old Moor Street, Colombo 12', 'STUDENT'),
(30, 'stu_janith', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'Janith Rathnayake', 'janith@student.ac.lk', '+94 70 123 4020', 'No 82, Hospital Road, Jaffna', 'STUDENT');

INSERT INTO students (student_id, reg_no, department_id, department, batch, student_status) VALUES
(11, 'TG/2022/1001', 2, 'Information and communication Technology', '2021/2022', 'NORMAL'),
(12, 'TG/2022/1002', 2, 'Information and communication Technology', '2021/2022', 'NORMAL'),
(13, 'TG/2022/1003', 2, 'Information and communication Technology', '2021/2022', 'NORMAL'),
(14, 'TG/2022/1004', 2, 'Information and communication Technology', '2021/2022', 'NORMAL'),
(15, 'TG/2022/1005', 2, 'Information and communication Technology', '2021/2022', 'NORMAL'),
(16, 'TG/2022/1006', 1, 'Engineering Technology', '2021/2022', 'NORMAL'),
(17, 'TG/2022/1007', 1, 'Engineering Technology', '2021/2022', 'NORMAL'),
(18, 'TG/2022/1008', 1, 'Engineering Technology', '2021/2022', 'NORMAL'),
(19, 'TG/2022/1009', 1, 'Engineering Technology', '2021/2022', 'NORMAL'),
(20, 'TG/2022/1010', 1, 'Engineering Technology', '2021/2022', 'NORMAL'),
(21, 'TG/2022/1011', 3, 'Bio System Technology', '2021/2022', 'NORMAL'),
(22, 'TG/2022/1012', 3, 'Bio System Technology', '2021/2022', 'NORMAL'),
(23, 'TG/2022/1013', 3, 'Bio System Technology', '2021/2022', 'NORMAL'),
(24, 'TG/2022/1014', 3, 'Bio System Technology', '2021/2022', 'NORMAL'),
(25, 'TG/2022/1015', 3, 'Bio System Technology', '2021/2022', 'NORMAL'),
(26, 'TG/2022/1016', 4, 'Multidisciplinary Studies', '2021/2022', 'NORMAL'),
(27, 'TG/2021/0805', 2, 'Information and communication Technology', '2020/2021', 'REPEAT'),
(28, 'TG/2021/0812', 2, 'Information and communication Technology', '2020/2021', 'REPEAT'),
(29, 'TG/2020/0550', 2, 'Information and communication Technology', '2019/2020', 'BATCH_MISSED'),
(30, 'TG/2020/0592', 2, 'Information and communication Technology', '2019/2020', 'BATCH_MISSED');

-- ----------------------------------------------------------------------------
-- 5. Insert Courses
-- ----------------------------------------------------------------------------
INSERT INTO courses (course_id, course_code, course_name, credit, department_id, department, semester, lecturer_id) VALUES
(1, 'ICT2101', 'Object Oriented Programming', 3, 2, 'Information and communication Technology', 1, 2),
(2, 'ICT2102', 'Database Management Systems', 3, 2, 'Information and communication Technology', 1, 3),
(3, 'ENG2103', 'Mechanical Systems & Instrumentation', 3, 1, 'Engineering Technology', 1, 4),
(4, 'BST2104', 'Agricultural & Bio-Resource Processing', 2, 3, 'Bio System Technology', 1, 5),
(5, 'MDS2105', 'Professional Ethics & Management', 2, 4, 'Multidisciplinary Studies', 1, 6),
(6, 'ICT2103', 'Data Structures & Algorithms', 3, 2, 'Information and communication Technology', 1, 2),
(7, 'ICT2104', 'Web Application Development', 2, 2, 'Information and communication Technology', 1, 3),
(8, 'ENG2106', 'Digital Electronics & Microcontrollers', 3, 1, 'Engineering Technology', 1, 4);

-- ----------------------------------------------------------------------------
-- 6. Insert Course Materials
-- ----------------------------------------------------------------------------
INSERT INTO course_materials (course_id, title, description, file_type, file_path_or_url) VALUES
(1, 'Lecture 01 - Introduction to OOP Concepts', 'Covers Classes, Objects, and Basic Java Syntax', 'PDF', 'materials/ict2101_lec01.pdf'),
(1, 'Lecture 02 - Encapsulation and Access Modifiers', 'Details on private fields, getters, and setters', 'PDF', 'materials/ict2101_lec02.pdf'),
(1, 'Lecture 03 - Inheritance and Polymorphism', 'Subclasses, super keyword, and dynamic method dispatch', 'PDF', 'materials/ict2101_lec03.pdf'),
(1, 'Lab Sheet 01 - JavaFX GUI Basics', 'Hands-on guide to creating layouts and forms in JavaFX', 'PDF', 'materials/ict2101_lab01.pdf'),
(2, 'Lecture 01 - Relational Database Modeling', 'ER diagrams, Normalization up to 3NF, and SQL', 'PDF', 'materials/ict2102_lec01.pdf');

-- ----------------------------------------------------------------------------
-- 7. Insert Enrollments
-- ----------------------------------------------------------------------------
INSERT INTO enrollments (student_id, course_id, academic_year, status) VALUES
(11, 1, '2023/2024', 'ENROLLED'), (11, 2, '2023/2024', 'ENROLLED'),
(12, 1, '2023/2024', 'ENROLLED'), (12, 2, '2023/2024', 'ENROLLED'),
(13, 1, '2023/2024', 'ENROLLED'), (13, 2, '2023/2024', 'ENROLLED'),
(14, 1, '2023/2024', 'ENROLLED'), (14, 2, '2023/2024', 'ENROLLED'),
(15, 1, '2023/2024', 'ENROLLED'), (15, 2, '2023/2024', 'ENROLLED'),
(16, 1, '2023/2024', 'ENROLLED'), (17, 1, '2023/2024', 'ENROLLED'),
(18, 1, '2023/2024', 'ENROLLED'), (19, 1, '2023/2024', 'ENROLLED'),
(20, 1, '2023/2024', 'ENROLLED'), (21, 1, '2023/2024', 'ENROLLED'),
(22, 1, '2023/2024', 'ENROLLED'), (23, 1, '2023/2024', 'ENROLLED'),
(24, 1, '2023/2024', 'ENROLLED'), (25, 1, '2023/2024', 'ENROLLED'),
(26, 1, '2023/2024', 'ENROLLED'),
(27, 1, '2023/2024', 'REPEAT'),   (28, 1, '2023/2024', 'REPEAT'),
(29, 1, '2023/2024', 'ENROLLED'), (30, 1, '2023/2024', 'ENROLLED');

-- ----------------------------------------------------------------------------
-- 8. Insert Attendance Records (15 Theory & 15 Practical = 30 Sessions per course)
-- Representing the 5 mandatory conditions for ICT2101:
-- ----------------------------------------------------------------------------
-- CASE 1: More than 80% attendance (>80%)
-- Student 11 (Chamindu): Theory 14/15, Practical 14/15 -> 28/30 = 93.3%
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 11, 1, 'THEORY', n, DATE_ADD('2024-02-05', INTERVAL (n-1)*7 DAY), IF(n=15, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 11, 1, 'PRACTICAL', n, DATE_ADD('2024-02-08', INTERVAL (n-1)*7 DAY), IF(n=15, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

-- CASE 2: Exactly 80% attendance (24/30 = 80.0%)
-- Student 12 (Kavindi): Theory 12/15, Practical 12/15 -> 24/30 = 80.0%
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 12, 1, 'THEORY', n, DATE_ADD('2024-02-05', INTERVAL (n-1)*7 DAY), IF(n > 12, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 12, 1, 'PRACTICAL', n, DATE_ADD('2024-02-08', INTERVAL (n-1)*7 DAY), IF(n > 12, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

-- CASE 3: Less than 80% attendance without medicals
-- Student 13 (Kasun): Theory 10/15, Practical 10/15 -> 20/30 = 66.7% (No medicals, NOT ELIGIBLE)
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 13, 1, 'THEORY', n, DATE_ADD('2024-02-05', INTERVAL (n-1)*7 DAY), IF(n > 10, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 13, 1, 'PRACTICAL', n, DATE_ADD('2024-02-08', INTERVAL (n-1)*7 DAY), IF(n > 10, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

-- CASE 4: More than 80% attendance WITH medicals
-- Student 14 (Nethmi): Attended Theory 11/15, Practical 11/15 -> Raw 22/30 (73.3%).
-- Approved medical for Theory 12, 13, 14 (3 sessions) -> 22 + 3 = 25/30 = 83.3% (>80% with medicals, ELIGIBLE)
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 14, 1, 'THEORY', n, DATE_ADD('2024-02-05', INTERVAL (n-1)*7 DAY), IF(n > 11, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 14, 1, 'PRACTICAL', n, DATE_ADD('2024-02-08', INTERVAL (n-1)*7 DAY), IF(n > 11, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

-- CASE 5: Less than 80% attendance WITH medicals
-- Student 15 (Sandun): Attended Theory 9/15, Practical 9/15 -> Raw 18/30 (60.0%).
-- Approved medical for Theory 10, 11 (2 sessions) -> 18 + 2 = 20/30 = 66.7% (<80% with medicals, NOT ELIGIBLE)
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 15, 1, 'THEORY', n, DATE_ADD('2024-02-05', INTERVAL (n-1)*7 DAY), IF(n > 9, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT 15, 1, 'PRACTICAL', n, DATE_ADD('2024-02-08', INTERVAL (n-1)*7 DAY), IF(n > 9, 'ABSENT', 'PRESENT')
FROM (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) numbers;

-- Populate basic attendance for remaining students 16..30 (90% attendance)
INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT s.student_id, 1, 'THEORY', n.n, DATE_ADD('2024-02-05', INTERVAL (n.n-1)*7 DAY), IF(n.n = 15, 'ABSENT', 'PRESENT')
FROM (SELECT student_id FROM students WHERE student_id >= 16) s
CROSS JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) n;

INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status)
SELECT s.student_id, 1, 'PRACTICAL', n.n, DATE_ADD('2024-02-08', INTERVAL (n.n-1)*7 DAY), IF(n.n = 15, 'ABSENT', 'PRESENT')
FROM (SELECT student_id FROM students WHERE student_id >= 16) s
CROSS JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15) n;

-- ----------------------------------------------------------------------------
-- 9. Insert Medical Records (with Reason per ER diagram)
-- ----------------------------------------------------------------------------
-- Case 4: Approved medicals for Student 14 (3 sessions)
INSERT INTO medical_records (student_id, course_id, session_type, session_number, medical_date, reason, document_info, is_approved, comments) VALUES
(14, 1, 'THEORY', 12, '2024-04-22', 'Acute Bronchitis and high fever', 'Government Hospital Certificate #MC-8812', 'APPROVED', 'Approved by Senior Medical Officer'),
(14, 1, 'THEORY', 13, '2024-04-29', 'Severe flu infection requiring bed rest', 'Government Hospital Certificate #MC-8812', 'APPROVED', 'Approved by Senior Medical Officer'),
(14, 1, 'THEORY', 14, '2024-05-06', 'Post-viral recovery period', 'Government Hospital Certificate #MC-8812', 'APPROVED', 'Approved by Senior Medical Officer');

-- Case 5: Approved medicals for Student 15 (2 sessions)
INSERT INTO medical_records (student_id, course_id, session_type, session_number, medical_date, reason, document_info, is_approved, comments) VALUES
(15, 1, 'THEORY', 10, '2024-04-08', 'Sprained ankle sustained during inter-faculty tournament', 'University Medical Center Slip #UMC-404', 'APPROVED', 'Verified by TO'),
(15, 1, 'THEORY', 11, '2024-04-15', 'Physiotherapy check-up and rest', 'University Medical Center Slip #UMC-404', 'APPROVED', 'Verified by TO');

-- Pending & Rejected Medical examples for realism
INSERT INTO medical_records (student_id, course_id, session_type, session_number, medical_date, reason, document_info, is_approved, comments) VALUES
(13, 1, 'THEORY', 14, '2024-05-06', 'High fever and migraine', 'Private Clinic Prescription #PC-102', 'PENDING', 'Under verification with physician'),
(13, 1, 'PRACTICAL', 14, '2024-05-09', 'Personal illness', 'Handwritten letter', 'REJECTED', 'Unverified medical document submitted late');

-- ----------------------------------------------------------------------------
-- 10. Insert Marks & Grades (Calculated with UGC Circular No. 12/2024)
-- Formula: CA = quiz*0.25 + mid*0.50 + assignment*0.25; Total = CA*0.40 + Final*0.60
-- CA Eligibility threshold: CA >= 40.0%
-- ----------------------------------------------------------------------------
INSERT INTO marks (student_id, course_id, quiz_mark, mid_mark, assignment_mark, ca_mark, final_exam_mark, total_mark, grade, gpv) VALUES
-- Student 11: Chamindu (High Distinction: A+)
(11, 1, 90.00, 88.00, 92.00, 89.50, 90.00, 89.80, 'A+', 4.00),
(11, 2, 85.00, 84.00, 88.00, 85.25, 87.00, 86.30, 'A+', 4.00),

-- Student 12: Kavindi (Excellent: A)
(12, 1, 80.00, 78.00, 82.00, 79.50, 80.00, 79.80, 'A', 4.00),
(12, 2, 75.00, 76.00, 78.00, 76.25, 75.00, 75.50, 'A', 4.00),

-- Student 13: Kasun (CA Eligible, Attendance NOT eligible)
(13, 1, 60.00, 65.00, 62.00, 63.00, 65.00, 64.20, 'B', 3.00),

-- Student 14: Nethmi (Good: B+)
(14, 1, 70.00, 68.00, 72.00, 69.50, 68.00, 68.60, 'B+', 3.30),

-- Student 15: Sandun (CA < 40%, CA NOT ELIGIBLE!)
(15, 1, 30.00, 35.00, 32.00, 33.00, 50.00, 43.20, 'C-', 1.70),

-- Student 16: Ishani (Pass: C+)
(16, 1, 55.00, 52.00, 54.00, 53.25, 52.00, 52.50, 'C+', 2.30),

-- Student 17: Dinuka (Pass: C)
(17, 1, 48.00, 46.00, 50.00, 47.50, 48.00, 47.80, 'C', 2.00),

-- Student 18: Tharushi (Weak: D+)
(18, 1, 38.00, 36.00, 40.00, 37.50, 38.00, 37.80, 'D+', 1.30),

-- Student 19: Hasitha (Fail: E)
(19, 1, 20.00, 25.00, 22.00, 23.00, 25.00, 24.20, 'E', 0.00),

-- Student 27: Repeat Student Lahiru
(27, 1, 50.00, 52.00, 48.00, 50.50, 48.00, 49.00, 'C', 2.00);

-- ----------------------------------------------------------------------------
-- 11. Insert Notices
-- ----------------------------------------------------------------------------
INSERT INTO notices (notice_id, title, content, target_audience, posted_by, is_pinned) VALUES
(1, 'End-Semester Examination Time Table Published', 'The official examination timetable for Semester 1 of Academic Year 2023/2024 is now available on the portal. Please verify your course codes and reporting times.', 'ALL', 1, TRUE),
(2, 'Submission of Medical Certificates for Absent Sessions', 'All undergraduates who missed theory or practical classes must submit approved medical forms to Technical Officers within 14 days of absence to be counted towards 80% eligibility.', 'STUDENTS', 1, TRUE),
(3, 'Continuous Assessment (CA) Marks Submission Deadline', 'All academic staff members are kindly requested to upload and finalize Continuous Assessment (CA) marks by next Friday. CA eligibility threshold is strictly 40%.', 'LECTURERS', 1, FALSE),
(4, 'Laboratory Maintenance Schedule', 'Technical Officers are requested to check and calibrate lab terminals in Computer Labs 1, 2, and 3 ahead of the upcoming practical examinations.', 'OFFICERS', 1, FALSE);

-- ----------------------------------------------------------------------------
-- 12. Insert Timetables
-- ----------------------------------------------------------------------------
INSERT INTO timetables (department_id, department, semester, day_of_week, start_time, end_time, course_id, venue, lecturer_id) VALUES
(2, 'Information and communication Technology', 1, 'Monday', '08:30:00', '11:30:00', 1, 'Lecture Theatre 01', 2),
(2, 'Information and communication Technology', 1, 'Tuesday', '10:30:00', '12:30:00', 2, 'Lecture Theatre 02', 3),
(1, 'Engineering Technology', 1, 'Wednesday', '13:30:00', '16:30:00', 3, 'Engineering Lab 01', 4),
(3, 'Bio System Technology', 1, 'Thursday', '09:30:00', '12:30:00', 4, 'Bio Lab 01', 5),
(4, 'Multidisciplinary Studies', 1, 'Friday', '08:30:00', '11:30:00', 5, 'Main Auditorium', 6);
