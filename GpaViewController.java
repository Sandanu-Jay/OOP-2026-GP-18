package com.faculty.management.controller;

import com.faculty.management.dao.MarksDAO;
import com.faculty.management.model.Marks;
import com.faculty.management.model.User;
import com.faculty.management.util.GradeCalculator;
import com.faculty.management.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for Undergraduate GPA & UGC Circular No. 12-2024 Grade Breakdown.
 */
public class GpaViewController {

    @FXML private Label lblCgpa;
    @FXML private Label lblTotalCredits;
    @FXML private Label lblEarnedPoints;

    @FXML private TableView<Marks> tblCourseGrades;
    @FXML private TableColumn<Marks, String> colCode;
    @FXML private TableColumn<Marks, String> colName;
    @FXML private TableColumn<Marks, Integer> colCredits;
    @FXML private TableColumn<Marks, Double> colTotalMarks;
    @FXML private TableColumn<Marks, String> colGrade;
    @FXML private TableColumn<Marks, Double> colGpv;
    @FXML private TableColumn<Marks, String> colPoints;
    @FXML private TableColumn<Marks, String> colDesc;

    private final MarksDAO marksDAO = new MarksDAO();
    private final ObservableList<Marks> marksList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credit"));
        colTotalMarks.setCellValueFactory(new PropertyValueFactory<>("totalMark"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colGpv.setCellValueFactory(new PropertyValueFactory<>("gpv"));
        colPoints.setCellValueFactory(cell -> {
            double p = cell.getValue().getGpv() * cell.getValue().getCredit();
            return new SimpleStringProperty(String.format("%.2f", p));
        });
        colDesc.setCellValueFactory(cell -> new SimpleStringProperty(GradeCalculator.getDescription(cell.getValue().getGrade())));

        User user = SessionManager.getCurrentUser();
        if (user != null) {
            loadGpaData(user.getUserId());
        }
    }

    public void loadGpaData(int studentId) {
        marksList.clear();
        var list = marksDAO.getMarksByStudent(studentId);
        marksList.addAll(list);
        tblCourseGrades.setItems(marksList);

        int totalCredits = 0;
        double totalPoints = 0.0;

        for (Marks m : list) {
            if (m.getGrade() != null && !"Pending".equalsIgnoreCase(m.getGrade())) {
                totalCredits += m.getCredit();
                totalPoints += (m.getGpv() * m.getCredit());
            }
        }

        double gpa = (totalCredits > 0) ? (totalPoints / totalCredits) : 0.00;
        lblCgpa.setText(String.format("%.2f", gpa));
        lblTotalCredits.setText(String.valueOf(totalCredits));
        lblEarnedPoints.setText(String.format("%.2f", totalPoints));
    }
}
