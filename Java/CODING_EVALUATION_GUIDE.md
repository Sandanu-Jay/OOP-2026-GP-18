# University OOP Mini Project: Coding Evaluation & Viva Guide

This document is your complete preparation guide for your individual coding viva/evaluation.
Every concept, class, code snippet, and question listed here is based directly on the actual project code.

---

## 1. Core Object-Oriented Programming (OOP) Concepts in the Project

### Concept 1: Classes and Objects
* **Which Class Demonstrates It?**
  All model classes, such as `Course.java`, `Student.java`, and `AttendanceSummary.java`.
* **Which Code Demonstrates It?**
  ```java
  // In Course.java: The class acts as a blueprint
  public class Course {
      private int courseId;
      private String courseCode;
      private String courseName;
      // ...
  }

  // In CourseDAO.java: An object is an instance created with 'new'
  Course c = new Course(1, "ICT2101", "Object Oriented Programming", 3, "CS", 1, 2);
  ```
* **Why It Is Used:**
  To model real-world faculty entities (courses, students, timetables, marks) with attributes (state) and methods (behavior).
* **Viva Explanation:**
  > *"A class is a blueprint that defines the properties and behaviors of an entity. An object is a concrete instance of that class created in memory using the `new` keyword. In my project, `Course` is the class blueprint, and when the DAO loads modules from the database, it creates individual `Course` objects for the application to display."*

---

### Concept 2: Encapsulation
* **Which Class Demonstrates It?**
  `User.java`, `Student.java`, `Marks.java`, and `AttendanceSummary.java`.
* **Which Code Demonstrates It?**
  ```java
  // In User.java: All fields are private
  private String email;
  private String phone;

  // Controlled access via getters and setters:
  public String getEmail() {
      return email;
  }

  public void setEmail(String email) {
      this.email = email;
  }
  ```
  And in `Marks.java`:
  ```java
  // Business logic is encapsulated inside the class
  public boolean isCaEligible() {
      return this.caMark >= 40.0;
  }
  ```
* **Why It Is Used:**
  To hide the internal state of objects from unauthorized outside interference and ensure data integrity.
* **Viva Explanation:**
  > *"Encapsulation is data hiding. I declared all class fields as `private` so outside classes cannot alter them directly. Access is controlled through `public` getters and setters, and internal business calculations (like whether a student's CA mark satisfies the 40% threshold) are encapsulated inside the model rather than scattered across the UI."*

---

### Concept 3: Inheritance
* **Which Class Demonstrates It?**
  `User.java` (Superclass / Parent) and `Student.java`, `Lecturer.java`, `TechnicalOfficer.java`, `Admin.java` (Subclasses / Children).
* **Which Code Demonstrates It?**
  ```java
  // Student.java inherits from User.java using the 'extends' keyword
  public class Student extends User {
      private String regNo;
      private String department;
      private String batch;
      private String studentStatus;

      public Student(int userId, String username, String passwordHash, String fullName, 
                     String email, String phone, String profileImage, 
                     String regNo, String department, String batch, String studentStatus) {
          // 'super' invokes the constructor of the parent class User
          super(userId, username, passwordHash, fullName, email, phone, "STUDENT", profileImage);
          this.regNo = regNo;
          this.department = department;
          this.batch = batch;
          this.studentStatus = studentStatus;
      }
  }
  ```
* **Why It Is Used:**
  To promote code reusability. Instead of repeating `userId`, `username`, `email`, and `phone` in Student, Lecturer, and Admin, they are defined once in `User`.
* **Viva Explanation:**
  > *"Inheritance allows a subclass to inherit fields and methods from a superclass using the `extends` keyword. In my system, `Admin`, `Lecturer`, `TechnicalOfficer`, and `Student` all extend the common `User` class. The `super()` keyword is used in the child constructors to initialize the common user fields defined in the parent."*

---

### Concept 4: Abstraction
* **Which Class Demonstrates It?**
  `User.java` (Abstract Class) and `UserDAO.java` / `DatabaseConnection.java`.
* **Which Code Demonstrates It?**
  ```java
  // In User.java:
  public abstract class User {
      // Abstract method: Has no implementation body in User.
      // Every subclass MUST provide its own implementation.
      public abstract String getRoleDisplayName();

      // Common non-abstract methods and state
      public int getUserId() { return userId; }
  }
  ```
* **Why It Is Used:**
  A generic user cannot physically exist on its own in the faculty system; someone must specifically be an Admin, Lecturer, Technical Officer, or Student.
* **Viva Explanation:**
  > *"Abstraction hides implementation complexity and shows only essential features. I declared `User` as an `abstract class` because no user in the university exists as just a generic user. It defines the abstract method `getRoleDisplayName()`, forcing each subclass to define how its role is presented without exposing unnecessary details."*

---

### Concept 5: Polymorphism (Method Overriding & Dynamic Binding)
* **Which Class Demonstrates It?**
  `User.java` and its subclasses (`Student.java`, `Lecturer.java`, `Admin.java`), and `UserDAO.java`.
* **Which Code Demonstrates It?**
  ```java
  // In Admin.java:
  @Override
  public String getRoleDisplayName() {
      return "Administrator";
  }

  // In Student.java:
  @Override
  public String getRoleDisplayName() {
      return "Undergraduate (" + regNo + ")";
  }

  // In UserDAO.java: Polymorphic instantiation and return type
  User user = userDAO.authenticate(username, password);
  // At runtime, user.getRoleDisplayName() invokes the subclass method dynamically!
  ```
* **Why It Is Used:**
  To allow a single interface or base class reference to execute different subclass behaviors dynamically at runtime.
* **Viva Explanation:**
  > *"Polymorphism means 'many forms'. In my project, I used Runtime Polymorphism (Method Overriding) with the `@Override` annotation. When authenticating, the method returns a `User` reference. However, when calling `user.getRoleDisplayName()`, Java dynamically invokes the child class's overridden version depending on whether the object is an Admin, Lecturer, or Student."*

---

### Concept 6: Exception Handling
* **Which Class Demonstrates It?**
  `DatabaseConnection.java`, all DAO classes (e.g., `StudentDAO.java`, `MarksDAO.java`), and `LoginController.java`.
* **Which Code Demonstrates It?**
  ```java
  // In DatabaseConnection.java & StudentDAO.java:
  // Using try-with-resources to automatically close connections and statements
  try (Connection conn = DatabaseConnection.getConnection();
       PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, regNo);
      stmt.executeUpdate();

  } catch (SQLException e) {
      // Graceful error logging without crashing the desktop app
      System.err.println("Database operation failed: " + e.getMessage());
  }
  ```
* **Why It Is Used:**
  To prevent unexpected application crashes when errors happen (such as network drops, invalid SQL, or incorrect user input) and to display friendly error messages.
* **Viva Explanation:**
  > *"Exception handling ensures that if a database query fails or a connection drops, the application doesn't abruptly crash. I used `try-with-resources` blocks so that database connections, PreparedStatements, and ResultSets are automatically closed even if an exception occurs. In the UI, friendly alert dialogs are displayed instead of showing raw stack traces to the user."*

---

### Concept 7: Database Handling & JDBC
* **Which Class Demonstrates It?**
  `DatabaseConnection.java` and all DAO classes (`UserDAO`, `StudentDAO`, `AttendanceDAO`, `MarksDAO`, etc.).
* **Which Code Demonstrates It?**
  ```java
  // Using JDBC Connection and PreparedStatement to prevent SQL Injection
  String sql = "UPDATE users SET email = ?, phone = ? WHERE user_id = ?";
  try (Connection conn = DatabaseConnection.getConnection();
       PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, email);
      stmt.setString(2, phone);
      stmt.setInt(3, userId);
      return stmt.executeUpdate() > 0;
  }
  ```
* **Why It Is Used:**
  To connect the Java application with the MySQL database to execute queries and transactions reliably.
* **Viva Explanation:**
  > *"I used standard Java Database Connectivity (JDBC). Database configurations are read from `db.properties` via `DatabaseConnection`. All database commands use `PreparedStatement` with parameterized placeholders (`?`) to prevent SQL Injection vulnerabilities."*

---

### Concept 8: JavaFX GUI
* **Which Class Demonstrates It?**
  `Main.java`, `SceneManager.java`, and controllers (`LoginController.java`, `MarksManagementController.java`, etc.).
* **Which Code Demonstrates It?**
  ```java
  // FXML Bindings in Controller:
  @FXML private TableView<Marks> tblMarks;
  @FXML private TableColumn<Marks, Double> colCa;
  @FXML private Button btnEnterMarks;

  // Event handler connected to FXML action:
  @FXML
  private void handleEnterMarks() {
      // Dialog modal and live calculation logic
  }
  ```
* **Why It Is Used:**
  To build an interactive, desktop graphical user interface with CSS styling for students, lecturers, technical officers, and administrators.
* **Viva Explanation:**
  > *"JavaFX provides the desktop presentation layer. The view layout is defined in declarative FXML files, styled with custom CSS (`styles.css`), and connected to Java controllers using `@FXML` annotations. `SceneManager` handles single-window scene switching."*

---

## 2. Likely Viva / Coding Evaluation Questions & Answers

### Q1: Why did you make your fields `private` instead of `public`?
**Answer:**
> *"Making fields `private` implements Encapsulation. If fields were `public`, any external class could modify values without validation—for example, setting a negative attendance or an invalid mark above 100. By keeping them private and providing getters and setters, we protect the internal integrity of our objects."*

### Q2: What is the purpose of the `super` keyword in your project?
**Answer:**
> *"The `super` keyword is used in subclasses like `Student`, `Lecturer`, and `Admin` to call the constructor of the parent class `User`. This initializes the inherited fields such as `userId`, `username`, `email`, and `phone` before initializing the subclass's own fields."*

### Q3: Why did you make `User` an abstract class?
**Answer:**
> *"Because a generic 'User' is an abstract concept that shouldn't be instantiated on its own. Every user in the faculty is specifically an Admin, Lecturer, Technical Officer, or Student. Making `User` abstract prevents developers from doing `new User()` while still letting us share code across all user types."*

### Q4: How is Polymorphism demonstrated in your authentication process?
**Answer:**
> *"In `UserDAO.authenticate()`, the return type is the superclass `User`. Depending on the user's role in MySQL, the method instantiates and returns a concrete subclass like `new Student(...)` or `new Lecturer(...)`. When the dashboard calls `user.getRoleDisplayName()`, Java uses dynamic binding at runtime to execute the child class's specific method."*

### Q5: Why did you use `PreparedStatement` instead of regular `Statement`?
**Answer:**
> *"I used `PreparedStatement` for two main reasons: First, security—it prevents SQL Injection attacks by treating input as parameters rather than executable SQL code. Second, performance—PreparedStatements are pre-compiled by the database engine."*

### Q6: What is a DAO and why did you use the DAO pattern?
**Answer:**
> *"DAO stands for Data Access Object. It isolates all database operations (SQL queries, inserts, updates) from the JavaFX UI controllers. This follows the Single Responsibility Principle: controllers handle user interactions, while DAOs handle database persistence."*

### Q7: What is the benefit of `try-with-resources`?
**Answer:**
> *"In JDBC, database connections and statements must be closed to prevent resource and memory leaks. The `try-with-resources` syntax guarantees that every resource declared in the parentheses (like `Connection`, `PreparedStatement`, `ResultSet`) is automatically closed when execution leaves the block, even if an exception is thrown."*

### Q8: How does the application calculate attendance and check the 80% rule?
**Answer:**
> *"Each course has 15 Theory and 15 Practical sessions, totaling 30 sessions. The system counts attended theory and practical sessions. If a student missed sessions due to illness and has approved medical certificates, those approved medical sessions are added to the attended count. If the resulting effective percentage is 80.0% or greater, the student is marked as attendance eligible."*

### Q9: How is Continuous Assessment (CA) eligibility determined?
**Answer:**
> *"According to the specification, CA marks are computed from quizzes, mid-term examinations, and assignments. A student must achieve a CA mark of greater than or equal to 40% (`caMark >= 40.0`) to satisfy CA eligibility."*

### Q10: How is Overall Final-Exam Eligibility calculated?
**Answer:**
> *"A student must satisfy two conditions simultaneously:
> 1. Attendance Eligibility (effective attendance >= 80% including approved medicals)
> 2. CA Eligibility (CA mark >= 40%)
> If and only if both conditions are met, the student is marked 'Eligible' for the final examination."*

### Q11: Which grading standard does the project implement?
**Answer:**
> *"The project strictly follows UGC Commission Circular No. 12-2024. Total marks are calculated from CA (40% weight) and the Final Examination (60% weight). The marks are mapped to letter grades: A+ (>=85, GPV 4.00), A (75-84, GPV 4.00), A- (70-74, GPV 3.70), down to E (<30, GPV 0.00). This logic is centralized in `GradeCalculator.java`."*

### Q12: How are SGPA and CGPA calculated?
**Answer:**
> *"GPA is calculated as the sum of `(GPV × Course Credit)` divided by the `Total Credits Completed` across the evaluated courses. This weighted average reflects the student's academic standing accurately."*

### Q13: How are passwords stored and verified?
**Answer:**
> *"Passwords are never stored in plain text. When a user is registered or logs in, `PasswordUtil.hashPassword()` generates a 256-bit cryptographic SHA-256 hex string. When logging in, the entered password is hashed and compared against the stored hash in the database."*

### Q14: How are profile editing permissions enforced?
**Answer:**
> *"In `ProfileController.java`, the system inspects the logged-in user type. For students, fields like registration number, department, batch, status, username, and full name have `setEditable(false)`. Students can only modify their contact phone number, email, and avatar. Only Admins can modify account profiles fully."*

### Q15: How does the application prevent opening duplicate windows?
**Answer:**
> *"I implemented a `SceneManager` utility that retains a reference to the primary `Stage`. When navigating between screens (e.g. from Login to Dashboard), the scene on the existing window is replaced rather than spawning new stages."*

### Q16: Why do you have both `Main.java` and `AppLauncher.java`?
**Answer:**
> *"Since Java 11, JavaFX is not bundled inside the JDK. When you directly run a class that extends `javafx.application.Application`, Java's launcher performs a strict check looking for JavaFX modules on the module path, throwing 'JavaFX runtime components are missing'. By having a separate `AppLauncher` class that does not extend `Application` simply call `Main.main(args)`, the JVM loads the JavaFX libraries from the standard Maven classpath without needing complex VM options."*

---

## 3. Summary of Project Classes & Responsibilities

| Class | Responsibility | Key OOP Concept |
| :--- | :--- | :--- |
| `User.java` | Base abstract entity for all faculty system users | Abstraction, Encapsulation |
| `Admin.java` | Subclass representing system administrator | Inheritance, Polymorphism |
| `Lecturer.java` | Subclass representing academic staff | Inheritance, Polymorphism |
| `TechnicalOfficer.java` | Subclass representing laboratory technical staff | Inheritance, Polymorphism |
| `Student.java` | Subclass representing undergraduate student | Inheritance, Polymorphism |
| `Course.java` | Encapsulates module details and credit units | Encapsulation |
| `CourseMaterial.java` | Represents lecture notes, lab sheets, and resources | Encapsulation |
| `AttendanceSummary.java` | Calculates 15 theory + 15 practical sessions and 80% rule | Encapsulation, Business Logic |
| `MedicalRecord.java` | Encapsulates student medical submissions and approvals | Encapsulation |
| `Marks.java` | Holds marks components, CA eligibility, and final grade | Encapsulation, Business Logic |
| `Notice.java` | Represents faculty notices and audience targeting | Encapsulation |
| `Timetable.java` | Represents lecture and laboratory room schedules | Encapsulation |
| `EligibilityStatus.java` | Evaluates combined CA and Attendance eligibility | Business Logic |
| `GradeCalculator.java` | Implements UGC Commission Circular No. 12-2024 grading | Utility / Static methods |
| `DatabaseConnection.java` | Centralized JDBC connection management via `db.properties` | Exception Handling, JDBC |
| `PasswordUtil.java` | SHA-256 cryptographic password hashing | Security Utility |
| `SessionManager.java` | Manages authenticated user state in memory | State Management |
| `SceneManager.java` | Manages single-stage scene and sub-view navigation | GUI Navigation |
