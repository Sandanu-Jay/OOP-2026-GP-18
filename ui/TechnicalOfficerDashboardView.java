package com.faculty.management.ui;

import com.faculty.management.dao.*;
import com.faculty.management.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Modern Swing Technical Officer Dashboard View.
 * Attendance marking (15 Theory + 15 Practical = 30 sessions) and Medical Certificate approvals.
 */
public class TechnicalOfficerDashboardView extends JPanel {

    private final ViewNavigator navigator;
    private final User currentUser;

    // DAO Interfaces (OOP Abstraction)
    private final ICourseDAO courseDAO = new CourseDAO();
    private final IStudentDAO studentDAO = new StudentDAO();
    private final IAttendanceDAO attendanceDAO = new AttendanceDAO();
    private final IMedicalDAO medicalDAO = new MedicalDAO();
    private final ITimetableDAO timetableDAO = new TimetableDAO();
    private final INoticeDAO noticeDAO = new NoticeDAO();

    private CardLayout contentCardLayout;
    private JPanel contentPanel;
    private final JButton[] navButtons = new JButton[4];
    private static final String[] TAB_NAMES = {"ATTENDANCE", "MEDICALS", "TIMETABLE", "NOTICES"};

    // Attendance tab state
    private JComboBox<CourseItem> attCourseCombo;
    private JTable attTable;
    private DefaultTableModel attModel;
    private JComboBox<StudentItem> attStudentCombo;
    private JSpinner spTheory;
    private JSpinner spPractical;
    private JLabel lblAttPct;
    private JLabel lblAttStatus;

    // Medicals tab state
    private JTable medicalsTable;
    private DefaultTableModel medicalsModel;

    // Timetable tab state
    private DefaultTableModel timetableModel;

    // Notices tab state
    private DefaultTableModel noticesModel;

    public TechnicalOfficerDashboardView(ViewNavigator navigator, User user) {
        this.navigator = navigator;
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initUI();
        loadAllData();
    }

    private void initUI() {
        // --- Sidebar (West) ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(new EmptyBorder(25, 16, 25, 16));

        JLabel brandLbl = new JLabel("FACULTY PORTAL");
        brandLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        brandLbl.setForeground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel(currentUser != null ? currentUser.getFullName() : "Technical Officer");
        userLbl.setFont(UITheme.FONT_HEADER);
        userLbl.setForeground(Color.WHITE);

        JLabel roleBadge = new JLabel("Technical Officer");
        roleBadge.setFont(UITheme.FONT_SMALL);
        roleBadge.setForeground(new Color(148, 163, 184));

        sidebar.add(brandLbl);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(userLbl);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(roleBadge);
        sidebar.add(Box.createVerticalStrut(25));

        String[] btnLabels = {
            "📝  Record Attendance",
            "🏥  Verify Medicals",
            "📅  Class Timetable",
            "📢  Notice Board"
        };

        for (int i = 0; i < btnLabels.length; i++) {
            final int index = i;
            JButton btn = createNavBtn(btnLabels[i]);
            btn.addActionListener(e -> switchTab(index));
            navButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = UIHelper.createDangerButton("🚪  Sign Out");
        logoutBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        logoutBtn.addActionListener(e -> navigator.logout());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // --- Center Panel ---
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(UITheme.BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        contentPanel.add(buildAttendanceTab(), "ATTENDANCE");
        contentPanel.add(buildMedicalsTab(), "MEDICALS");
        contentPanel.add(buildTimetableTab(), "TIMETABLE");
        contentPanel.add(buildNoticesTab(), "NOTICES");

        add(contentPanel, BorderLayout.CENTER);
        setActiveNav(0);
    }

    private JButton createNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_REGULAR);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }

    private void switchTab(int index) {
        setActiveNav(index);
        contentCardLayout.show(contentPanel, TAB_NAMES[index]);
    }

    private void setActiveNav(int activeIndex) {
        for (int i = 0; i < navButtons.length; i++) {
            if (i == activeIndex) {
                navButtons[i].setBackground(UITheme.SIDEBAR_ACTIVE);
                navButtons[i].setForeground(Color.WHITE);
                navButtons[i].setFont(UITheme.FONT_REGULAR_BOLD);
            } else {
                navButtons[i].setBackground(UITheme.SIDEBAR_BG);
                navButtons[i].setForeground(new Color(203, 213, 225));
                navButtons[i].setFont(UITheme.FONT_REGULAR);
            }
        }
    }

    // ==========================================
    // 1. ATTENDANCE MARKING TAB
    // ==========================================
    private JPanel buildAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Student Attendance Recording",
            "Maintain 15 Theory and 15 Practical sessions (30 sessions total). Evaluates 80% threshold rule."), BorderLayout.NORTH);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(new JLabel("Select Course Module:"));
        attCourseCombo = new JComboBox<>();
        attCourseCombo.setFont(UITheme.FONT_REGULAR);
        attCourseCombo.setPreferredSize(new Dimension(320, 36));
        attCourseCombo.addActionListener(e -> onAttCourseChanged());
        topBar.add(attCourseCombo);
        panel.add(topBar, BorderLayout.NORTH);

        JPanel split = new JPanel(new BorderLayout(20, 0));
        split.setOpaque(false);

        // Left Table: Batch Summary
        JPanel leftCard = UIHelper.createCardPanel(new BorderLayout(0, 8));
        JLabel tableTitle = new JLabel("Batch Attendance Summary (80% Rule Compliance)");
        tableTitle.setFont(UITheme.FONT_HEADER);
        leftCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Reg No", "Full Name", "Theory (/15)", "Practical (/15)", "Medicals", "Effective %", "Status"};
        attModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        attTable = new JTable(attModel);
        UIHelper.styleTable(attTable);
        attTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onAttTableSelect();
        });
        leftCard.add(new JScrollPane(attTable), BorderLayout.CENTER);
        split.add(leftCard, BorderLayout.CENTER);

        // Right Card: Update Attendance Form
        JPanel rightCard = UIHelper.createCardPanel(new BorderLayout(0, 12));
        rightCard.setPreferredSize(new Dimension(340, 0));
        JLabel formTitle = new JLabel("Update Attendance Sessions");
        formTitle.setFont(UITheme.FONT_HEADER);
        rightCard.add(formTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(4, 2, 8, 12));
        formGrid.setOpaque(false);

        attStudentCombo = new JComboBox<>();
        attStudentCombo.setFont(UITheme.FONT_REGULAR);
        attStudentCombo.addActionListener(e -> onAttComboStudentChanged());

        spTheory = new JSpinner(new SpinnerNumberModel(12, 0, 15, 1));
        spPractical = new JSpinner(new SpinnerNumberModel(12, 0, 15, 1));

        spTheory.addChangeListener(e -> calcPreviewPct());
        spPractical.addChangeListener(e -> calcPreviewPct());

        formGrid.add(new JLabel("Student:")); formGrid.add(attStudentCombo);
        formGrid.add(new JLabel("Theory Attended (0-15):")); formGrid.add(spTheory);
        formGrid.add(new JLabel("Practical Attended (0-15):")); formGrid.add(spPractical);

        lblAttPct = new JLabel("80.0 %"); lblAttPct.setFont(UITheme.FONT_HEADER); lblAttPct.setForeground(UITheme.SUCCESS);
        lblAttStatus = new JLabel("ELIGIBLE (≥ 80%)"); lblAttStatus.setFont(UITheme.FONT_REGULAR_BOLD); lblAttStatus.setForeground(UITheme.SUCCESS);

        JPanel statusPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        statusPanel.setBackground(new Color(241, 245, 249));
        statusPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statusPanel.add(new JLabel("Attendance:")); statusPanel.add(lblAttPct);
        statusPanel.add(new JLabel("80% Status:")); statusPanel.add(lblAttStatus);

        JPanel formCenter = new JPanel(new BorderLayout(0, 12));
        formCenter.setOpaque(false);
        formCenter.add(formGrid, BorderLayout.NORTH);
        formCenter.add(statusPanel, BorderLayout.CENTER);
        rightCard.add(formCenter, BorderLayout.CENTER);

        JButton saveBtn = UIHelper.createPrimaryButton("💾  Save Attendance");
        saveBtn.addActionListener(e -> saveAttendance());
        rightCard.add(saveBtn, BorderLayout.SOUTH);

        split.add(rightCard, BorderLayout.EAST);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void onAttCourseChanged() {
        CourseItem c = (CourseItem) attCourseCombo.getSelectedItem();
        if (c == null) return;
        attModel.setRowCount(0);
        attStudentCombo.removeAllItems();

        List<AttendanceSummary> list = attendanceDAO.getBatchAttendanceSummary(c.courseId);
        for (AttendanceSummary s : list) {
            attModel.addRow(new Object[]{
                s.getStudentId(), s.getStudentRegNo(), s.getStudentName(),
                s.getTheoryCount() + " / 15", s.getPracticalCount() + " / 15",
                s.getApprovedMedicals(),
                String.format("%.1f %%", s.getEffectivePercentage()),
                s.isEligible() ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
            attStudentCombo.addItem(new StudentItem(s.getStudentId(), s.getStudentRegNo() + " - " + s.getStudentName()));
        }
    }

    private void onAttTableSelect() {
        int row = attTable.getSelectedRow();
        if (row < 0) return;
        int stuId = (int) attModel.getValueAt(row, 0);

        for (int i = 0; i < attStudentCombo.getItemCount(); i++) {
            if (attStudentCombo.getItemAt(i).studentId == stuId) {
                attStudentCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onAttComboStudentChanged() {
        StudentItem stu = (StudentItem) attStudentCombo.getSelectedItem();
        CourseItem crs = (CourseItem) attCourseCombo.getSelectedItem();
        if (stu == null || crs == null) return;

        AttendanceSummary summary = attendanceDAO.getStudentAttendanceSummary(stu.studentId, crs.courseId);
        if (summary != null) {
            spTheory.setValue(summary.getTheoryCount());
            spPractical.setValue(summary.getPracticalCount());
            calcPreviewPct();
        }
    }

    private void calcPreviewPct() {
        int th = (int) spTheory.getValue();
        int pr = (int) spPractical.getValue();
        double pct = ((th + pr) / 30.0) * 100.0;
        lblAttPct.setText(String.format("%.1f %%", pct));
        if (pct >= 80.0) {
            lblAttStatus.setText("ELIGIBLE (≥ 80%)");
            lblAttStatus.setForeground(UITheme.SUCCESS);
            lblAttPct.setForeground(UITheme.SUCCESS);
        } else {
            lblAttStatus.setText("NOT ELIGIBLE (< 80%)");
            lblAttStatus.setForeground(UITheme.DANGER);
            lblAttPct.setForeground(UITheme.DANGER);
        }
    }

    private void saveAttendance() {
        StudentItem stu = (StudentItem) attStudentCombo.getSelectedItem();
        CourseItem crs = (CourseItem) attCourseCombo.getSelectedItem();
        if (stu == null || crs == null) {
            JOptionPane.showMessageDialog(this, "Select both course and student.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int th = (int) spTheory.getValue();
        int pr = (int) spPractical.getValue();
        Date today = Date.valueOf(LocalDate.now());

        // Mark individual session records
        for (int i = 1; i <= 15; i++) {
            String status = (i <= th) ? "PRESENT" : "ABSENT";
            attendanceDAO.markAttendance(stu.studentId, crs.courseId, "THEORY", i, today, status);
        }
        for (int i = 1; i <= 15; i++) {
            String status = (i <= pr) ? "PRESENT" : "ABSENT";
            attendanceDAO.markAttendance(stu.studentId, crs.courseId, "PRACTICAL", i, today, status);
        }

        JOptionPane.showMessageDialog(this, "Attendance recorded successfully for " + stu.label + "!");
        onAttCourseChanged();
    }

    // ==========================================
    // 2. MEDICAL VERIFICATION TAB
    // ==========================================
    private JPanel buildMedicalsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Medical Certificate Verification",
            "Approve or reject undergraduate medical certificates. Approved medicals convert absent sessions into attendance credits."), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);
        JButton approveBtn = UIHelper.createSuccessButton("✓ Approve Medical");
        JButton rejectBtn = UIHelper.createDangerButton("✗ Reject Medical");

        approveBtn.addActionListener(e -> updateSelectedMedical("APPROVED"));
        rejectBtn.addActionListener(e -> updateSelectedMedical("REJECTED"));

        toolbar.add(approveBtn);
        toolbar.add(rejectBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Reg No", "Student Name", "Course", "Session Type", "Session #", "Medical Date", "Status", "Reason", "Officer Comments"};
        medicalsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        medicalsTable = new JTable(medicalsModel);
        UIHelper.styleTable(medicalsTable);
        tableCard.add(new JScrollPane(medicalsTable), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void updateSelectedMedical(String status) {
        int row = medicalsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a medical record from the table.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int medId = (int) medicalsModel.getValueAt(row, 0);
        String student = (String) medicalsModel.getValueAt(row, 2);

        String remarks = JOptionPane.showInputDialog(this,
            "Enter comments/remarks for " + status + " medical of " + student + ":",
            status.equals("APPROVED") ? "Medical Approved by Technical Officer" : "Insufficient proof");

        if (remarks == null) return; // User cancelled

        if (medicalDAO.updateApprovalStatus(medId, status, remarks)) {
            JOptionPane.showMessageDialog(this, "Medical status updated to " + status + " successfully!");
            loadMedicalsData();
            if (attCourseCombo.getSelectedItem() != null) {
                onAttCourseChanged();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medical status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // 3. TIMETABLE TAB
    // ==========================================
    private JPanel buildTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Faculty Laboratory & Lecture Timetable", "Weekly schedule for departmental facilities"), BorderLayout.NORTH);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Department", "Semester", "Day", "Start Time", "End Time", "Course", "Venue", "Lecturer"};
        timetableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(timetableModel);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 4. NOTICES TAB
    // ==========================================
    private JPanel buildNoticesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Faculty Notices & Announcements", "Announcements for technical staff"), BorderLayout.NORTH);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Title", "Target Audience", "Posted Date", "Pinned"};
        noticesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(noticesModel);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // DATA LOADERS
    // ==========================================
    public void loadAllData() {
        attCourseCombo.removeAllItems();
        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) {
            attCourseCombo.addItem(new CourseItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
        }

        loadMedicalsData();
        loadTimetableData();
        loadNoticesData();
    }

    private void loadMedicalsData() {
        medicalsModel.setRowCount(0);
        List<MedicalRecord> list = medicalDAO.getAllMedicalRecords();
        for (MedicalRecord m : list) {
            medicalsModel.addRow(new Object[]{
                m.getMedicalId(), m.getStudentRegNo(), m.getStudentName(), m.getCourseCode(),
                m.getSessionType(), m.getSessionNumber(), m.getMedicalDate(),
                m.getIsApproved(), m.getReason(), m.getComments() != null ? m.getComments() : ""
            });
        }
    }

    private void loadTimetableData() {
        timetableModel.setRowCount(0);
        List<Timetable> list = timetableDAO.getAllTimetables();
        for (Timetable t : list) {
            timetableModel.addRow(new Object[]{
                t.getTimetableId(), t.getDepartment(), t.getSemester(), t.getDayOfWeek(),
                t.getStartTime(), t.getEndTime(), t.getCourseCode(), t.getVenue(),
                t.getLecturerName() != null ? t.getLecturerName() : "TBD"
            });
        }
    }

    private void loadNoticesData() {
        noticesModel.setRowCount(0);
        List<Notice> notes = noticeDAO.getNoticesForAudience("TECHNICAL_OFFICER");
        for (Notice n : notes) {
            noticesModel.addRow(new Object[]{
                n.getNoticeId(), n.getTitle(), n.getTargetAudience(),
                n.getPostedDate() != null ? n.getPostedDate().toLocalDate() : "",
                n.isPinned() ? "YES" : "NO"
            });
        }
    }

    private static class CourseItem {
        final int courseId;
        final String label;

        CourseItem(int courseId, String label) {
            this.courseId = courseId;
            this.label = label;
        }

        @Override
        public String toString() { return label; }
    }

    private static class StudentItem {
        final int studentId;
        final String label;

        StudentItem(int studentId, String label) {
            this.studentId = studentId;
            this.label = label;
        }

        @Override
        public String toString() { return label; }
    }
}
