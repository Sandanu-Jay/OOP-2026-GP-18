package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.dao.StudentDAO;
import com.faculty.management.dao.TechnicalOfficerDAO;
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
 * Controller for Administrator Dashboard.
 * Coordinates Admin navigation and renders metric summary cards.
 */
public class AdminDashboardController {

    @FXML private Label lblAdminName;
    @FXML private Label lblPageTitle;
    @FXML private StackPane contentArea;

    // Overview Metric Labels
    @FXML private VBox overviewView;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblTotalLecturers;
    @FXML private Label lblTotalOfficers;
    @FXML private Label lblTotalCourses;

    // Navigation Buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavStudents;
    @FXML private Button btnNavLecturers;
    @FXML private Button btnNavOfficers;
    @FXML private Button btnNavCourses;
    @FXML private Button btnNavNotices;
    @FXML private Button btnNavTimetable;
    @FXML private Button btnNavProfile;

    private final StudentDAO studentDAO = new StudentDAO();
    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final TechnicalOfficerDAO officerDAO = new TechnicalOfficerDAO();
    private final CourseDAO courseDAO = new CourseDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblAdminName.setText(user.getFullName());
        }
        showDashboardOverview();
    }

    private void setActiveNav(Button activeButton, String title) {
        Button[] buttons = {btnNavDashboard, btnNavStudents, btnNavLecturers, 
                            btnNavOfficers, btnNavCourses, btnNavNotices, 
                            btnNavTimetable, btnNavProfile};
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
        setActiveNav(btnNavDashboard, "Administrator Dashboard Overview");
        contentArea.getChildren().setAll(overviewView);

        // Refresh live metric counters
        lblTotalStudents.setText(String.valueOf(studentDAO.countStudents()));
        lblTotalLecturers.setText(String.valueOf(lecturerDAO.countLecturers()));
        lblTotalOfficers.setText(String.valueOf(officerDAO.countOfficers()));
        lblTotalCourses.setText(String.valueOf(courseDAO.countCourses()));
    }

    @FXML
    public void showStudents() {
        setActiveNav(btnNavStudents, "Manage Undergraduate Students");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/students_view.fxml");
    }

    @FXML
    public void showLecturers() {
        setActiveNav(btnNavLecturers, "Manage Academic Lecturers");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/lecturers_view.fxml");
    }

    @FXML
    public void showOfficers() {
        setActiveNav(btnNavOfficers, "Manage Technical Officers");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/officers_view.fxml");
    }

    @FXML
    public void showCourses() {
        setActiveNav(btnNavCourses, "Manage Course Catalog & Materials");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/courses_view.fxml");
    }

    @FXML
    public void showNotices() {
        setActiveNav(btnNavNotices, "Faculty Notices & Announcements");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/notices_view.fxml");
    }

    @FXML
    public void showTimetable() {
        setActiveNav(btnNavTimetable, "Faculty Timetable & Schedule");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/timetable_view.fxml");
    }

    @FXML
    public void showProfile() {
        setActiveNav(btnNavProfile, "Administrator Profile");
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
