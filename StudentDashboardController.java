package com.faculty.management.controller;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.MarksDAO;
import com.faculty.management.model.AttendanceSummary;
import com.faculty.management.model.Student;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SceneManager;
import com.faculty.management.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller for Undergraduate / Student Portal.
 * Gives students real-time visibility into their CGPA, Attendance, Medicals,
 * Courses, Materials, and Personal Timetable.
 */
public class StudentDashboardController {

    @FXML private Label lblStudentName;
    @FXML private Label lblRegNoHeader;
    @FXML private Label lblPageTitle;
    @FXML private StackPane contentArea;

    // Overview Metric Labels
    @FXML private VBox overviewView;
    @FXML private Label lblCgpaValue;
    @FXML private Label lblAttendancePercentage;
    @FXML private Label lblEnrolledCoursesCount;
    @FXML private Label lblExamEligibilityStatus;

    // Navigation Buttons
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavProfile;
    @FXML private Button btnNavCourses;
    @FXML private Button btnNavAttendance;
    @FXML private Button btnNavMedical;
    @FXML private Button btnNavMarks;
    @FXML private Button btnNavGpa;
    @FXML private Button btnNavTimetable;
    @FXML private Button btnNavNotices;

    private final MarksDAO marksDAO = new MarksDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblStudentName.setText(user.getFullName());
            if (user instanceof Student) {
                lblRegNoHeader.setText(((Student) user).getRegNo());
            }
        }
        showDashboardOverview();
    }

    private void setActiveNav(Button activeButton, String title) {
        Button[] buttons = {btnNavDashboard, btnNavProfile, btnNavCourses, 
                            btnNavAttendance, btnNavMedical, btnNavMarks, 
                            btnNavGpa, btnNavTimetable, btnNavNotices};
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
        setActiveNav(btnNavDashboard, "Undergraduate Portal Overview");
        contentArea.getChildren().setAll(overviewView);

        User user = SessionManager.getCurrentUser();
        if (user != null) {
            // CGPA
            double gpa = marksDAO.calculateStudentGpa(user.getUserId());
            lblCgpaValue.setText(String.format("%.2f", gpa));

            // Enrolled courses
            var courses = courseDAO.getCoursesByStudent(user.getUserId());
            lblEnrolledCoursesCount.setText(String.valueOf(courses.size()));

            // Average effective attendance across courses
            List<AttendanceSummary> attList = attendanceDAO.getStudentAllCourseSummaries(user.getUserId());
            if (!attList.isEmpty()) {
                double totalPct = 0;
                boolean allEligible = true;
                for (AttendanceSummary as : attList) {
                    totalPct += as.getEffectivePercentage();
                    if (!as.isEligible()) allEligible = false;
                }
                double avgAtt = totalPct / attList.size();
                lblAttendancePercentage.setText(String.format("%.1f%%", avgAtt));
                lblExamEligibilityStatus.setText(allEligible ? "ELIGIBLE" : "REVIEW NEEDED");
                lblExamEligibilityStatus.setStyle(allEligible ? "-fx-text-fill: #059669;" : "-fx-text-fill: #DC2626;");
            } else {
                lblAttendancePercentage.setText("N/A");
                lblExamEligibilityStatus.setText("N/A");
            }
        }
    }

    @FXML
    public void showProfile() {
        setActiveNav(btnNavProfile, "My Academic Profile");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/profile_view.fxml");
    }

    @FXML
    public void showCourses() {
        setActiveNav(btnNavCourses, "Enrolled Courses & Study Materials");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/courses_view.fxml");
    }

    @FXML
    public void showAttendance() {
        setActiveNav(btnNavAttendance, "My Attendance & 80% Eligibility");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/attendance_view.fxml");
    }

    @FXML
    public void showMedical() {
        setActiveNav(btnNavMedical, "My Submitted Medical Records");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/medical_view.fxml");
    }

    @FXML
    public void showMarks() {
        setActiveNav(btnNavMarks, "Course Marks & Evaluation (CA & Final)");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/marks_view.fxml");
    }

    @FXML
    public void showGpa() {
        setActiveNav(btnNavGpa, "GPA Calculation & UGC Circular 12-2024 Breakdown");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/gpa_view.fxml");
    }

    @FXML
    public void showTimetable() {
        setActiveNav(btnNavTimetable, "My Lecture & Lab Timetable");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/timetable_view.fxml");
    }

    @FXML
    public void showNotices() {
        setActiveNav(btnNavNotices, "Faculty Notices & Announcements");
        SceneManager.loadViewIntoPane(contentArea, "/fxml/notices_view.fxml");
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
