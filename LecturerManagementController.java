package com.faculty.management.controller;

import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.model.Lecturer;
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

/**
 * Controller for managing Academic Staff / Lecturers.
 */
public class LecturerManagementController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Lecturer> tblLecturers;
    @FXML private TableColumn<Lecturer, String> colFullName;
    @FXML private TableColumn<Lecturer, String> colEmail;
    @FXML private TableColumn<Lecturer, String> colPhone;
    @FXML private TableColumn<Lecturer, String> colDepartment;
    @FXML private TableColumn<Lecturer, String> colDesignation;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final ObservableList<Lecturer> lecturerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colDesignation.setCellValueFactory(new PropertyValueFactory<>("designation"));

        loadLecturers();

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                loadLecturers();
            } else {
                List<Lecturer> filtered = lecturerDAO.searchLecturers(newText.trim());
                lecturerList.setAll(filtered);
                tblLecturers.setItems(lecturerList);
            }
        });
    }

    @FXML
    public void loadLecturers() {
        lecturerList.clear();
        lecturerList.addAll(lecturerDAO.getAllLecturers());
        tblLecturers.setItems(lecturerList);
    }

    @FXML
    private void handleAddLecturer() {
        Dialog<Lecturer> dialog = new Dialog<>();
        dialog.setTitle("Add New Lecturer");
        dialog.setHeaderText("Enter Lecturer Credentials & Academic Profile");

        ButtonType saveBtn = new ButtonType("Save Lecturer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField fullNameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue("Department of Computer Science");

        TextField designationField = new TextField("Senior Lecturer Gr. II");

        grid.add(new Label("Full Name (with Title):"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Department:"), 0, 5);
        grid.add(deptCombo, 1, 5);
        grid.add(new Label("Designation:"), 0, 6);
        grid.add(designationField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                if (!ValidationUtil.isNotEmpty(fullNameField.getText()) ||
                    !ValidationUtil.isNotEmpty(usernameField.getText()) ||
                    !ValidationUtil.isNotEmpty(passwordField.getText()) ||
                    !ValidationUtil.isValidEmail(emailField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide all required fields with valid email.");
                    return null;
                }

                Lecturer lec = new Lecturer();
                lec.setFullName(fullNameField.getText().trim());
                lec.setUsername(usernameField.getText().trim());
                lec.setEmail(emailField.getText().trim());
                lec.setPhone(phoneField.getText().trim());
                lec.setDepartment(deptCombo.getValue());
                lec.setDesignation(designationField.getText().trim());
                lec.setProfileImage("default_avatar.png");

                boolean added = lecturerDAO.addLecturer(lec, passwordField.getText().trim());
                if (added) {
                    AlertUtil.showInfo("Success", "Lecturer profile created successfully!");
                    loadLecturers();
                } else {
                    AlertUtil.showError("Error", "Could not create lecturer. Username may already exist.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditLecturer() {
        Lecturer selected = tblLecturers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a lecturer to edit.");
            return;
        }

        Dialog<Lecturer> dialog = new Dialog<>();
        dialog.setTitle("Edit Lecturer Details");
        dialog.setHeaderText("Updating: " + selected.getFullName());

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField fullNameField = new TextField(selected.getFullName());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue(selected.getDepartment());
        TextField designationField = new TextField(selected.getDesignation());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);
        grid.add(new Label("Designation:"), 0, 4);
        grid.add(designationField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                if (!ValidationUtil.isValidEmail(emailField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide a valid email address.");
                    return null;
                }

                selected.setFullName(fullNameField.getText().trim());
                selected.setEmail(emailField.getText().trim());
                selected.setPhone(phoneField.getText().trim());
                selected.setDepartment(deptCombo.getValue());
                selected.setDesignation(designationField.getText().trim());

                boolean updated = lecturerDAO.updateLecturer(selected);
                if (updated) {
                    AlertUtil.showInfo("Success", "Lecturer updated successfully!");
                    loadLecturers();
                } else {
                    AlertUtil.showError("Error", "Could not update lecturer profile.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteLecturer() {
        Lecturer selected = tblLecturers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a lecturer to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete: " + selected.getFullName() + "?\nCourses taught by this lecturer will be unassigned."
        );

        if (confirm) {
            boolean deleted = lecturerDAO.deleteLecturer(selected.getUserId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Lecturer removed successfully.");
                loadLecturers();
            } else {
                AlertUtil.showError("Error", "Could not delete lecturer.");
            }
        }
    }
}
