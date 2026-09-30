package com.faculty.management.controller;

import com.faculty.management.dao.TechnicalOfficerDAO;
import com.faculty.management.model.TechnicalOfficer;
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
 * Controller for managing Technical Officers.
 */
public class OfficerManagementController {

    @FXML private TextField txtSearch;
    @FXML private TableView<TechnicalOfficer> tblOfficers;
    @FXML private TableColumn<TechnicalOfficer, String> colFullName;
    @FXML private TableColumn<TechnicalOfficer, String> colDepartment;
    @FXML private TableColumn<TechnicalOfficer, String> colEmail;
    @FXML private TableColumn<TechnicalOfficer, String> colPhone;

    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    private final TechnicalOfficerDAO officerDAO = new TechnicalOfficerDAO();
    private final ObservableList<TechnicalOfficer> officerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadOfficers();

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                loadOfficers();
            } else {
                List<TechnicalOfficer> filtered = officerDAO.searchOfficers(newText.trim());
                officerList.setAll(filtered);
                tblOfficers.setItems(officerList);
            }
        });
    }

    @FXML
    public void loadOfficers() {
        officerList.clear();
        officerList.addAll(officerDAO.getAllOfficers());
        tblOfficers.setItems(officerList);
    }

    @FXML
    private void handleAddOfficer() {
        Dialog<TechnicalOfficer> dialog = new Dialog<>();
        dialog.setTitle("Add Technical Officer");
        dialog.setHeaderText("Enter Technical Officer Information & Credentials");

        ButtonType saveBtn = new ButtonType("Save Officer", ButtonBar.ButtonData.OK_DONE);
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

        grid.add(new Label("Full Name:"), 0, 0);
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

                TechnicalOfficer to = new TechnicalOfficer();
                to.setFullName(fullNameField.getText().trim());
                to.setUsername(usernameField.getText().trim());
                to.setEmail(emailField.getText().trim());
                to.setPhone(phoneField.getText().trim());
                to.setDepartment(deptCombo.getValue());
                to.setProfileImage("default_avatar.png");

                boolean added = officerDAO.addOfficer(to, passwordField.getText().trim());
                if (added) {
                    AlertUtil.showInfo("Success", "Technical Officer added successfully!");
                    loadOfficers();
                } else {
                    AlertUtil.showError("Error", "Could not add officer. Username may already exist.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditOfficer() {
        TechnicalOfficer selected = tblOfficers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an officer to edit.");
            return;
        }

        Dialog<TechnicalOfficer> dialog = new Dialog<>();
        dialog.setTitle("Edit Technical Officer");
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

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);

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

                boolean updated = officerDAO.updateOfficer(selected);
                if (updated) {
                    AlertUtil.showInfo("Success", "Technical Officer updated successfully!");
                    loadOfficers();
                } else {
                    AlertUtil.showError("Error", "Could not update officer details.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteOfficer() {
        TechnicalOfficer selected = tblOfficers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an officer to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete: " + selected.getFullName() + "?"
        );

        if (confirm) {
            boolean deleted = officerDAO.deleteOfficer(selected.getUserId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Officer profile deleted.");
                loadOfficers();
            } else {
                AlertUtil.showError("Error", "Could not delete officer.");
            }
        }
    }
}
