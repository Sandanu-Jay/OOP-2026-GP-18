/*
 * This JavaFX controller handles the CourseManagement screen.
 * Explain it using: initialize -> user action -> validation -> DAO -> UI update.
 */
package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.DepartmentDAO;
import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;
import com.faculty.management.model.Lecturer;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Desktop;

/**
 * Controller for Courses and Course Materials Management.
 * Dynamically adapts UI permissions based on user role (Admin, Lecturer, Student).
 */
public class CourseManagementController {

    @FXML private TableView<Course> tblCourses;
    @FXML private TableColumn<Course, String> colCourseCode;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, Integer> colCredit;
    @FXML private TableColumn<Course, String> colDepartment;
    @FXML private TableColumn<Course, Integer> colSemester;
    @FXML private TableColumn<Course, Integer> colLevel;
    @FXML private TableColumn<Course, String> colLecturer;

    @FXML private HBox adminControls;
    @FXML private Button btnAddCourse;
    @FXML private Button btnEditCourse;
    @FXML private Button btnDeleteCourse;

    @FXML private HBox studentControls;
    @FXML private Button btnRegisterCourse;
    @FXML private Button btnDropCourse;

    @FXML private VBox materialsSection;
    @FXML private Label lblSelectedCourse;
    @FXML private TableView<CourseMaterial> tblMaterials;
    @FXML private TableColumn<CourseMaterial, String> colMaterialTitle;
    @FXML private TableColumn<CourseMaterial, String> colMaterialFile;
    @FXML private TableColumn<CourseMaterial, String> colMaterialDate;
    @FXML private HBox lecturerMaterialControls;
    @FXML private Button btnUploadMaterial;
    @FXML private Button btnDeleteMaterial;
    @FXML private Button btnOpenMaterial;





    private final CourseDAO courseDAO = new CourseDAO();
    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<CourseMaterial> materialList = FXCollections.observableArrayList();
    private boolean lecturerView;
    private boolean studentView;

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        if (colLevel != null) colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));



        User user = SessionManager.getCurrentUser();
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());
        lecturerView = user != null && "LECTURER".equalsIgnoreCase(user.getRole());
        studentView = user != null && "STUDENT".equalsIgnoreCase(user.getRole());

        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);


        studentControls.setVisible(studentView);
        studentControls.setManaged(studentView);

        // Admins do not see learning materials. Lecturers upload; students view.
        boolean showMaterials = lecturerView || studentView;
        materialsSection.setVisible(showMaterials);
        materialsSection.setManaged(showMaterials);
        lecturerMaterialControls.setVisible(lecturerView);
        lecturerMaterialControls.setManaged(lecturerView);
        btnOpenMaterial.setVisible(studentView);
        btnOpenMaterial.setManaged(studentView);

        colMaterialTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMaterialFile.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colMaterialDate.setCellValueFactory(new PropertyValueFactory<>("uploadedDate"));
        tblMaterials.setItems(materialList);

        tblCourses.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            loadMaterialsForSelectedCourse();
        });

        loadCourses();
    }

    public void loadCourses() {
        courseList.clear();
        User user = SessionManager.getCurrentUser();
        if (user != null && "LECTURER".equalsIgnoreCase(user.getRole())) {
            courseList.addAll(courseDAO.getCoursesByLecturer(user.getUserId()));
        } else if (user != null && "STUDENT".equalsIgnoreCase(user.getRole())) {
            courseList.addAll(courseDAO.getCoursesByStudent(user.getUserId()));
        } else {
            courseList.addAll(courseDAO.getAllCourses());
        }
        tblCourses.setItems(courseList);
        if (!courseList.isEmpty()) {
            tblCourses.getSelectionModel().selectFirst();
        } else {
            materialList.clear();
            lblSelectedCourse.setText("Select a course to view its learning materials.");
        }
        loadMaterialsForSelectedCourse();
    }

    private void loadMaterialsForSelectedCourse() {
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        materialList.clear();
        if (selected == null || (!lecturerView && !studentView)) {
            lblSelectedCourse.setText("Select a course to view its learning materials.");
            return;
        }
        lblSelectedCourse.setText(selected.getCourseCode() + " - " + selected.getCourseName());
        materialList.addAll(courseDAO.getMaterialsByCourse(selected.getCourseId()));
    }

    @FXML
    private void handleUploadMaterial() {
        User user = SessionManager.getCurrentUser();
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        if (user == null || !lecturerView || selected == null) {
            AlertUtil.showWarning("Select Course", "Please select one of your assigned courses first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Course Material File");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Learning files", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.xls", "*.xlsx", "*.txt", "*.zip", "*.jpg", "*.png"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
        Window window = tblCourses.getScene() == null ? null : tblCourses.getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file == null) return;

        try {
            long maxBytes = 20L * 1024 * 1024;
            if (file.length() > maxBytes) {
                AlertUtil.showWarning("File Too Large", "Please choose a file smaller than 20 MB.");
                return;
            }
            CourseMaterial material = new CourseMaterial();
            material.setCourseId(selected.getCourseId());
            material.setTitle(file.getName());
            material.setDescription("");
            String extension = "";
            int dot = file.getName().lastIndexOf('.');
            if (dot >= 0) extension = file.getName().substring(dot + 1).toUpperCase();
            material.setFileType(extension);
            material.setFileName(file.getName());
            material.setFileData(Files.readAllBytes(file.toPath()));

            if (courseDAO.addCourseMaterial(material, user.getUserId())) {
                AlertUtil.showInfo("Upload Complete", "The file has been added to " + selected.getCourseCode() + ".");
                loadMaterialsForSelectedCourse();
            } else {
                AlertUtil.showError("Upload Failed", "The file could not be saved. Check that this course is assigned to your lecturer account and the database migration is applied.");
            }
        } catch (IOException ex) {
            AlertUtil.showError("File Error", "Could not read the selected file: " + ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteMaterial() {
        User user = SessionManager.getCurrentUser();
        CourseMaterial selected = tblMaterials.getSelectionModel().getSelectedItem();
        if (user == null || !lecturerView || selected == null) {
            AlertUtil.showWarning("Select File", "Please select a material to delete.");
            return;
        }
        if (!AlertUtil.showConfirmation("Delete Material", "Delete " + selected.getFileName() + "?")) return;
        if (courseDAO.deleteCourseMaterial(selected.getMaterialId(), user.getUserId())) {
            AlertUtil.showInfo("Deleted", "Course material deleted successfully.");
            loadMaterialsForSelectedCourse();
        } else {
            AlertUtil.showError("Delete Failed", "The material could not be deleted.");
        }
    }

    @FXML
    private void handleOpenMaterial() {
        User user = SessionManager.getCurrentUser();
        CourseMaterial selected = tblMaterials.getSelectionModel().getSelectedItem();
        if (user == null || !studentView || selected == null) {
            AlertUtil.showWarning("Select File", "Please select a material to open.");
            return;
        }
        byte[] data = courseDAO.getCourseMaterialFile(selected.getMaterialId(), user.getUserId());
        if (data == null) {
            AlertUtil.showError("File Unavailable", "This file is unavailable or you are not enrolled in the course.");
            return;
        }
        try {
            String safeName = Path.of(selected.getFileName()).getFileName().toString();
            String suffix = safeName.contains(".") ? safeName.substring(safeName.lastIndexOf('.')) : ".dat";
            Path tempFile = Files.createTempFile("course-material-", suffix);
            Files.write(tempFile, data);
            tempFile.toFile().deleteOnExit();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile.toFile());
            } else {
                AlertUtil.showInfo("File Saved", "The file was saved temporarily at: " + tempFile);
            }
        } catch (Exception ex) {
            AlertUtil.showError("Open Failed", "Could not open this file: " + ex.getMessage());
        }
    }

    @FXML
    private void handleAddCourse() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Create New Course");
        dialog.setHeaderText("Course Catalog Entry");

        ButtonType saveBtn = new ButtonType("Create Course", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField codeField = new TextField();
        codeField.setPromptText("ICT2105");
        TextField nameField = new TextField();
        Spinner<Integer> creditSpinner = new Spinner<>(1, 6, 3);
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll(DepartmentDAO.STANDARD_DEPARTMENTS);
        deptCombo.setValue(DepartmentDAO.STANDARD_DEPARTMENTS.get(0));

        Spinner<Integer> levelSpinner = new Spinner<>(1, 4, 1);
        Spinner<Integer> semSpinner = new Spinner<>(1, 2, 1);
        ComboBox<Lecturer> lecCombo = new ComboBox<>();
        lecCombo.getItems().addAll(lecturerDAO.getAllLecturers());

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(creditSpinner, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);
        grid.add(new Label("Level:"), 0, 4);
        grid.add(levelSpinner, 1, 4);
        grid.add(new Label("Semester:"), 0, 5);
        grid.add(semSpinner, 1, 5);
        grid.add(new Label("Assigned Lecturer:"), 0, 6);
        grid.add(lecCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (!ValidationUtil.isNotEmpty(codeField.getText()) || !ValidationUtil.isNotEmpty(nameField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide Course Code and Course Name.");
                    return null;
                }

                Course c = new Course();
                c.setCourseCode(codeField.getText().trim().toUpperCase());
                c.setCourseName(nameField.getText().trim());
                c.setCredit(creditSpinner.getValue());
                c.setDepartment(deptCombo.getValue());
                c.setLevel(levelSpinner.getValue());
                c.setSemester(semSpinner.getValue());
                if (lecCombo.getValue() != null) {
                    c.setLecturerId(lecCombo.getValue().getUserId());
                }

                boolean added = courseDAO.addCourse(c);
                if (added) {
                    AlertUtil.showInfo("Success", "Course created successfully!");
                    loadCourses();
                } else {
                    AlertUtil.showError("Error", "Could not create course. Course code may already exist.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditCourse() {
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a course to edit.");
            return;
        }

        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Modifying: " + selected.getCourseCode());

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField codeField = new TextField(selected.getCourseCode());
        TextField nameField = new TextField(selected.getCourseName());
        Spinner<Integer> creditSpinner = new Spinner<>(1, 6, selected.getCredit());
        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll(DepartmentDAO.STANDARD_DEPARTMENTS);
        deptCombo.setValue(selected.getDepartment());

        Spinner<Integer> levelSpinner = new Spinner<>(1, 4, selected.getLevel());
        Spinner<Integer> semSpinner = new Spinner<>(1, 2, selected.getSemester());
        ComboBox<Lecturer> lecCombo = new ComboBox<>();
        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
        lecCombo.getItems().addAll(lecturers);
        for (Lecturer l : lecturers) {
            if (selected.getLecturerId() != null && l.getUserId() == selected.getLecturerId()) {
                lecCombo.setValue(l);
                break;
            }
        }

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(creditSpinner, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);
        grid.add(new Label("Level:"), 0, 4);
        grid.add(levelSpinner, 1, 4);
        grid.add(new Label("Semester:"), 0, 5);
        grid.add(semSpinner, 1, 5);
        grid.add(new Label("Assigned Lecturer:"), 0, 6);
        grid.add(lecCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setCourseCode(codeField.getText().trim().toUpperCase());
                selected.setCourseName(nameField.getText().trim());
                selected.setCredit(creditSpinner.getValue());
                selected.setDepartment(deptCombo.getValue());
                selected.setLevel(levelSpinner.getValue());
                selected.setSemester(semSpinner.getValue());
                if (lecCombo.getValue() != null) {
                    selected.setLecturerId(lecCombo.getValue().getUserId());
                } else {
                    selected.setLecturerId(null);
                }

                boolean updated = courseDAO.updateCourse(selected);
                if (updated) {
                    AlertUtil.showInfo("Success", "Course updated successfully!");
                    loadCourses();
                } else {
                    AlertUtil.showError("Error", "Could not update course.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteCourse() {
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a course to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
            "Confirm Delete", 
            "Are you sure you want to delete: " + selected.getCourseCode() + " - " + selected.getCourseName() + "?"
        );

        if (confirm) {
            boolean deleted = courseDAO.deleteCourse(selected.getCourseId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Course removed.");
                loadCourses();
            } else {
                AlertUtil.showError("Error", "Could not delete course.");
            }
        }
    }

    @FXML
    private void handleRegisterCourse() {
        User user = SessionManager.getCurrentUser();
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            AlertUtil.showWarning("Permission Denied", "Only undergraduates can register for courses.");
            return;
        }

        List<Course> available = courseDAO.getAvailableCoursesForStudent(user.getUserId());
        if (available.isEmpty()) {
            AlertUtil.showInfo("No Courses Available", "You are already enrolled in all available faculty courses!");
            return;
        }

        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("Course Registration");
        dialog.setHeaderText("Register for an Academic Course Module");

        ButtonType regBtn = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(regBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(available);
        courseCombo.getSelectionModel().selectFirst();
        courseCombo.setPrefWidth(350);

        TextField academicYearField = new TextField("2023/2024");
        academicYearField.setPromptText("e.g. 2023/2024");

        Label lblDetails = new Label();
        lblDetails.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-wrap-text: true;");
        lblDetails.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        courseCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                lblDetails.setText(String.format("Credits: %d  |  Level: %d  |  Semester: %d  |  Dept: %s",
                        newV.getCredit(), newV.getLevel(), newV.getSemester(), newV.getDepartment()));
            }
        });
        if (courseCombo.getValue() != null) {
            lblDetails.setText(String.format("Credits: %d  |  Level: %d  |  Semester: %d  |  Dept: %s",
                    courseCombo.getValue().getCredit(), courseCombo.getValue().getLevel(), courseCombo.getValue().getSemester(), courseCombo.getValue().getDepartment()));
        }

        grid.add(new Label("Select Course:"), 0, 0);
        grid.add(courseCombo, 1, 0);
        grid.add(new Label("Course Info:"), 0, 1);
        grid.add(lblDetails, 1, 1);
        grid.add(new Label("Academic Year:"), 0, 2);
        grid.add(academicYearField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == regBtn) {
                Course sel = courseCombo.getValue();
                String year = academicYearField.getText().trim();
                if (sel != null) {
                    boolean success = courseDAO.enrollStudent(user.getUserId(), sel.getCourseId(), year);
                    if (success) {
                        AlertUtil.showInfo("Enrolled Successfully", "You have successfully registered for " + sel.getCourseCode() + " - " + sel.getCourseName());
                        loadCourses();
                    } else {
                        AlertUtil.showError("Enrollment Failed", "Could not complete course registration. Please try again.");
                    }
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDropCourse() {
        User user = SessionManager.getCurrentUser();
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return;
        }
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an enrolled course to drop.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation(
            "Drop Course Confirmation",
            "Are you sure you want to drop course: " + selected.getCourseCode() + " (" + selected.getCourseName() + ")?"
        );
        if (confirm) {
            boolean success = courseDAO.dropCourse(user.getUserId(), selected.getCourseId());
            if (success) {
                AlertUtil.showInfo("Success", "Course dropped successfully.");
                loadCourses();
            } else {
                AlertUtil.showError("Error", "Failed to drop the course.");
            }
        }
    }





}
