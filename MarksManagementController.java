package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.MarksDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.Marks;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.GradeCalculator;
import com.faculty.management.util.SessionManager;
import com.faculty.management.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Controller for Marks, Continuous Assessment (CA) Eligibility, and Grading.
 * Adheres strictly to UGC Commission Circular No. 12-2024.
 */
public class MarksManagementController {

    @FXML private ComboBox<Course> cmbCourses;
    @FXML private Label lblCourseSelector;
    @FXML private Button btnEnterMarks;
    @FXML private Button btnRefresh;

    @FXML private TableView<Marks> tblMarks;
    @FXML private TableColumn<Marks, String> colRegNo;
    @FXML private TableColumn<Marks, String> colStudentName;
    @FXML private TableColumn<Marks, String> colCourseCode;
    @FXML private TableColumn<Marks, Double> colQuiz;
    @FXML private TableColumn<Marks, Double> colMid;
    @FXML private TableColumn<Marks, Double> colAssignment;
    @FXML private TableColumn<Marks, Double> colCa;
    @FXML private TableColumn<Marks, String> colCaStatus;
    @FXML private TableColumn<Marks, Double> colFinalExam;
    @FXML private TableColumn<Marks, Double> colTotal;
    @FXML private TableColumn<Marks, String> colGrade;
    @FXML private TableColumn<Marks, Double> colGpv;

    @FXML private HBox gpaSummaryBox;
    @FXML private Label lblGpaValue;

    private final MarksDAO marksDAO = new MarksDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<Marks> marksList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colQuiz.setCellValueFactory(new PropertyValueFactory<>("quizMark"));
        colMid.setCellValueFactory(new PropertyValueFactory<>("midMark"));
        colAssignment.setCellValueFactory(new PropertyValueFactory<>("assignmentMark"));
        colCa.setCellValueFactory(new PropertyValueFactory<>("caMark"));
        colCaStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaEligibilityStatus()));
        colFinalExam.setCellValueFactory(new PropertyValueFactory<>("finalExamMark"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalMark"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGpv.setCellValueFactory(new PropertyValueFactory<>("gpv"));

        User user = SessionManager.getCurrentUser();
        boolean isStudent = user != null && "STUDENT".equalsIgnoreCase(user.getRole());

        if (isStudent) {
            lblCourseSelector.setVisible(false);
            lblCourseSelector.setManaged(false);
            cmbCourses.setVisible(false);
            cmbCourses.setManaged(false);
            btnEnterMarks.setVisible(false);
            btnEnterMarks.setManaged(false);

            gpaSummaryBox.setVisible(true);
            gpaSummaryBox.setManaged(true);
            loadStudentMarks(user.getUserId());
        } else {
            gpaSummaryBox.setVisible(false);
            gpaSummaryBox.setManaged(false);
            loadCoursesForStaff(user);
        }
    }

    private void loadStudentMarks(int studentId) {
        marksList.clear();
        marksList.addAll(marksDAO.getMarksByStudent(studentId));
        tblMarks.setItems(marksList);

        double gpa = marksDAO.calculateStudentGpa(studentId);
        lblGpaValue.setText(String.format("%.2f", gpa));
    }

    private void loadCoursesForStaff(User user) {
        List<Course> courses;
        if (user != null && "LECTURER".equalsIgnoreCase(user.getRole())) {
            courses = courseDAO.getCoursesByLecturer(user.getUserId());
        } else {
            courses = courseDAO.getAllCourses();
        }

        cmbCourses.getItems().setAll(courses);
        cmbCourses.setOnAction(e -> {
            Course selected = cmbCourses.getValue();
            if (selected != null) {
                loadCourseMarks(selected.getCourseId());
            }
        });

        if (!courses.isEmpty()) {
            cmbCourses.getSelectionModel().selectFirst();
            loadCourseMarks(courses.get(0).getCourseId());
        }
    }

    public void loadCourseMarks(int courseId) {
        marksList.clear();
        marksList.addAll(marksDAO.getAllMarksByCourse(courseId));
        tblMarks.setItems(marksList);
    }

    @FXML
    private void handleRefresh() {
        User user = SessionManager.getCurrentUser();
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            loadStudentMarks(user.getUserId());
        } else if (cmbCourses.getValue() != null) {
            loadCourseMarks(cmbCourses.getValue().getCourseId());
        }
    }

    @FXML
    private void handleEnterMarks() {
        Marks selected = tblMarks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a student record from the table to enter marks.");
            return;
        }

        Dialog<Marks> dialog = new Dialog<>();
        dialog.setTitle("Enter / Update Marks");
        dialog.setHeaderText("Undergraduate: " + selected.getStudentName() + " (" + selected.getStudentRegNo() + ")\n" +
                             "Course: " + (selected.getCourseCode() != null ? selected.getCourseCode() : cmbCourses.getValue().getCourseCode()));

        ButtonType saveBtn = new ButtonType("Save Marks", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField quizField = new TextField(String.valueOf(selected.getQuizMark()));
        TextField midField = new TextField(String.valueOf(selected.getMidMark()));
        TextField assignmentField = new TextField(String.valueOf(selected.getAssignmentMark()));
        TextField finalExamField = new TextField(String.valueOf(selected.getFinalExamMark()));

        Label lblCalculatedCa = new Label(String.format("CA: %.2f", selected.getCaMark()));
        Label lblCalculatedTotal = new Label(String.format("Total: %.2f | Grade: %s (GPV: %.2f)", 
                selected.getTotalMark(), selected.getGrade(), selected.getGpv()));
        lblCalculatedCa.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563EB;");
        lblCalculatedTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #0F172A;");

        // Live calculator on input change
        Runnable recalc = () -> {
            try {
                double q = Double.parseDouble(quizField.getText().trim());
                double m = Double.parseDouble(midField.getText().trim());
                double a = Double.parseDouble(assignmentField.getText().trim());
                double fe = Double.parseDouble(finalExamField.getText().trim());

                double ca = (q * 0.25) + (m * 0.50) + (a * 0.25);
                double tot = (ca * 0.40) + (fe * 0.60);
                String gr = GradeCalculator.getGrade(tot);
                double gpv = GradeCalculator.getGpv(tot);

                String caStatus = ca >= 40.0 ? "ELIGIBLE (>=40%)" : "NOT ELIGIBLE (<40%)";
                lblCalculatedCa.setText(String.format("CA Mark: %.2f [%s]", ca, caStatus));
                lblCalculatedTotal.setText(String.format("Total: %.2f | Grade: %s (GPV: %.2f)", tot, gr, gpv));
            } catch (Exception ignored) {}
        };

        quizField.textProperty().addListener((o, oldV, newV) -> recalc.run());
        midField.textProperty().addListener((o, oldV, newV) -> recalc.run());
        assignmentField.textProperty().addListener((o, oldV, newV) -> recalc.run());
        finalExamField.textProperty().addListener((o, oldV, newV) -> recalc.run());

        grid.add(new Label("Quiz Mark (out of 100):"), 0, 0);
        grid.add(quizField, 1, 0);
        grid.add(new Label("Mid-Term Exam (out of 100):"), 0, 1);
        grid.add(midField, 1, 1);
        grid.add(new Label("Assignment / Lab (out of 100):"), 0, 2);
        grid.add(assignmentField, 1, 2);
        grid.add(new Label("End-Semester Exam (out of 100):"), 0, 3);
        grid.add(finalExamField, 1, 3);
        grid.add(new Separator(), 0, 4, 2, 1);
        grid.add(lblCalculatedCa, 0, 5, 2, 1);
        grid.add(lblCalculatedTotal, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    double q = Double.parseDouble(quizField.getText().trim());
                    double m = Double.parseDouble(midField.getText().trim());
                    double a = Double.parseDouble(assignmentField.getText().trim());
                    double fe = Double.parseDouble(finalExamField.getText().trim());

                    if (!ValidationUtil.isValidMark(q) || !ValidationUtil.isValidMark(m) ||
                        !ValidationUtil.isValidMark(a) || !ValidationUtil.isValidMark(fe)) {
                        AlertUtil.showWarning("Validation Error", "All marks must be between 0.00 and 100.00.");
                        return null;
                    }

                    selected.setQuizMark(q);
                    selected.setMidMark(m);
                    selected.setAssignmentMark(a);
                    selected.setFinalExamMark(fe);
                    if (selected.getCourseId() == 0 && cmbCourses.getValue() != null) {
                        selected.setCourseId(cmbCourses.getValue().getCourseId());
                    }

                    boolean saved = marksDAO.saveOrUpdateMarks(selected);
                    if (saved) {
                        AlertUtil.showInfo("Marks Saved", "Marks calculated & saved successfully under UGC Circular No. 12-2024!");
                        handleRefresh();
                    } else {
                        AlertUtil.showError("Error", "Could not save marks.");
                    }
                } catch (NumberFormatException ex) {
                    AlertUtil.showWarning("Validation Error", "Please enter valid numeric values for all marks.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
