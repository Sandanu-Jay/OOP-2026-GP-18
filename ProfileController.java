package com.faculty.management.controller;

import com.faculty.management.dao.UserDAO;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Student;
import com.faculty.management.model.TechnicalOfficer;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SessionManager;
import com.faculty.management.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Controller for the User Profile screen.
 * Implements strict role-based editing restrictions (Students can only edit contact details).
 */
public class ProfileController {

    @FXML private ImageView imgProfilePhoto;
    @FXML private Label lblAvatarPlaceholder;
    @FXML private Button btnChangePhoto;

    @FXML private Label lblFullNameHeader;
    @FXML private Label lblRoleBadge;
    @FXML private Label lblUsernameDisplay;
    
    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtAddress;
    
    @FXML private VBox studentDetailsBox;
    @FXML private TextField txtRegNo;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtBatch;
    @FXML private TextField txtStudentStatus;

    @FXML private VBox staffDetailsBox;
    @FXML private TextField txtStaffDepartment;
    @FXML private TextField txtStaffDesignation;

    @FXML private Button btnSave;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        lblFullNameHeader.setText(currentUser.getFullName());
        lblRoleBadge.setText(currentUser.getRoleDisplayName());
        lblUsernameDisplay.setText("@" + currentUser.getUsername());

        txtFullName.setText(currentUser.getFullName());
        txtUsername.setText(currentUser.getUsername());
        txtEmail.setText(currentUser.getEmail());
        txtPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        txtAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");

        loadProfilePhoto();

        // Username is NEVER editable from normal profile update
        txtUsername.setEditable(false);
        txtUsername.setStyle("-fx-background-color: #F1F5F9;");

        // Role-based field restrictions
        if (currentUser instanceof Student) {
            Student s = (Student) currentUser;
            studentDetailsBox.setVisible(true);
            studentDetailsBox.setManaged(true);
            staffDetailsBox.setVisible(false);
            staffDetailsBox.setManaged(false);

            txtRegNo.setText(s.getRegNo());
            txtDepartment.setText(s.getDepartment());
            txtBatch.setText(s.getBatch());
            txtStudentStatus.setText(s.getStudentStatus());

            // Full name, Reg No, Dept, Batch, Status are read-only for students
            txtFullName.setEditable(false);
            txtFullName.setStyle("-fx-background-color: #F1F5F9;");
            txtRegNo.setEditable(false);
            txtDepartment.setEditable(false);
            txtBatch.setEditable(false);
            txtStudentStatus.setEditable(false);
        } else if (currentUser instanceof Lecturer) {
            Lecturer l = (Lecturer) currentUser;
            studentDetailsBox.setVisible(false);
            studentDetailsBox.setManaged(false);
            staffDetailsBox.setVisible(true);
            staffDetailsBox.setManaged(true);

            txtStaffDepartment.setText(l.getDepartment());
            txtStaffDesignation.setText(l.getDesignation());
            txtStaffDepartment.setEditable(false);
            txtStaffDesignation.setEditable(false);
        } else if (currentUser instanceof TechnicalOfficer) {
            TechnicalOfficer to = (TechnicalOfficer) currentUser;
            studentDetailsBox.setVisible(false);
            studentDetailsBox.setManaged(false);
            staffDetailsBox.setVisible(true);
            staffDetailsBox.setManaged(true);

            txtStaffDepartment.setText(to.getDepartment());
            txtStaffDesignation.setText("Technical Officer");
            txtStaffDepartment.setEditable(false);
            txtStaffDesignation.setEditable(false);
        } else {
            // Admin
            studentDetailsBox.setVisible(false);
            studentDetailsBox.setManaged(false);
            staffDetailsBox.setVisible(false);
            staffDetailsBox.setManaged(false);
        }
    }

    private void loadProfilePhoto() {
        if (currentUser == null || imgProfilePhoto == null) return;
        String imagePath = currentUser.getProfileImage();
        if (imagePath != null && !imagePath.isEmpty() && !"default_avatar.png".equalsIgnoreCase(imagePath)) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    Image img = new Image(file.toURI().toString());
                    imgProfilePhoto.setImage(img);
                    clipCircular(imgProfilePhoto, 38);
                    imgProfilePhoto.setVisible(true);
                    lblAvatarPlaceholder.setVisible(false);
                    return;
                }
            } catch (Exception e) {
                System.err.println("Could not load user avatar: " + e.getMessage());
            }
        }
        imgProfilePhoto.setVisible(false);
        lblAvatarPlaceholder.setVisible(true);
    }

    private void clipCircular(ImageView iv, double radius) {
        Circle clip = new Circle(radius, radius, radius);
        iv.setClip(clip);
    }

    @FXML
    private void handleChangePhoto() {
        if (currentUser == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        Window window = btnChangePhoto.getScene() != null ? btnChangePhoto.getScene().getWindow() : null;
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile == null) return;

        try {
            // Ensure uploads/profiles directory exists
            File uploadDir = new File("uploads/profiles");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique target filename
            String ext = "";
            int dot = selectedFile.getName().lastIndexOf('.');
            if (dot >= 0) ext = selectedFile.getName().substring(dot);
            String destFilename = "user_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ext;
            File destFile = new File(uploadDir, destFilename);

            // Copy file
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String savedPath = destFile.getPath();
            boolean updated = userDAO.updateProfileImage(currentUser.getUserId(), savedPath);
            if (updated) {
                currentUser.setProfileImage(savedPath);
                loadProfilePhoto();
                AlertUtil.showInfo("Photo Updated", "Your profile photo has been successfully updated!");
            } else {
                AlertUtil.showError("Error", "Could not save profile picture in database.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to upload photo: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveProfile() {
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress != null ? txtAddress.getText().trim() : "";

        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showWarning("Validation Error", "Please provide a valid email address (e.g. name@domain.com).");
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            AlertUtil.showWarning("Validation Error", "Please provide a valid phone number.");
            return;
        }

        boolean success = userDAO.updateContactAndProfilePicture(
            currentUser.getUserId(), 
            email, 
            phone, 
            address,
            currentUser.getProfileImage()
        );

        if (success) {
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            AlertUtil.showInfo("Success", "Profile updated successfully!");
        } else {
            AlertUtil.showError("Error", "Failed to update profile. Please try again.");
        }
    }
}
