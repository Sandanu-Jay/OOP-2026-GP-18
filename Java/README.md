# University Faculty Management System

A desktop-based Faculty Management System developed for an undergraduate Object-Oriented Programming (OOP) mini-project using **JavaFX**, **MySQL**, and **JDBC**.

The application provides an academic management and administration desktop portal with a modern UI style, role-based dashboards, and business logic complying with **UGC Commission Circular No. 12-2024** and university academic standards.

---

## Technologies Used

* **Language:** Java (JDK 21+)
* **GUI Toolkit:** JavaFX 21 (Declarative FXML + Modern JavaFX CSS)
* **Database:** MySQL 8.0+
* **Connectivity:** JDBC (Java Database Connectivity) with `PreparedStatement`
* **Build System:** Apache Maven (with included cross-platform Maven Wrapper `mvnw` / `mvnw.cmd`)
* **Security:** Cryptographic SHA-256 password hashing

---

## Main Features & Roles

### 1. Administrator
* Manage user accounts (Undergraduates, Lecturers, Technical Officers).
* Maintain course catalogs (Course Code, Name, Credits, Department, Assigned Lecturer).
* Publish and pin faculty notices and targeted announcements.
* Schedule and manage lecture hall and lab timetables.
* Real-time metrics overview on system totals.

### 2. Lecturer
* View assigned course modules and manage/upload learning resources (slides, lab sheets, notes).
* Enter marks for Quizzes, Mid-Term Exams, Assignments, and End-Semester Exams.
* Automatic Continuous Assessment (CA) calculation and CA Eligibility evaluation (**CA ≥ 40%**).
* Automatic Final Examination marks calculation and letter grading per **UGC Commission Circular No. 12-2024**.
* View student attendance summaries across 15 Theory + 15 Practical sessions.
* Inspect undergraduate medical submissions.
* View whole-batch Final-Exam eligibility.

### 3. Technical Officer
* Record and maintain student attendance across **15 Theory** and **15 Practical** sessions (30 sessions total).
* Process undergraduate medical certificates for missed sessions (Approve or Reject with remarks).
* View attendance calculations with approved medical credits.
* View department laboratory and lecture timetables.
* Read faculty notices.

### 4. Undergraduate / Student
* View academic profile with restricted editing (Can only update contact phone, email, and avatar; registration number, username, and batch are read-only).
* View enrolled courses and download course materials.
* View personal attendance percentage, approved medicals, and **80% attendance threshold status**.
* View submitted medical certificate records and approval statuses.
* View marks and grades breakdown.
* View calculated Cumulative GPA (CGPA) and Semester GPA (SGPA).
* View personal lecture timetable and university notices.

---

## Grading Scheme (UGC Commission Circular No. 12-2024)

| Percentage Marks | Letter Grade | Grade Point Value (GPV) | Academic Status |
| :---: | :---: | :---: | :--- |
| **85 – 100** | **A+** | **4.00** | Excellent |
| **75 – 84** | **A** | **4.00** | Excellent |
| **70 – 74** | **A-** | **3.70** | Very Good |
| **65 – 69** | **B+** | **3.30** | Good |
| **60 – 64** | **B** | **3.00** | Good |
| **55 – 59** | **B-** | **2.70** | Satisfactory |
| **50 – 54** | **C+** | **2.30** | Pass |
| **45 – 49** | **C** | **2.00** | Pass |
| **40 – 44** | **C-** | **1.70** | Conditional Pass |
| **35 – 39** | **D+** | **1.30** | Weak |
| **30 – 34** | **D** | **1.00** | Weak |
| **0 – 29** | **E** | **0.00** | Fail |

* **CA Eligibility Threshold:** CA Mark must be **≥ 40.0%**.
* **Attendance Eligibility Threshold:** Effective Attendance must be **≥ 80.0%** (15 Theory + 15 Practical = 30 sessions, including approved medical certificates).
* **Overall Final-Exam Eligibility:** Student must satisfy **both** `CA ≥ 40%` AND `Attendance ≥ 80%`.

---

## Pre-Configured Demo Accounts

All demo passwords are pre-hashed using SHA-256 in the database.

| Role | Username | Password | Full Name / Description |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | System Administrator |
| **Lecturer** | `lec_kamal` | `lec123` | Dr. Kamal Silva (Senior Lecturer - Computer Science) |
| **Lecturer** | `lec_nimal` | `lec123` | Prof. Nimal Perera (Head of Department - CS) |
| **Lecturer** | `lec_sunil` | `lec123` | Dr. Sunil Fernando (Senior Lecturer - IT) |
| **Lecturer** | `lec_priya` | `lec123` | Dr. Priyantha Dias (Lecturer - IT) |
| **Lecturer** | `lec_anura` | `lec123` | Dr. Anura Jayawardena (Senior Lecturer - ET) |
| **Technical Officer** | `to_saman` | `to123` | Mr. Saman Wickrama (Computer Science Lab) |
| **Technical Officer** | `to_ruwan` | `to123` | Mr. Ruwan Bandara (Hardware & Network Lab) |
| **Technical Officer** | `to_chathura` | `to123` | Mr. Chathura Kumara (Electronics Lab) |
| **Technical Officer** | `to_dilani` | `to123` | Ms. Dilani Weerasinghe (Software Lab) |
| **Student** | `stu_chamindu` | `student123` | Chamindu Dilhara (TG/2022/1001, >80% Attendance, A+) |
| **Student** | `stu_kavindi` | `student123` | Kavindi Tharushika (TG/2022/1002, Exactly 80% Attendance) |
| **Student** | `stu_kasun` | `student123` | Kasun Madushanka (TG/2022/1003, <80% Attendance without medicals) |
| **Student** | `stu_nethmi` | `student123` | Nethmi Hansika (TG/2022/1004, >80% Attendance with approved medicals) |
| **Student** | `stu_sandun` | `student123` | Sandun Prasanna (TG/2022/1005, <80% Attendance with medicals, CA < 40%) |
| **Student** | `stu_lahiru` | `student123` | Lahiru Vimukthi (TG/2021/0805, Repeat Student) |
| **Student** | `stu_nadeesha` | `student123` | Nadeesha Kaluarachchi (TG/2020/0550, Batch-Missed Student) |

---

## Database Setup (MySQL)

1. Ensure your MySQL server service is running on your machine (e.g. `localhost:3306`).
2. Open MySQL Workbench, MySQL Command Line Client, or phpMyAdmin.
3. Execute the database script located at:
   ```
   database/faculty_management.sql
   ```
   Or via terminal:
   ```bash
   mysql -u root -p < database/faculty_management.sql
   ```
   This creates the database `faculty_management_db`, all required tables, foreign keys, and complete sample data.

### Configuring Database Credentials
Database connection properties are configured in:
```
src/main/resources/db.properties
```
Example:
```properties
db.url=jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_mysql_password
```
*(Leave password blank if your root user does not have a password).*

---

## How to Run the Application

The project uses a standard Maven setup and includes the Maven Wrapper.

### Option 1: Running from IntelliJ IDEA / VS Code / Eclipse (Recommended)
1. Open the project folder in your IDE.
2. Navigate to:
   ```
   src/main/java/com/faculty/management/AppLauncher.java
   ```
3. Click **Run** on [`AppLauncher.java`](file:///c:/Users/chamindu/OneDrive/Desktop/Java/src/main/java/com/faculty/management/AppLauncher.java).
   *(Note: Run `AppLauncher.java` instead of `Main.java` directly to prevent the Java 11+ JavaFX runtime components check).*

### Option 2: Running via Maven Command
In the project root directory:
```bash
# Windows
.\mvnw.cmd javafx:run

# Linux / macOS
./mvnw javafx:run
```

---

## Project Structure

```
├── pom.xml
├── mvnw / mvnw.cmd / .mvn/
├── README.md
├── CODING_EVALUATION_GUIDE.md
├── database/
│   └── faculty_management.sql
└── src/
    ├── main/
    │   ├── java/com/faculty/management/
    │   │   ├── Main.java
    │   │   ├── model/
    │   │   │   ├── User.java (abstract)
    │   │   │   ├── Admin.java
    │   │   │   ├── Lecturer.java
    │   │   │   ├── TechnicalOfficer.java
    │   │   │   ├── Student.java
    │   │   │   ├── Course.java
    │   │   │   ├── CourseMaterial.java
    │   │   │   ├── Attendance.java
    │   │   │   ├── AttendanceSummary.java
    │   │   │   ├── MedicalRecord.java
    │   │   │   ├── Marks.java
    │   │   │   ├── Notice.java
    │   │   │   ├── Timetable.java
    │   │   │   └── EligibilityStatus.java
    │   │   ├── dao/
    │   │   │   ├── UserDAO.java
    │   │   │   ├── StudentDAO.java
    │   │   │   ├── LecturerDAO.java
    │   │   │   ├── TechnicalOfficerDAO.java
    │   │   │   ├── CourseDAO.java
    │   │   │   ├── MarksDAO.java
    │   │   │   ├── AttendanceDAO.java
    │   │   │   ├── MedicalDAO.java
    │   │   │   ├── NoticeDAO.java
    │   │   │   └── TimetableDAO.java
    │   │   ├── controller/
    │   │   │   ├── LoginController.java
    │   │   │   ├── AdminDashboardController.java
    │   │   │   ├── LecturerDashboardController.java
    │   │   │   ├── TechnicalOfficerDashboardController.java
    │   │   │   ├── StudentDashboardController.java
    │   │   │   ├── ProfileController.java
    │   │   │   ├── StudentManagementController.java
    │   │   │   ├── LecturerManagementController.java
    │   │   │   ├── OfficerManagementController.java
    │   │   │   ├── CourseManagementController.java
    │   │   │   ├── MarksManagementController.java
    │   │   │   ├── AttendanceManagementController.java
    │   │   │   ├── MedicalManagementController.java
    │   │   │   ├── NoticeManagementController.java
    │   │   │   ├── TimetableManagementController.java
    │   │   │   ├── EligibilityViewController.java
    │   │   │   └── GpaViewController.java
    │   │   ├── database/
    │   │   │   └── DatabaseConnection.java
    │   │   └── util/
    │   │       ├── PasswordUtil.java
    │   │       ├── ValidationUtil.java
    │   │       ├── SessionManager.java
    │   │       ├── SceneManager.java
    │   │       ├── GradeCalculator.java
    │   │       └── AlertUtil.java
    │   └── resources/
    │       ├── db.properties
    │       ├── css/
    │       │   └── styles.css
    │       └── fxml/
    │           ├── login.fxml
    │           ├── admin_dashboard.fxml
    │           ├── lecturer_dashboard.fxml
    │           ├── technical_officer_dashboard.fxml
    │           ├── student_dashboard.fxml
    │           ├── profile_view.fxml
    │           ├── students_view.fxml
    │           ├── lecturers_view.fxml
    │           ├── officers_view.fxml
    │           ├── courses_view.fxml
    │           ├── marks_view.fxml
    │           ├── attendance_view.fxml
    │           ├── medical_view.fxml
    │           ├── notices_view.fxml
    │           ├── timetable_view.fxml
    │           ├── eligibility_view.fxml
    │           └── gpa_view.fxml
    └── test/
        └── java/com/faculty/management/
            └── SystemVerificationTest.java
```

---

## Troubleshooting

* **"Database unreachable. Ensure MySQL is running on localhost:3306"**
  * Open Services (`services.msc` on Windows) and start the **MYSQL80** or **MySQL** service.
  * Check that `src/main/resources/db.properties` has the correct username and password.
* **"Communications link failure / Access denied for user 'root'"**
  * Check the password inside `src/main/resources/db.properties`.
* **Testing without UI:**
  * Run `.\mvnw.cmd test` to verify all grading calculations, attendance formulas, and OOP hierarchies.
