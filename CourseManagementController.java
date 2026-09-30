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

import java.time.LocalDateTime;
import java.util.List;

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
    @FXML private TableColumn<Course, String> colLecturer;

    @FXML private HBox adminControls;
    @FXML private Button btnAddCourse;
    @FXML private Button btnEditCourse;
    @FXML private Button btnDeleteCourse;

    @FXML private HBox studentControls;
    @FXML private Button btnRegisterCourse;
    @FXML private Button btnDropCourse;

    @FXML private TableView<CourseMaterial> tblMaterials;
    @FXML private TableColumn<CourseMaterial, String> colMaterialTitle;
    @FXML private TableColumn<CourseMaterial, String> colMaterialDesc;
    @FXML private TableColumn<CourseMaterial, String> colMaterialType;
    @FXML private TableColumn<CourseMaterial, LocalDateTime> colMaterialDate;

    @FXML private HBox materialControls;
    @FXML private Button btnAddMaterial;
    @FXML private Button btnDeleteMaterial;
    @FXML private Label lblSelectedCourse;

    private final CourseDAO courseDAO = new CourseDAO();
    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<CourseMaterial> materialList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));

        colMaterialTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colMaterialDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMaterialType.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        colMaterialDate.setCellValueFactory(new PropertyValueFactory<>("uploadedDate"));

        User user = SessionManager.getCurrentUser();
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());
        boolean isLecturer = user != null && "LECTURER".equalsIgnoreCase(user.getRole());
        boolean isStudent = user != null && "STUDENT".equalsIgnoreCase(user.getRole());

        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);

        studentControls.setVisible(isStudent);
        studentControls.setManaged(isStudent);

        materialControls.setVisible(isAdmin || isLecturer);
        materialControls.setManaged(isAdmin || isLecturer);

        tblCourses.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                lblSelectedCourse.setText(newSel.getCourseCode() + " - " + newSel.getCourseName());
                loadMaterials(newSel.getCourseId());
            } else {
                lblSelectedCourse.setText("Select a course to view materials");
                materialList.clear();
            }
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
        }
    }

    private void loadMaterials(int courseId) {
        materialList.clear();
        materialList.addAll(courseDAO.getMaterialsByCourse(courseId));
        tblMaterials.setItems(materialList);
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

        Spinner<Integer> semSpinner = new Spinner<>(1, 8, 1);
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
        grid.add(new Label("Semester:"), 0, 4);
        grid.add(semSpinner, 1, 4);
        grid.add(new Label("Assigned Lecturer:"), 0, 5);
        grid.add(lecCombo, 1, 5);

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

        Spinner<Integer> semSpinner = new Spinner<>(1, 8, selected.getSemester());
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
        grid.add(new Label("Semester:"), 0, 4);
        grid.add(semSpinner, 1, 4);
        grid.add(new Label("Assigned Lecturer:"), 0, 5);
        grid.add(lecCombo, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setCourseCode(codeField.getText().trim().toUpperCase());
                selected.setCourseName(nameField.getText().trim());
                selected.setCredit(creditSpinner.getValue());
                selected.setDepartment(deptCombo.getValue());
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
        lblDetails.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold;");

        courseCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                lblDetails.setText(String.format("Credits: %d  |  Semester: %d  |  Dept: %s",
                        newV.getCredit(), newV.getSemester(), newV.getDepartment()));
            }
        });
        if (courseCombo.getValue() != null) {
            lblDetails.setText(String.format("Credits: %d  |  Semester: %d  |  Dept: %s",
                    courseCombo.getValue().getCredit(), courseCombo.getValue().getSemester(), courseCombo.getValue().getDepartment()));
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

    @FXML
    private void handleAddMaterial() {
        Course selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a course before adding materials.");
            return;
        }

        Dialog<CourseMaterial> dialog = new Dialog<>();
        dialog.setTitle("Upload Course Material");
        dialog.setHeaderText("Add Learning Resource to: " + selected.getCourseCode());

        ButtonType uploadBtn = new ButtonType("Add Material", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(uploadBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField titleField = new TextField();
        titleField.setPromptText("e.g. Lecture 04 - Polymorphism & Interfaces");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Description / Topic summary");
        descArea.setPrefRowCount(3);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("PDF Lecture Slides", "Lab Sheet", "Assignment Brief", "Reference Document", "Source Code Zip");
        typeCombo.setValue("PDF Lecture Slides");
        TextField pathField = new TextField("materials/" + selected.getCourseCode().toLowerCase() + "_doc.pdf");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Resource Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descArea, 1, 2);
        grid.add(new Label("File Path / URL:"), 0, 3);
        grid.add(pathField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == uploadBtn) {
                if (!ValidationUtil.isNotEmpty(titleField.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide a title for the material.");
                    return null;
                }

                CourseMaterial mat = new CourseMaterial();
                mat.setCourseId(selected.getCourseId());
                mat.setTitle(titleField.getText().trim());
                mat.setDescription(descArea.getText().trim());
                mat.setFileType(typeCombo.getValue());
                mat.setFilePathOrUrl(pathField.getText().trim());

                boolean added = courseDAO.addCourseMaterial(mat);
                if (added) {
                    AlertUtil.showInfo("Success", "Course material added!");
                    loadMaterials(selected.getCourseId());
                } else {
                    AlertUtil.showError("Error", "Could not add material.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteMaterial() {
        CourseMaterial selectedMat = tblMaterials.getSelectionModel().getSelectedItem();
        if (selectedMat == null) {
            AlertUtil.showWarning("No Selection", "Please select a material item to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Delete material: " + selectedMat.getTitle() + "?");
        if (confirm) {
            boolean deleted = courseDAO.deleteCourseMaterial(selectedMat.getMaterialId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Material removed.");
                loadMaterials(selectedMat.getCourseId());
            } else {
                AlertUtil.showError("Error", "Could not delete material.");
            }
        }
    }
}
