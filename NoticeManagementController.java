package com.faculty.management.controller;

import com.faculty.management.dao.NoticeDAO;
import com.faculty.management.model.Notice;
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

import java.time.LocalDateTime;

/**
 * Controller for Faculty Notices and Announcements.
 */
public class NoticeManagementController {

    @FXML private TableView<Notice> tblNotices;
    @FXML private TableColumn<Notice, String> colTitle;
    @FXML private TableColumn<Notice, String> colTarget;
    @FXML private TableColumn<Notice, String> colAuthor;
    @FXML private TableColumn<Notice, LocalDateTime> colDate;
    @FXML private TableColumn<Notice, Boolean> colPinned;

    @FXML private Label lblNoticeTitle;
    @FXML private Label lblNoticeMeta;
    @FXML private TextArea txtNoticeContent;

    @FXML private HBox adminControls;
    @FXML private Button btnAddNotice;
    @FXML private Button btnEditNotice;
    @FXML private Button btnDeleteNotice;
    @FXML private Button btnRefresh;

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private final ObservableList<Notice> noticeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetAudience"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("postedByName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("postedDate"));
        colPinned.setCellValueFactory(new PropertyValueFactory<>("pinned"));

        User user = SessionManager.getCurrentUser();
        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);

        tblNotices.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                lblNoticeTitle.setText(newSel.getTitle());
                lblNoticeMeta.setText("Target: " + newSel.getTargetAudience() + " | Posted by: " + newSel.getPostedByName() + " on " + newSel.getPostedDate().toLocalDate());
                txtNoticeContent.setText(newSel.getContent());
            } else {
                lblNoticeTitle.setText("Select a notice");
                lblNoticeMeta.setText("");
                txtNoticeContent.setText("");
            }
        });

        loadNotices();
    }

    public void loadNotices() {
        noticeList.clear();
        User user = SessionManager.getCurrentUser();
        if (user != null && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            noticeList.addAll(noticeDAO.getNoticesForAudience(user.getRole()));
        } else {
            noticeList.addAll(noticeDAO.getAllNotices());
        }
        tblNotices.setItems(noticeList);
        if (!noticeList.isEmpty()) {
            tblNotices.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleAddNotice() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;

        Dialog<Notice> dialog = new Dialog<>();
        dialog.setTitle("Publish Faculty Notice");
        dialog.setHeaderText("Create Official Faculty Announcement");

        ButtonType postBtn = new ButtonType("Publish Notice", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(postBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField titleField = new TextField();
        titleField.setPromptText("Notice Title / Subject");
        ComboBox<String> audienceCombo = new ComboBox<>();
        audienceCombo.getItems().addAll("ALL", "STUDENTS", "LECTURERS", "OFFICERS");
        audienceCombo.setValue("ALL");

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Enter full announcement text here...");
        contentArea.setPrefRowCount(6);

        CheckBox pinCheck = new CheckBox("Pin Notice to Top of Portal");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Audience:"), 0, 1);
        grid.add(audienceCombo, 1, 1);
        grid.add(new Label("Content:"), 0, 2);
        grid.add(contentArea, 1, 2);
        grid.add(pinCheck, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == postBtn) {
                if (!ValidationUtil.isNotEmpty(titleField.getText()) || !ValidationUtil.isNotEmpty(contentArea.getText())) {
                    AlertUtil.showWarning("Validation Error", "Please provide a title and announcement body.");
                    return null;
                }

                Notice n = new Notice();
                n.setTitle(titleField.getText().trim());
                n.setContent(contentArea.getText().trim());
                n.setTargetAudience(audienceCombo.getValue());
                n.setPostedBy(user.getUserId());
                n.setPinned(pinCheck.isSelected());

                boolean added = noticeDAO.addNotice(n);
                if (added) {
                    AlertUtil.showInfo("Success", "Notice published successfully!");
                    loadNotices();
                } else {
                    AlertUtil.showError("Error", "Could not publish notice.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleEditNotice() {
        Notice selected = tblNotices.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a notice to edit.");
            return;
        }

        Dialog<Notice> dialog = new Dialog<>();
        dialog.setTitle("Edit Notice");
        dialog.setHeaderText("Updating: " + selected.getTitle());

        ButtonType saveBtn = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField titleField = new TextField(selected.getTitle());
        ComboBox<String> audienceCombo = new ComboBox<>();
        audienceCombo.getItems().addAll("ALL", "STUDENTS", "LECTURERS", "OFFICERS");
        audienceCombo.setValue(selected.getTargetAudience());

        TextArea contentArea = new TextArea(selected.getContent());
        contentArea.setPrefRowCount(6);

        CheckBox pinCheck = new CheckBox("Pin Notice to Top");
        pinCheck.setSelected(selected.isPinned());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Audience:"), 0, 1);
        grid.add(audienceCombo, 1, 1);
        grid.add(new Label("Content:"), 0, 2);
        grid.add(contentArea, 1, 2);
        grid.add(pinCheck, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setTitle(titleField.getText().trim());
                selected.setContent(contentArea.getText().trim());
                selected.setTargetAudience(audienceCombo.getValue());
                selected.setPinned(pinCheck.isSelected());

                boolean updated = noticeDAO.updateNotice(selected);
                if (updated) {
                    AlertUtil.showInfo("Success", "Notice updated successfully!");
                    loadNotices();
                } else {
                    AlertUtil.showError("Error", "Could not update notice.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteNotice() {
        Notice selected = tblNotices.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a notice to delete.");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete notice: " + selected.getTitle() + "?");
        if (confirm) {
            boolean deleted = noticeDAO.deleteNotice(selected.getNoticeId());
            if (deleted) {
                AlertUtil.showInfo("Deleted", "Notice removed.");
                loadNotices();
            } else {
                AlertUtil.showError("Error", "Could not delete notice.");
            }
        }
    }
}
