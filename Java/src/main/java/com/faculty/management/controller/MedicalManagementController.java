/*
 * This JavaFX controller handles the MedicalManagement screen.
 * Explain it using: initialize -> user action -> validation -> DAO -> UI update.
 */
package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.MedicalDAO;
import com.faculty.management.dao.StudentDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.MedicalRecord;
import com.faculty.management.model.Student;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SessionManager;
import com.faculty.management.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Controller for Medical Records Management.
 * Technical Officers can add, update, approve or reject medicals for absent sessions.
 */
public class MedicalManagementController {

    @FXML private TableView<MedicalRecord> tblMedicals;
    @FXML private TableColumn<MedicalRecord, String> colRegNo;
    @FXML private TableColumn<MedicalRecord, String> colStudentName;
    @FXML private TableColumn<MedicalRecord, String> colCourseCode;
    @FXML private TableColumn<MedicalRecord, String> colSessionType;
    @FXML private TableColumn<MedicalRecord, Integer> colSessionNum;
    @FXML private TableColumn<MedicalRecord, LocalDate> colMedicalDate;
    @FXML private TableColumn<MedicalRecord, String> colReason;
    @FXML private TableColumn<MedicalRecord, String> colDocumentInfo;
    @FXML private TableColumn<MedicalRecord, String> colStatus;
    @FXML private TableColumn<MedicalRecord, String> colComments;
    @FXML private TableColumn<MedicalRecord, LocalDateTime> colSubmittedDate;

    @FXML private HBox studentControls;
    @FXML private Button btnStudentSubmitMedical;

    @FXML private HBox officerControls;
    @FXML private Button btnAddMedical;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final MedicalDAO medicalDAO = new MedicalDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final ObservableList<MedicalRecord> medicalList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(new PropertyValueFactory<>("studentRegNo"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colSessionType.setCellValueFactory(new PropertyValueFactory<>("sessionType"));
        colSessionNum.setCellValueFactory(new PropertyValueFactory<>("sessionNumber"));
        colMedicalDate.setCellValueFactory(new PropertyValueFactory<>("medicalDate"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colDocumentInfo.setCellValueFactory(new PropertyValueFactory<>("documentInfo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isApproved"));
        colComments.setCellValueFactory(new PropertyValueFactory<>("comments"));
        colSubmittedDate.setCellValueFactory(new PropertyValueFactory<>("submittedDate"));

        User user = SessionManager.getCurrentUser();
        boolean isOfficer = user != null && "TECHNICAL_OFFICER".equalsIgnoreCase(user.getRole());
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isStudent = user != null && "STUDENT".equalsIgnoreCase(user.getRole());

        officerControls.setVisible(isOfficer || isAdmin);
        officerControls.setManaged(isOfficer || isAdmin);

        studentControls.setVisible(isStudent);
        studentControls.setManaged(isStudent);

        loadMedicals();
    }

    public void loadMedicals() {
        medicalList.clear();
        User user = SessionManager.getCurrentUser();
        if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            medicalList.addAll(medicalDAO.getMedicalRecordsByStudent(user.getUserId()));
        } else {
            medicalList.addAll(medicalDAO.getAllMedicalRecords());
        }
        tblMedicals.setItems(medicalList);
    }

    @FXML
    private void handleStudentSubmitMedical() {
        User user = SessionManager.getCurrentUser();
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            AlertUtil.showWarning("Permission Denied", "Only undergraduates can submit medical certificates.");
            return;
        }

        List<Course> enrolledCourses = courseDAO.getCoursesByStudent(user.getUserId());
        if (enrolledCourses.isEmpty()) {
            AlertUtil.showWarning("No Courses", "You are not enrolled in any course to submit medicals for.");
            return;
        }

        Dialog<MedicalRecord> dialog = new Dialog<>();
        dialog.setTitle("Submit Medical Certificate");
        dialog.setHeaderText("Undergraduate Medical Submission for Absent Session");

        ButtonType submitBtn = new ButtonType("Submit Certificate", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        Label lblStudent = new Label(user.getFullName() + " (" + ((Student) user).getRegNo() + ")");
        lblStudent.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(enrolledCourses);
        courseCombo.getSelectionModel().selectFirst();
        courseCombo.setPrefWidth(320);

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("THEORY", "PRACTICAL");
        typeCombo.setValue("THEORY");

        Spinner<Integer> sessionSpinner = new Spinner<>(1, 15, 1);
        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Specify illness or medical reason (e.g., Viral fever, Hospitalized, Migraine)...");
        reasonArea.setPrefRowCount(2);

        TextField docInfoField = new TextField();
        docInfoField.setPromptText("Medical Certificate # or Hospital Reference");

        Button btnBrowseFile = new Button("📎 Attach Document");
        btnBrowseFile.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Medical Certificate Document");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Medical Documents (*.pdf, *.jpg, *.png)", "*.pdf", "*.jpg", "*.jpeg", "*.png")
            );
            java.io.File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                docInfoField.setText(file.getName() + " [Ref: " + System.currentTimeMillis() % 10000 + "]");
            }
        });

        HBox docBox = new HBox(8, docInfoField, btnBrowseFile);

        grid.add(new Label("Student:"), 0, 0);
        grid.add(lblStudent, 1, 0);
        grid.add(new Label("Course Module:"), 0, 1);
        grid.add(courseCombo, 1, 1);
        grid.add(new Label("Session Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Missed Session (1-15):"), 0, 3);
        grid.add(sessionSpinner, 1, 3);
        grid.add(new Label("Date of Absence:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Medical Reason:"), 0, 5);
        grid.add(reasonArea, 1, 5);
        grid.add(new Label("Document / Ref:"), 0, 6);
        grid.add(docBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == submitBtn) {
                if (courseCombo.getValue() == null || datePicker.getValue() == null ||
                    !ValidationUtil.isNotEmpty(reasonArea.getText()) ||
                    !ValidationUtil.isNotEmpty(docInfoField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide all details: Course, Date, Reason, and Certificate Reference.");
                    return null;
                }

                MedicalRecord m = new MedicalRecord();
                m.setStudentId(user.getUserId());
                m.setCourseId(courseCombo.getValue().getCourseId());
                m.setSessionType(typeCombo.getValue());
                m.setSessionNumber(sessionSpinner.getValue());
                m.setMedicalDate(datePicker.getValue());
                m.setReason(reasonArea.getText().trim());
                m.setDocumentInfo(docInfoField.getText().trim());
                m.setIsApproved("PENDING");
                m.setComments("Submitted by student. Awaiting Technical Officer review.");

                boolean added = medicalDAO.addMedicalRecord(m);
                if (added) {
                    AlertUtil.showInfo("Medical Submitted", "Your medical certificate has been submitted for review by Technical Officers.");
                    loadMedicals();
                } else {
                    AlertUtil.showError("Error", "Could not submit medical record. Please try again.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleAddMedical() {
        Dialog<MedicalRecord> dialog = new Dialog<>();
        dialog.setTitle("Submit Medical Certificate");
        dialog.setHeaderText("Add Undergraduate Medical Submission for Absent Session");

        ButtonType submitBtn = new ButtonType("Submit Medical", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        ComboBox<Student> stuCombo = new ComboBox<>();
        stuCombo.getItems().addAll(studentDAO.getAllStudents());
        if (!stuCombo.getItems().isEmpty()) stuCombo.getSelectionModel().selectFirst();

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(courseDAO.getAllCourses());
        if (!courseCombo.getItems().isEmpty()) courseCombo.getSelectionModel().selectFirst();

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("THEORY", "PRACTICAL");
        typeCombo.setValue("THEORY");

        Spinner<Integer> sessionSpinner = new Spinner<>(1, 15, 1);
        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Reason for absence");
        reasonArea.setPrefRowCount(2);

        TextField docInfoField = new TextField();
        docInfoField.setPromptText("e.g. Government Hospital Cert #MC-9941");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PENDING", "APPROVED", "REJECTED");
        statusCombo.setValue("APPROVED");

        TextArea commentsArea = new TextArea();
        commentsArea.setPromptText("Remarks or verification notes");
        commentsArea.setPrefRowCount(2);

        grid.add(new Label("Undergraduate:"), 0, 0);
        grid.add(stuCombo, 1, 0);
        grid.add(new Label("Course:"), 0, 1);
        grid.add(courseCombo, 1, 1);
        grid.add(new Label("Session Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);
        grid.add(new Label("Missed Session (1-15):"), 0, 3);
        grid.add(sessionSpinner, 1, 3);
        grid.add(new Label("Absence Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Medical Reason:"), 0, 5);
        grid.add(reasonArea, 1, 5);
        grid.add(new Label("Certificate Reference:"), 0, 6);
        grid.add(docInfoField, 1, 6);
        grid.add(new Label("Approval Status:"), 0, 7);
        grid.add(statusCombo, 1, 7);
        grid.add(new Label("Comments:"), 0, 8);
        grid.add(commentsArea, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == submitBtn) {
                if (stuCombo.getValue() == null || courseCombo.getValue() == null ||
                    !ValidationUtil.isNotEmpty(docInfoField.getText()) || datePicker.getValue() == null) {
                    AlertUtil.showWarning("Validation Error", "Please provide all required medical certificate details.");
                    return null;
                }

                MedicalRecord m = new MedicalRecord();
                m.setStudentId(stuCombo.getValue().getUserId());
                m.setCourseId(courseCombo.getValue().getCourseId());
                m.setSessionType(typeCombo.getValue());
                m.setSessionNumber(sessionSpinner.getValue());
                m.setMedicalDate(datePicker.getValue());
                m.setReason(reasonArea.getText().trim());
                m.setDocumentInfo(docInfoField.getText().trim());
                m.setIsApproved(statusCombo.getValue());
                m.setComments(commentsArea.getText().trim());

                boolean added = medicalDAO.addMedicalRecord(m);
                if (added) {
                    AlertUtil.showInfo("Success", "Medical record submitted successfully!");
                    loadMedicals();
                } else {
                    AlertUtil.showError("Error", "Could not submit medical record.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleApprove() {
        MedicalRecord selected = tblMedicals.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a medical record from the table to approve.");
            return;
        }

        boolean updated = medicalDAO.updateApprovalStatus(selected.getMedicalId(), "APPROVED", "Approved by Technical Officer");
        if (updated) {
            AlertUtil.showInfo("Approved", "Medical certificate approved. Effective attendance updated!");
            loadMedicals();
        } else {
            AlertUtil.showError("Error", "Could not update status.");
        }
    }

    @FXML
    private void handleReject() {
        MedicalRecord selected = tblMedicals.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a medical record to reject.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("Certificate documentation insufficient");
        dialog.setTitle("Reject Medical Certificate");
        dialog.setHeaderText("Specify reason for rejection:");
        dialog.setContentText("Reason:");

        dialog.showAndWait().ifPresent(reason -> {
            boolean updated = medicalDAO.updateApprovalStatus(selected.getMedicalId(), "REJECTED", reason);
            if (updated) {
                AlertUtil.showInfo("Rejected", "Medical certificate marked as REJECTED.");
                loadMedicals();
            } else {
                AlertUtil.showError("Error", "Could not update status.");
            }
        });
    }

    @FXML
    private void handleDeleteMedical() {
        MedicalRecord selected = tblMedicals.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a medical record to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete medical record for " + selected.getStudentName() + "?");
        if (confirm) {
            boolean deleted = medicalDAO.deleteMedicalRecord(selected.getMedicalId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Medical record deleted.");
                loadMedicals();
            } else {
                AlertUtil.showError("Error", "Could not delete record.");
            }
        }
    }
}
