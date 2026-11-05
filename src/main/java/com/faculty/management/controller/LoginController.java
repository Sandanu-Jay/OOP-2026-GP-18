/*
 * This JavaFX controller handles the Login screen.
 * Explain it using: initialize -> user action -> validation -> DAO -> UI update.
 */
package com.faculty.management.controller;

import com.faculty.management.dao.UserDAO;
import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.User;
import com.faculty.management.util.SceneManager;
import com.faculty.management.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Login screen.
 * Authenticates user credentials and redirects dynamically based on user role.
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        lblError.setText("");
        // Bind visible textfield and passwordfield
        txtPasswordVisible.setManaged(false);
        txtPasswordVisible.setVisible(false);

        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());

        chkShowPassword.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtPasswordVisible.setVisible(true);
                txtPasswordVisible.setManaged(true);
                txtPassword.setVisible(false);
                txtPassword.setManaged(false);
            } else {
                txtPasswordVisible.setVisible(false);
                txtPasswordVisible.setManaged(false);
                txtPassword.setVisible(true);
                txtPassword.setManaged(true);
            }
        });
    }

    @FXML
    private void handleLogin() {
        lblError.setText("");
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        // Test database availability first to provide helpful error if MySQL is stopped
        if (!DatabaseConnection.testConnection()) {
            lblError.setText("Database unreachable. Ensure MySQL is running on localhost:3306.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            lblError.setText("Invalid username or password. Please try again.");
            return;
        }

        // Store session
        SessionManager.setCurrentUser(user);

        // Redirect based on Polymorphic Role
        switch (user.getRole()) {
            case "ADMIN":
                SceneManager.switchScene("/fxml/admin_dashboard.fxml", "Admin Dashboard - Faculty Management System");
                break;
            case "LECTURER":
                SceneManager.switchScene("/fxml/lecturer_dashboard.fxml", "Lecturer Dashboard - Faculty Management System");
                break;
            case "TECHNICAL_OFFICER":
                SceneManager.switchScene("/fxml/technical_officer_dashboard.fxml", "Technical Officer Dashboard - Faculty Management System");
                break;
            case "STUDENT":
                SceneManager.switchScene("/fxml/student_dashboard.fxml", "Student Portal - Faculty Management System");
                break;
            default:
                lblError.setText("Unrecognized role: " + user.getRole());
        }
    }
}
