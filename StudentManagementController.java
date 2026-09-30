package com.faculty.management.controller;

import com.faculty.management.dao.StudentDAO;
import com.faculty.management.model.Student;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing Undergraduate student records.
 * Provides full CRUD: Create, Read, Update, Delete, Search, and Status tracking.
 */
public class StudentManagementController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Student> tblStudents;
    @FXML private TableColumn<Student, String> colRegNo;
    @FXML private TableColumn<Student, String> colFullName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colPhone;
    @FXML private TableColumn<Student, String> colDepartment;
    @FXML private TableColumn<Student, String> colBatch;
    @FXML private TableColumn<Student, String> colStatus;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final StudentDAO studentDAO = new StudentDAO();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colRegNo.setCellValueFactory(new PropertyValueFactory<>("regNo"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batch"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("studentStatus"));

        loadStudents();

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                loadStudents();
            } else {
                List<Student> filtered = studentDAO.searchStudents(newText.trim());
                studentList.setAll(filtered);
                tblStudents.setItems(studentList);
            }
        });
    }

    public void setReadOnly(boolean readOnly) {
        if (btnAdd != null) btnAdd.setVisible(!readOnly);
        if (btnEdit != null) btnEdit.setVisible(!readOnly);
        if (btnDelete != null) btnDelete.setVisible(!readOnly);
    }

    @FXML
    public void loadStudents() {
        studentList.clear();
        studentList.addAll(studentDAO.getAllStudents());
        tblStudents.setItems(studentList);
    }

    @FXML
    private void handleAddStudent() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Undergraduate");
        dialog.setHeaderText("Enter Undergraduate Details & Login Credentials");

        ButtonType saveButtonType = new ButtonType("Save Student", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField regNoField = new TextField();
        regNoField.setPromptText("TG/2023/1025");
        TextField fullNameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue("Department of Computer Science");

        TextField batchField = new TextField("2022/2023");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("NORMAL", "REPEAT", "BATCH_MISSED");
        statusCombo.setValue("NORMAL");

        grid.add(new Label("Registration No:"), 0, 0);
        grid.add(regNoField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullNameField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(new Label("Department:"), 0, 6);
        grid.add(deptCombo, 1, 6);
        grid.add(new Label("Batch:"), 0, 7);
        grid.add(batchField, 1, 7);
        grid.add(new Label("Status:"), 0, 8);
        grid.add(statusCombo, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!ValidationUtil.isNotEmpty(regNoField.getText()) ||
                    !ValidationUtil.isNotEmpty(fullNameField.getText()) ||
                    !ValidationUtil.isNotEmpty(usernameField.getText()) ||
                    !ValidationUtil.isNotEmpty(passwordField.getText()) ||
                    !ValidationUtil.isValidEmail(emailField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please fill in all mandatory fields with valid email.");
                    return null;
                }

                Student s = new Student();
                s.setRegNo(regNoField.getText().trim());
                s.setFullName(fullNameField.getText().trim());
                s.setUsername(usernameField.getText().trim());
                s.setEmail(emailField.getText().trim());
                s.setPhone(phoneField.getText().trim());
                s.setDepartment(deptCombo.getValue());
                s.setBatch(batchField.getText().trim());
                s.setStudentStatus(statusCombo.getValue());
                s.setProfileImage("default_avatar.png");

                boolean added = studentDAO.addStudent(s, passwordField.getText().trim());
                if (added) {
                    AlertUtil.showInfo("Success", "Student registered successfully!");
                    loadStudents();
                } else {
                    AlertUtil.showError("Error", "Could not register student. Username or Reg No may already exist.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditStudent() {
        Student selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a student from the table to edit.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Undergraduate Details");
        dialog.setHeaderText("Updating details for: " + selected.getFullName());

        ButtonType saveButtonType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField regNoField = new TextField(selected.getRegNo());
        TextField fullNameField = new TextField(selected.getFullName());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue(selected.getDepartment());

        TextField batchField = new TextField(selected.getBatch());
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("NORMAL", "REPEAT", "BATCH_MISSED");
        statusCombo.setValue(selected.getStudentStatus());

        grid.add(new Label("Registration No:"), 0, 0);
        grid.add(regNoField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Department:"), 0, 4);
        grid.add(deptCombo, 1, 4);
        grid.add(new Label("Batch:"), 0, 5);
        grid.add(batchField, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!ValidationUtil.isValidEmail(emailField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide a valid email address.");
                    return null;
                }

                selected.setRegNo(regNoField.getText().trim());
                selected.setFullName(fullNameField.getText().trim());
                selected.setEmail(emailField.getText().trim());
                selected.setPhone(phoneField.getText().trim());
                selected.setDepartment(deptCombo.getValue());
                selected.setBatch(batchField.getText().trim());
                selected.setStudentStatus(statusCombo.getValue());

                boolean updated = studentDAO.updateStudent(selected);
                if (updated) {
                    AlertUtil.showInfo("Success", "Student updated successfully!");
                    loadStudents();
                } else {
                    AlertUtil.showError("Error", "Failed to update student.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a student from the table to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete student: " + selected.getFullName() + " (" + selected.getRegNo() + ")?\nAll associated attendance, enrollments, and marks will also be deleted."
        );

        if (confirm) {
            boolean deleted = studentDAO.deleteStudent(selected.getUserId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Student removed successfully.");
                loadStudents();
            } else {
                AlertUtil.showError("Error", "Could not delete student.");
            }
        }
    }
}
