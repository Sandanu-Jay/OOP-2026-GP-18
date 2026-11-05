/*
 * This JavaFX controller handles the TimetableManagement screen.
 * Explain it using: initialize -> user action -> validation -> DAO -> UI update.
 */
package com.faculty.management.controller;

import com.faculty.management.dao.CourseDAO;
import com.faculty.management.dao.LecturerDAO;
import com.faculty.management.dao.TimetableDAO;
import com.faculty.management.model.Course;
import com.faculty.management.model.Lecturer;
import com.faculty.management.model.Timetable;
import com.faculty.management.model.User;
import com.faculty.management.util.AlertUtil;
import com.faculty.management.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalTime;
import java.util.List;

/**
 * Controller for Timetables and Room Schedules.
 */
public class TimetableManagementController {

    @FXML private TableView<Timetable> tblTimetables;
    @FXML private TableColumn<Timetable, String> colDay;
    @FXML private TableColumn<Timetable, String> colTime;
    @FXML private TableColumn<Timetable, String> colCourseCode;
    @FXML private TableColumn<Timetable, String> colCourseName;
    @FXML private TableColumn<Timetable, String> colVenue;
    @FXML private TableColumn<Timetable, String> colLecturer;
    @FXML private TableColumn<Timetable, String> colDepartment;

    @FXML private HBox adminControls;
    @FXML private Button btnAddSlot;
    @FXML private Button btnEditSlot;
    @FXML private Button btnDeleteSlot;
    @FXML private Button btnRefresh;

    private final TimetableDAO timetableDAO = new TimetableDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final LecturerDAO lecturerDAO = new LecturerDAO();
    private final ObservableList<Timetable> timetableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colTime.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTimeSlot()));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));

        User user = SessionManager.getCurrentUser();
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);

        loadTimetables();
    }

    public void loadTimetables() {
        timetableList.clear();
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            switch (user.getRole().toUpperCase()) {
                case "LECTURER":
                    timetableList.addAll(timetableDAO.getTimetablesByLecturer(user.getUserId()));
                    break;
                case "STUDENT":
                    timetableList.addAll(timetableDAO.getTimetablesForStudent(user.getUserId()));
                    break;
                default:
                    timetableList.addAll(timetableDAO.getAllTimetables());
                    break;
            }
        } else {
            timetableList.addAll(timetableDAO.getAllTimetables());
        }
        tblTimetables.setItems(timetableList);
    }

    @FXML
    private void handleAddSlot() {
        Dialog<Timetable> dialog = new Dialog<>();
        dialog.setTitle("Add Timetable Entry");
        dialog.setHeaderText("Schedule Lecture / Practical Session");

        ButtonType saveBtn = new ButtonType("Add to Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue("Department of Computer Science");

        Spinner<Integer> semSpinner = new Spinner<>(1, 8, 1);
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        dayCombo.setValue("Monday");

        TextField startTimeField = new TextField("08:30");
        TextField endTimeField = new TextField("11:30");
        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.getItems().addAll(courseDAO.getAllCourses());
        if (!courseCombo.getItems().isEmpty()) courseCombo.getSelectionModel().selectFirst();

        TextField venueField = new TextField("Lecture Theatre 01");
        ComboBox<Lecturer> lecCombo = new ComboBox<>();
        lecCombo.getItems().addAll(lecturerDAO.getAllLecturers());

        grid.add(new Label("Department:"), 0, 0);
        grid.add(deptCombo, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semSpinner, 1, 1);
        grid.add(new Label("Day:"), 0, 2);
        grid.add(dayCombo, 1, 2);
        grid.add(new Label("Start Time (HH:mm):"), 0, 3);
        grid.add(startTimeField, 1, 3);
        grid.add(new Label("End Time (HH:mm):"), 0, 4);
        grid.add(endTimeField, 1, 4);
        grid.add(new Label("Course:"), 0, 5);
        grid.add(courseCombo, 1, 5);
        grid.add(new Label("Venue / Room:"), 0, 6);
        grid.add(venueField, 1, 6);
        grid.add(new Label("Lecturer:"), 0, 7);
        grid.add(lecCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    LocalTime start = LocalTime.parse(startTimeField.getText().trim());
                    LocalTime end = LocalTime.parse(endTimeField.getText().trim());

                    Timetable t = new Timetable();
                    t.setDepartment(deptCombo.getValue());
                    t.setSemester(semSpinner.getValue());
                    t.setDayOfWeek(dayCombo.getValue());
                    t.setStartTime(start);
                    t.setEndTime(end);
                    t.setCourseId(courseCombo.getValue().getCourseId());
                    t.setVenue(venueField.getText().trim());
                    if (lecCombo.getValue() != null) {
                        t.setLecturerId(lecCombo.getValue().getUserId());
                    }

                    boolean added = timetableDAO.addTimetableEntry(t);
                    if (added) {
                        AlertUtil.showInfo("Success", "Timetable slot added!");
                        loadTimetables();
                    } else {
                        AlertUtil.showError("Error", "Could not add timetable entry.");
                    }
                } catch (Exception ex) {
                    AlertUtil.showWarning("Time Format Error", "Please enter time in HH:mm 24-hour format (e.g. 08:30, 13:30).");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditSlot() {
        Timetable selected = tblTimetables.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a timetable slot to edit.");
            return;
        }

        Dialog<Timetable> dialog = new Dialog<>();
        dialog.setTitle("Edit Timetable Slot");
        dialog.setHeaderText("Updating: " + selected.getCourseCode() + " (" + selected.getDayOfWeek() + ")");

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Department of Computer Science", "Department of Information Technology", "Department of Engineering Technology");
        deptCombo.setValue(selected.getDepartment());

        Spinner<Integer> semSpinner = new Spinner<>(1, 8, selected.getSemester());
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        dayCombo.setValue(selected.getDayOfWeek());

        TextField startTimeField = new TextField(selected.getStartTime().toString());
        TextField endTimeField = new TextField(selected.getEndTime().toString());
        TextField venueField = new TextField(selected.getVenue());

        grid.add(new Label("Department:"), 0, 0);
        grid.add(deptCombo, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semSpinner, 1, 1);
        grid.add(new Label("Day:"), 0, 2);
        grid.add(dayCombo, 1, 2);
        grid.add(new Label("Start Time (HH:mm):"), 0, 3);
        grid.add(startTimeField, 1, 3);
        grid.add(new Label("End Time (HH:mm):"), 0, 4);
        grid.add(endTimeField, 1, 4);
        grid.add(new Label("Venue / Room:"), 0, 5);
        grid.add(venueField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    selected.setDepartment(deptCombo.getValue());
                    selected.setSemester(semSpinner.getValue());
                    selected.setDayOfWeek(dayCombo.getValue());
                    selected.setStartTime(LocalTime.parse(startTimeField.getText().trim()));
                    selected.setEndTime(LocalTime.parse(endTimeField.getText().trim()));
                    selected.setVenue(venueField.getText().trim());

                    boolean updated = timetableDAO.updateTimetableEntry(selected);
                    if (updated) {
                        AlertUtil.showInfo("Success", "Timetable updated!");
                        loadTimetables();
                    } else {
                        AlertUtil.showError("Error", "Could not update entry.");
                    }
                } catch (Exception ex) {
                    AlertUtil.showWarning("Input Error", "Please ensure valid HH:mm time format.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteSlot() {
        Timetable selected = tblTimetables.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a timetable slot to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Remove timetable slot for " + selected.getCourseCode() + "?");
        if (confirm) {
            boolean deleted = timetableDAO.deleteTimetableEntry(selected.getTimetableId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Slot removed.");
                loadTimetables();
            } else {
                AlertUtil.showError("Error", "Could not delete entry.");
            }
        }
    }
}
