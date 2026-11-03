# Class-by-Class Easy Explanation Guide

**Purpose:** This guide is for the individual coding evaluation. It explains what each Java class does in simple English and gives a short Sinhala explanation.

## How to explain the project
1. **Model** classes store data.
2. **DAO** classes communicate with MySQL using JDBC.
3. **Controller** classes connect the JavaFX UI with the model/DAO classes.
4. **Utility** classes provide reusable helper methods.
5. **User** is the parent class; Admin, Lecturer, TechnicalOfficer and Student inherit from it.

## Main OOP concepts
- **Encapsulation:** model fields are private and accessed through getters/setters.
- **Inheritance:** Student, Lecturer, Admin and TechnicalOfficer extend User.
- **Abstraction:** User is an abstract class and defines common user behaviour.
- **Polymorphism:** child classes override `getRoleDisplayName()` and can be handled through the User reference.
- **Exception handling:** database operations use try/catch for SQL errors.
- **Database handling:** DAO classes use JDBC, PreparedStatement and ResultSet.
- **GUI:** JavaFX controllers and FXML files create the desktop interface.

## Model Classes

### `Admin`
- **File:** `com/faculty/management/model/Admin.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getRoleDisplayName()`, `canEditFullProfile()`
- **Evaluation point:** Child class of `User`; demonstrates inheritance and method overriding.
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Attendance`
- **File:** `com/faculty/management/model/Attendance.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getAttendanceId()`, `setAttendanceId()`, `getStudentId()`, `setStudentId()`, `getStudentRegNo()`, `setStudentRegNo()`, `getStudentName()`, `setStudentName()`, `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `AttendanceSummary`
- **File:** `com/faculty/management/model/AttendanceSummary.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `calculate()`, `getStudentId()`, `setStudentId()`, `getRegNo()`, `setRegNo()`, `getStudentName()`, `setStudentName()`, `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()`, `getCourseName()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Course`
- **File:** `com/faculty/management/model/Course.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()`, `getCourseName()`, `setCourseName()`, `getCredit()`, `setCredit()`, `getDepartment()`, `setDepartment()`, `getSemester()`, `setSemester()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `CourseMaterial`
- **File:** `com/faculty/management/model/CourseMaterial.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getMaterialId()`, `setMaterialId()`, `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()`, `getTitle()`, `setTitle()`, `getDescription()`, `setDescription()`, `getFileType()`, `setFileType()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Department`
- **File:** `com/faculty/management/model/Department.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getDepartmentId()`, `setDepartmentId()`, `getDepartmentName()`, `setDepartmentName()`, `toString()`
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `EligibilityStatus`
- **File:** `com/faculty/management/model/EligibilityStatus.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `evaluate()`, `getStudentId()`, `setStudentId()`, `getRegNo()`, `setRegNo()`, `getStudentName()`, `setStudentName()`, `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()`, `getCourseName()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Lecturer`
- **File:** `com/faculty/management/model/Lecturer.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getRoleDisplayName()`, `getDepartment()`, `setDepartment()`, `getDesignation()`, `setDesignation()`
- **Evaluation point:** Child class of `User`; demonstrates inheritance and method overriding.
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Marks`
- **File:** `com/faculty/management/model/Marks.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `isCaEligible()`, `getCaEligibilityStatus()`, `getMarkId()`, `setMarkId()`, `getStudentId()`, `setStudentId()`, `getStudentRegNo()`, `setStudentRegNo()`, `getStudentName()`, `setStudentName()`, `getCourseId()`, `setCourseId()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `MedicalRecord`
- **File:** `com/faculty/management/model/MedicalRecord.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getMedicalId()`, `setMedicalId()`, `getStudentId()`, `setStudentId()`, `getStudentRegNo()`, `setStudentRegNo()`, `getStudentName()`, `setStudentName()`, `getCourseId()`, `setCourseId()`, `getCourseCode()`, `setCourseCode()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Notice`
- **File:** `com/faculty/management/model/Notice.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getNoticeId()`, `setNoticeId()`, `getTitle()`, `setTitle()`, `getContent()`, `setContent()`, `getTargetAudience()`, `setTargetAudience()`, `getPostedBy()`, `setPostedBy()`, `getPostedByName()`, `setPostedByName()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Student`
- **File:** `com/faculty/management/model/Student.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getRoleDisplayName()`, `canEditFullProfile()`, `getRegNo()`, `setRegNo()`, `getDepartment()`, `setDepartment()`, `getBatch()`, `setBatch()`, `getStudentStatus()`, `setStudentStatus()`
- **Evaluation point:** Child class of `User`; demonstrates inheritance and method overriding.
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `TechnicalOfficer`
- **File:** `com/faculty/management/model/TechnicalOfficer.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getRoleDisplayName()`, `getDepartment()`, `setDepartment()`
- **Evaluation point:** Child class of `User`; demonstrates inheritance and method overriding.
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `Timetable`
- **File:** `com/faculty/management/model/Timetable.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getTimetableId()`, `setTimetableId()`, `getDepartment()`, `setDepartment()`, `getSemester()`, `setSemester()`, `getDayOfWeek()`, `setDayOfWeek()`, `getStartTime()`, `setStartTime()`, `getEndTime()`, `setEndTime()` ...
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

### `User`
- **File:** `com/faculty/management/model/User.java`
- **Purpose:** Data/model class. Stores information using private fields, constructors, getters and setters.
- **Important public methods:** `getRoleDisplayName()`, `canEditFullProfile()`, `getUserId()`, `setUserId()`, `getUsername()`, `setUsername()`, `getPasswordHash()`, `setPasswordHash()`, `getFullName()`, `setFullName()`, `getEmail()`, `setEmail()` ...
- **Evaluation point:** Abstract parent class. It demonstrates abstraction and encapsulation.
- **Simple Sinhala:** මේ class එකේ අදාල data store කරනවා.

## Dao Classes

### `AttendanceDAO`
- **File:** `com/faculty/management/dao/AttendanceDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `markAttendance()`, `getStudentAttendanceSummary()`, `getBatchAttendanceSummary()`, `getStudentAllCourseSummaries()`, `getAttendanceRecords()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `CourseDAO`
- **File:** `com/faculty/management/dao/CourseDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllCourses()`, `getCoursesByLecturer()`, `getCoursesByStudent()`, `getAvailableCoursesForStudent()`, `enrollStudent()`, `dropCourse()`, `isStudentEnrolled()`, `addCourse()`, `updateCourse()`, `deleteCourse()`, `countCourses()`, `getMaterialsByCourse()` ...
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `DepartmentDAO`
- **File:** `com/faculty/management/dao/DepartmentDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllDepartments()`, `getDepartmentById()`, `getDepartmentByName()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `LecturerDAO`
- **File:** `com/faculty/management/dao/LecturerDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllLecturers()`, `searchLecturers()`, `addLecturer()`, `updateLecturer()`, `deleteLecturer()`, `countLecturers()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `MarksDAO`
- **File:** `com/faculty/management/dao/MarksDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `saveOrUpdateMarks()`, `getAllMarksByCourse()`, `getMarksByStudent()`, `calculateStudentGpa()`, `getOverallEligibilityByCourse()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `MedicalDAO`
- **File:** `com/faculty/management/dao/MedicalDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllMedicalRecords()`, `getMedicalRecordsByStudent()`, `addMedicalRecord()`, `updateMedicalRecord()`, `updateApprovalStatus()`, `deleteMedicalRecord()`, `countPendingMedicals()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `NoticeDAO`
- **File:** `com/faculty/management/dao/NoticeDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllNotices()`, `getNoticesForAudience()`, `addNotice()`, `updateNotice()`, `deleteNotice()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `StudentDAO`
- **File:** `com/faculty/management/dao/StudentDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllStudents()`, `searchStudents()`, `addStudent()`, `updateStudent()`, `deleteStudent()`, `countStudents()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `TechnicalOfficerDAO`
- **File:** `com/faculty/management/dao/TechnicalOfficerDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllOfficers()`, `searchOfficers()`, `addOfficer()`, `updateOfficer()`, `deleteOfficer()`, `countOfficers()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `TimetableDAO`
- **File:** `com/faculty/management/dao/TimetableDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `getAllTimetables()`, `getTimetablesByDepartment()`, `getTimetablesByLecturer()`, `getTimetablesForStudent()`, `addTimetableEntry()`, `updateTimetableEntry()`, `deleteTimetableEntry()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

### `UserDAO`
- **File:** `com/faculty/management/dao/UserDAO.java`
- **Purpose:** Database access class. Sends SQL queries through JDBC and converts database rows into Java objects.
- **Important public methods:** `authenticate()`, `updateContactAndProfilePicture()`, `updateProfileImage()`, `updateUser()`, `deleteUser()`, `countUsersByRole()`
- **Evaluation point:** Demonstrates JDBC/database handling and SQL exception handling.
- **Simple Sinhala:** මේ class එක database එකට connect වෙලා data add/view/update/delete කරනවා.

## Controller Classes

### `AdminDashboardController`
- **File:** `com/faculty/management/controller/AdminDashboardController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `showDashboardOverview()`, `showStudents()`, `showLecturers()`, `showOfficers()`, `showCourses()`, `showNotices()`, `showTimetable()`, `showProfile()`, `handleLogout()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `AttendanceManagementController`
- **File:** `com/faculty/management/controller/AttendanceManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `CourseManagementController`
- **File:** `com/faculty/management/controller/CourseManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadCourses()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `EligibilityViewController`
- **File:** `com/faculty/management/controller/EligibilityViewController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadEligibility()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `GpaViewController`
- **File:** `com/faculty/management/controller/GpaViewController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadGpaData()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `LecturerDashboardController`
- **File:** `com/faculty/management/controller/LecturerDashboardController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `showDashboardOverview()`, `showCourses()`, `showMarks()`, `showStudents()`, `showEligibility()`, `showAttendance()`, `showMedical()`, `showNotices()`, `showProfile()`, `handleLogout()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `LecturerManagementController`
- **File:** `com/faculty/management/controller/LecturerManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadLecturers()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `LoginController`
- **File:** `com/faculty/management/controller/LoginController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `MarksManagementController`
- **File:** `com/faculty/management/controller/MarksManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadCourseMarks()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `MedicalManagementController`
- **File:** `com/faculty/management/controller/MedicalManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadMedicals()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `NoticeManagementController`
- **File:** `com/faculty/management/controller/NoticeManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadNotices()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `OfficerManagementController`
- **File:** `com/faculty/management/controller/OfficerManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadOfficers()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `ProfileController`
- **File:** `com/faculty/management/controller/ProfileController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `StudentDashboardController`
- **File:** `com/faculty/management/controller/StudentDashboardController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `showDashboardOverview()`, `showProfile()`, `showCourses()`, `showAttendance()`, `showMedical()`, `showMarks()`, `showGpa()`, `showTimetable()`, `showNotices()`, `handleLogout()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `StudentManagementController`
- **File:** `com/faculty/management/controller/StudentManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `setReadOnly()`, `loadStudents()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `TechnicalOfficerDashboardController`
- **File:** `com/faculty/management/controller/TechnicalOfficerDashboardController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `showDashboardOverview()`, `showStudents()`, `showAttendance()`, `showMedical()`, `showTimetable()`, `showNotices()`, `showProfile()`, `handleLogout()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

### `TimetableManagementController`
- **File:** `com/faculty/management/controller/TimetableManagementController.java`
- **Purpose:** JavaFX controller. Handles button clicks, form inputs, table loading and screen actions.
- **Important public methods:** `initialize()`, `loadTimetables()`
- **Evaluation point:** Demonstrates JavaFX event handling and connecting UI actions to application logic.
- **Simple Sinhala:** මේ class එක UI එකේ buttons, forms සහ screen actions control කරනවා.

## Util Classes

### `AlertUtil`
- **File:** `com/faculty/management/util/AlertUtil.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `showInfo()`, `showWarning()`, `showError()`, `showConfirmation()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

### `GradeCalculator`
- **File:** `com/faculty/management/util/GradeCalculator.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `getGrade()`, `getGpv()`, `getDescription()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

### `PasswordUtil`
- **File:** `com/faculty/management/util/PasswordUtil.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `hashPassword()`, `verifyPassword()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

### `SceneManager`
- **File:** `com/faculty/management/util/SceneManager.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `setPrimaryStage()`, `getPrimaryStage()`, `switchScene()`, `loadViewIntoPane()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

### `SessionManager`
- **File:** `com/faculty/management/util/SessionManager.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `setCurrentUser()`, `getCurrentUser()`, `isLoggedIn()`, `logout()`, `hasRole()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

### `ValidationUtil`
- **File:** `com/faculty/management/util/ValidationUtil.java`
- **Purpose:** Utility class. Contains small reusable functions such as validation, navigation, sessions or calculations.
- **Important public methods:** `isNotEmpty()`, `isValidEmail()`, `isValidPhone()`, `isValidMark()`, `isValidCredit()`, `isValidSessionNumber()`
- **Simple Sinhala:** මේක නැවත නැවත භාවිතා කරන්න පුළුවන් helper class එකක්.

## Database Classes

### `DatabaseConnection`
- **File:** `com/faculty/management/database/DatabaseConnection.java`
- **Purpose:** Database connection class. Creates the JDBC connection to MySQL.
- **Important public methods:** `getConnection()`, `testConnection()`, `getUrl()`, `getUser()`
- **Simple Sinhala:** මේක MySQL database connection එක හදන class එක.

## Common Viva Questions

### 1. Why did you use JavaFX?
**Answer:** JavaFX is used to create the graphical user interface of the desktop application.

### 2. What is encapsulation in your project?
**Answer:** Model class fields are private, and getters and setters are used to control access to those fields.

### 3. Where is inheritance used?
**Answer:** `Student`, `Lecturer`, `Admin` and `TechnicalOfficer` inherit common properties and methods from the `User` class.

### 4. Why is User abstract?
**Answer:** User contains common user information and behaviour. We do not need to create a generic User object, so it is used as an abstract parent class.

### 5. Where is polymorphism used?
**Answer:** A `User` reference can refer to an Admin, Lecturer, Technical Officer or Student object, and overridden methods provide role-specific behaviour.

### 6. Why do you use PreparedStatement?
**Answer:** It allows values to be safely inserted into SQL queries and makes parameterized database queries easier.

### 7. What is a DAO?
**Answer:** DAO means Data Access Object. It keeps database operations separate from the JavaFX UI code.

### 8. Why use try-catch?
**Answer:** Database operations can produce SQL exceptions, so try-catch is used to handle errors without crashing the application.

### 9. What is the purpose of a Controller?
**Answer:** A controller handles user actions from the JavaFX interface and calls the appropriate application or database methods.

### 10. How is attendance eligibility calculated?
**Answer:** The project calculates attendance percentage and checks it against the 80% requirement. Approved medical information is also considered in the attendance summary.

### 11. How is CA eligibility checked?
**Answer:** The CA mark is checked against the required 40% threshold.

### 12. Where is grade calculation handled?
**Answer:** The `GradeCalculator` utility class contains the grade and GPV calculation methods.

## Important advice for the coding evaluation
- Do not try to memorize every line.
- Understand the purpose of each class first.
- Be able to explain one complete flow: Login → Controller → DAO → MySQL → result shown in JavaFX.
- Be able to explain one OOP example from your own code.
- Be able to explain one SQL query and why `PreparedStatement` is used.
- Be honest if a part was implemented by another group member; explain your own contribution clearly.