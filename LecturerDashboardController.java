package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.StudentDAO;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SceneManager;
import com.faculty.management.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller for Lecturer Dashboard.
 * Provides access to Assigned Courses, Course Materials, Marks, CA & Final Eligibility, Attendance, and Medicals.
 */
public class LecturerDashboardController {

    @FXML private Label lblLecturerName;
    @FXML private Label lblPageTitle;
    @FXML private StackPane contentArea;

    // Overview Metric Labels
    @FXML private VBox overviewView;
    @FXML private Label lblMyCoursesCount;
    @FXML private Label lblTotalStudentsCount;

    // Navigation Buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavCourses;
    @FXML private Button btnNavMarks;
    @FXML private Button btnNavStudents;
    @FXML private Button btnNavEligibility;
    @FXML private Button btnNavAttendance;
    @FXML private Button btnNavMedical;
    @FXML private Button btnNavNotices;
    @FXML private Button btnNavProfile;

    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblLecturerName.setText(user.getFullName());
        }
        showDashboardOverview();
    }

    private void setActiveNav(Button activeButton, String title) {
        Button[] buttons = {btnNavDashboard, btnNavCourses, btnNavMarks, 
                            btnNavStudents, btnNavEligibility, btnNavAttendance, 
                            btnNavMedical, btnNavNotices, btnNavProfile};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.getStyleClass().remove("sidebar-button-active");
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
        lblPageTitle.setText(title);
    }

    @FXML
    public void showDashboardOverview() {
        setActiveNav(btnNavDashboard, "Lecturer Dashboard Overview");
        contentArea.getChildren().setAll(overviewView);

        User user = SessionManager.getCurrentUser();
        if (user != null) {
            int myCourses = courseDAO.getCoursesByLecturer(user.getUserId()).size();
            lblMyCoursesCount.setText(String.valueOf(myCourses));
        }
        lblTotalStudentsCount.setText(String.valueOf(studentDAO.countStudents()));
    }

    @FXML
    public void showCourses() {
        setActiveNav(btnNavCourses, "My Assigned Courses & Learning Materials");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/courses_view.fxml");
    }

    @FXML
    public void showMarks() {
        setActiveNav(btnNavMarks, "Marks Entry & Grading (UGC Circular 12-2024)");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/marks_view.fxml");
    }

    @FXML
    public void showStudents() {
        setActiveNav(btnNavStudents, "Enrolled Undergraduate Students");
        StudentManagementController ctrl = SceneManager.loadViewIntoPane(contentArea, "/fxml/students_view.fxml");
        if (ctrl != null) {
            ctrl.setReadOnly(true); // Lecturer has read-only access to student profiles
        }
    }

    @FXML
    public void showEligibility() {
        setActiveNav(btnNavEligibility, "Undergraduate Final-Exam Eligibility");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/eligibility_view.fxml");
    }

    @FXML
    public void showAttendance() {
        setActiveNav(btnNavAttendance, "Student Attendance Records (15 Theory + 15 Practical)");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/attendance_view.fxml");
    }

    @FXML
    public void showMedical() {
        setActiveNav(btnNavMedical, "Undergraduate Medical Records");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/medical_view.fxml");
    }

    @FXML
    public void showNotices() {
        setActiveNav(btnNavNotices, "Faculty Notices & Announcements");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/notices_view.fxml");
    }

    @FXML
    public void showProfile() {
        setActiveNav(btnNavProfile, "Academic Profile");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/profile_view.fxml");
    }

    @FXML
    public void handleLogout() {
        boolean confirm = AlertUtil.showConfirmation("Confirm Logout", "Are you sure you want to log out?");
        if (confirm) {
            SessionManager.logout();
            SceneManager.switchScene("/fxml/login.fxml", "Faculty Management System - Login");
        }
    }
}
