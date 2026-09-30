package com.faculty.management.controller;

import com.faculty.management.dao.AttendanceDAO;
import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.StudentDAO;
import com.faculty.management.model.AttendanceSummary;
import com.faculty.management.model.Course;
import com.faculty.management.model.Student;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Theory & Practical Attendance (15 sessions each = 30 total).
 * Calculates combined attendance, approved medicals integration, and 80% eligibility.
 */
public class AttendanceManagementController {

    @FXML private ComboBox<Course> cmbCourses;
    @FXML private Label lblCourseSelector;
    @FXML private Button btnMarkAttendance;
    @FXML private Button btnRefresh;

    @FXML private TableView<AttendanceSummary> tblAttendance;
    @FXML private TableColumn<AttendanceSummary, String> colRegNo;
    @FXML private TableColumn<AttendanceSummary, String> colStudentName;
    @FXML private TableColumn<AttendanceSummary, String> colCourseCode;
    @FXML private TableColumn<AttendanceSummary, Integer> colTheoryAttended;
    @FXML private TableColumn<AttendanceSummary, Integer> colPracticalAttended;
    @FXML private TableColumn<AttendanceSummary, Integer> colTotalAttended;
    @FXML private TableColumn<AttendanceSummary, String> colRawPercentage;
    @FXML private TableColumn<AttendanceSummary, Integer> colApprovedMedicals;
    @FXML private TableColumn<AttendanceSummary, String> colEffectivePercentage;
    @FXML private TableColumn<AttendanceSummary, String> colEligibility;
    @FXML private TableColumn<AttendanceSummary, String> colScenario;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ObservableList<AttendanceSummary> summaryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(new PropertyValueFactory<>("regNo"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colTheoryAttended.setCellValueFactory(new PropertyValueFactory<>("theoryAttended"));
        colPracticalAttended.setCellValueFactory(new PropertyValueFactory<>("practicalAttended"));
        colTotalAttended.setCellValueFactory(new PropertyValueFactory<>("totalAttended"));
        colRawPercentage.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedRawPercentage()));
        colApprovedMedicals.setCellValueFactory(new PropertyValueFactory<>("approvedMedicals"));
        colEffectivePercentage.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedEffectivePercentage()));
        colEligibility.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEligibilityStatus()));
        colScenario.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSummaryDescription()));

        User user = SessionManager.getCurrentUser();
        boolean isStudent = user != null && "STUDENT".equalsIgnoreCase(user.getRole());
        boolean isOfficer = user != null && "TECHNICAL_OFFICER".equalsIgnoreCase(user.getRole());
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        btnMarkAttendance.setVisible(isOfficer || isAdmin);
        btnMarkAttendance.setManaged(isOfficer || isAdmin);

        if (isStudent) {
            lblCourseSelector.setVisible(false);
            lblCourseSelector.setManaged(false);
            cmbCourses.setVisible(false);
            cmbCourses.setManaged(false);
            loadStudentAttendance(user.getUserId());
        } else {
            loadCourses();
        }
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.getAllCourses();
        cmbCourses.getItems().setAll(courses);
        cmbCourses.setOnAction(e -> {
            if (cmbCourses.getValue() != null) {
                loadCourseAttendance(cmbCourses.getValue().getCourseId());
            }
        });

        if (!courses.isEmpty()) {
            cmbCourses.getSelectionModel().selectFirst();
            loadCourseAttendance(courses.get(0).getCourseId());
        }
    }

    private void loadCourseAttendance(int courseId) {
        summaryList.clear();
        summaryList.addAll(attendanceDAO.getBatchAttendanceSummary(courseId));
        tblAttendance.setItems(summaryList);
    }

    private void loadStudentAttendance(int studentId) {
        summaryList.clear();
        summaryList.addAll(attendanceDAO.getStudentAllCourseSummaries(studentId));
        tblAttendance.setItems(summaryList);
    }

    @FXML
    private void handleRefresh() {
        User user = SessionManager.getCurrentUser();
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            loadStudentAttendance(user.getUserId());
        } else if (cmbCourses.getValue() != null) {
            loadCourseAttendance(cmbCourses.getValue().getCourseId());
        }
    }

    @FXML
    private void handleMarkAttendance() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Record Attendance");
        dialog.setHeaderText("Log Session Attendance (15 Theory / 15 Practical Sessions)");

        ButtonType saveBtn = new ButtonType("Record Attendance", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        ComboBox<Student> stuCombo = new ComboBox<>();
        stuCombo.getItems().addAll(studentDAO.getAllStudents());
        if (!stuCombo.getItems().isEmpty()) stuCombo.getSelectionModel().selectFirst();

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(courseDAO.getAllCourses());
        if (cmbCourses.getValue() != null) courseCombo.setValue(cmbCourses.getValue());
        else if (!courseCombo.getItems().isEmpty()) courseCombo.getSelectionModel().selectFirst();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("THEORY", "PRACTICAL");
        typeCombo.setValue("THEORY");

        Spinner<Integer> sessionSpinner = new Spinner<>(1, 15, 1);
        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PRESENT", "ABSENT");
        statusCombo.setValue("PRESENT");

        grid.add(new Label("Undergraduate:"), 0, 0);
        grid.add(stuCombo, 1, 0);
        grid.add(new Label("Course:"), 0, 1);
        grid.add(courseCombo, 1, 1);
        grid.add(new Label("Session Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Session Number (1-15):"), 0, 3);
        grid.add(sessionSpinner, 1, 3);
        grid.add(new Label("Session Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Attendance Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (stuCombo.getValue() == null || courseCombo.getValue() == null || datePicker.getValue() == null) {
                    AlertUtil.showWarning("Input Error", "Please select all required attendance options.");
                    return false;
                }

                boolean saved = attendanceDAO.markAttendance(
                    stuCombo.getValue().getUserId(),
                    courseCombo.getValue().getCourseId(),
                    typeCombo.getValue(),
                    sessionSpinner.getValue(),
                    Date.valueOf(datePicker.getValue()),
                    statusCombo.getValue()
                );

                if (saved) {
                    AlertUtil.showInfo("Success", "Attendance recorded successfully!");
                    handleRefresh();
                    return true;
                } else {
                    AlertUtil.showError("Error", "Could not record attendance.");
                    return false;
                }
            }
            return false;
        });

        dialog.showAndWait();
    }
}
