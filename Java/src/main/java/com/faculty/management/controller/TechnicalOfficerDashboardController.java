/*
 * This JavaFX controller handles the TechnicalOfficerDashboard screen.
 * Explain it using: initialize -> user action -> validation -> DAO -> UI update.
 */
package com.faculty.management.controller;

import com.faculty.management.dao.MedicalDAO;
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
 * Controller for Technical Officer Dashboard.
 * Focuses on Student Attendance maintenance, Medical Certificate approvals, and Lab Timetables.
 */
public class TechnicalOfficerDashboardController {

    @FXML private Label lblOfficerName;
    @FXML private Label lblPageTitle;
    @FXML private StackPane contentArea;

    // Overview Metric Labels
    @FXML private VBox overviewView;
    @FXML private Label lblTotalStudentsCount;
    @FXML private Label lblPendingMedicalsCount;

    // Navigation Buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavStudents;
    @FXML private Button btnNavAttendance;
    @FXML private Button btnNavMedical;
    @FXML private Button btnNavTimetable;
    @FXML private Button btnNavNotices;
    @FXML private Button btnNavProfile;

    private final StudentDAO studentDAO = new StudentDAO();
    private final MedicalDAO medicalDAO = new MedicalDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblOfficerName.setText(user.getFullName());
        }
        showDashboardOverview();
    }

    private void setActiveNav(Button activeButton, String title) {
        Button[] buttons = {btnNavDashboard, btnNavStudents, btnNavAttendance, 
                            btnNavMedical, btnNavTimetable, btnNavNotices, btnNavProfile};
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
        setActiveNav(btnNavDashboard, "Technical Officer Dashboard Overview");
        contentArea.getChildren().setAll(overviewView);

        lblTotalStudentsCount.setText(String.valueOf(studentDAO.countStudents()));
        lblPendingMedicalsCount.setText(String.valueOf(medicalDAO.countPendingMedicals()));
    }

    @FXML
    public void showStudents() {
        setActiveNav(btnNavStudents, "Faculty Undergraduates");
        StudentManagementController ctrl = SceneManager.loadViewIntoPane(contentArea, "/fxml/students_view.fxml");
        if (ctrl != null) {
            ctrl.setReadOnly(true);
        }
    }

    @FXML
    public void showAttendance() {
        setActiveNav(btnNavAttendance, "Student Attendance Records & Logging");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/attendance_view.fxml");
    }

    @FXML
    public void showMedical() {
        setActiveNav(btnNavMedical, "Maintain & Approve Medical Certificates");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/medical_view.fxml");
    }

    @FXML
    public void showTimetable() {
        setActiveNav(btnNavTimetable, "Department Laboratory & Lecture Timetable");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/timetable_view.fxml");
    }

    @FXML
    public void showNotices() {
        setActiveNav(btnNavNotices, "Faculty Notices");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/notices_view.fxml");
    }

    @FXML
    public void showProfile() {
        setActiveNav(btnNavProfile, "Technical Officer Profile");
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
