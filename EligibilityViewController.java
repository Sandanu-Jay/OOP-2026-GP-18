package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.MarksDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.EligibilityStatus;
import com.faculty.management.model.User;
import com.faculty.management.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller for Undergraduate Final-Exam Eligibility View.
 * Displays CA Mark, CA Eligibility (>= 40%), Attendance % (>= 80%),
 * and Overall Final-Exam Eligibility.
 */
public class EligibilityViewController {

    @FXML private ComboBox<Course> cmbCourses;
    @FXML private Label lblFilter;
    @FXML private TableView<EligibilityStatus> tblEligibility;
    @FXML private TableColumn<EligibilityStatus, String> colRegNo;
    @FXML private TableColumn<EligibilityStatus, String> colStudentName;
    @FXML private TableColumn<EligibilityStatus, String> colCourseCode;
    @FXML private TableColumn<EligibilityStatus, String> colCaMark;
    @FXML private TableColumn<EligibilityStatus, String> colCaStatus;
    @FXML private TableColumn<EligibilityStatus, String> colAttendance;
    @FXML private TableColumn<EligibilityStatus, String> colAttendanceStatus;
    @FXML private TableColumn<EligibilityStatus, String> colFinalStatus;

    private final MarksDAO marksDAO = new MarksDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<EligibilityStatus> eligibilityList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(new PropertyValueFactory<>("regNo"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCaMark.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedCaMark()));
        colCaStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCaStatusText()));
        colAttendance.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFormattedAttendance()));
        colAttendanceStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAttendanceStatusText()));
        colFinalStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFinalExamStatusText()));

        User user = SessionManager.getCurrentUser();
        List<Course> courses;
        if (user != null && "LECTURER".equalsIgnoreCase(user.getRole())) {
            courses = courseDAO.getCoursesByLecturer(user.getUserId());
        } else {
            courses = courseDAO.getAllCourses();
        }

        cmbCourses.getItems().setAll(courses);
        cmbCourses.setOnAction(e -> {
            if (cmbCourses.getValue() != null) {
                loadEligibility(cmbCourses.getValue().getCourseId());
            }
        });

        if (!courses.isEmpty()) {
            cmbCourses.getSelectionModel().selectFirst();
            loadEligibility(courses.get(0).getCourseId());
        }
    }

    public void loadEligibility(int courseId) {
        eligibilityList.clear();
        eligibilityList.addAll(marksDAO.getOverallEligibilityByCourse(courseId));
        tblEligibility.setItems(eligibilityList);
    }

    @FXML
    private void handleRefresh() {
        if (cmbCourses.getValue() != null) {
            loadEligibility(cmbCourses.getValue().getCourseId());
        }
    }
}
