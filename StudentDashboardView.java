package com.faculty.management.ui;

import com.faculty.management.dao.*;
import com.faculty.management.model.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modern Swing Student Dashboard View.
 * Complete 80% attendance rule monitoring, SGPA/CGPA calculation, and profile editing.
 */
public class StudentDashboardView extends JPanel {

    private final ViewNavigator navigator;
    private final User currentUser;
    private final Student currentStudent;

    // DAO Interfaces (OOP Abstraction)
    private final IUserDAO userDAO = new UserDAO();
    private final ICourseDAO courseDAO = new CourseDAO();
    private final IAttendanceDAO attendanceDAO = new AttendanceDAO();
    private final IMarksDAO marksDAO = new MarksDAO();
    private final IMedicalDAO medicalDAO = new MedicalDAO();
    private final ITimetableDAO timetableDAO = new TimetableDAO();
    private final INoticeDAO noticeDAO = new NoticeDAO();

    private CardLayout contentCardLayout;
    private JPanel contentPanel;
    private final JButton[] navButtons = new JButton[7];
    private static final String[] TAB_NAMES = {
        "PROFILE", "COURSES", "ATTENDANCE", "MARKS_GPA", "MEDICALS", "TIMETABLE", "NOTICES"
    };

    // Profile tab
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;

    // Attendance tab
    private JComboBox<CourseItem> attCourseCombo;
    private JLabel lblTheoryVal;
    private JLabel lblPracticalVal;
    private JLabel lblMedicalsVal;
    private JLabel lblAttPctVal;
    private JPanel attBannerPanel;
    private JLabel attBannerLabel;

    // Marks tab
    private JLabel lblCgpaVal;
    private JLabel lblStandingVal;
    private DefaultTableModel marksModel;

    // Courses & Materials
    private DefaultTableModel coursesModel;
    private DefaultTableModel materialsModel;

    // Medicals
    private DefaultTableModel medicalsModel;

    // Timetable & Notices
    private DefaultTableModel timetableModel;
    private DefaultTableModel noticesModel;

    public StudentDashboardView(ViewNavigator navigator, User user) {
        this.navigator = navigator;
        this.currentUser = user;
        this.currentStudent = (user instanceof Student s) ? s : new Student();

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

        JLabel brandLbl = new JLabel("STUDENT PORTAL");
        brandLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        brandLbl.setForeground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel(currentUser != null ? currentUser.getFullName() : "Student");
        userLbl.setFont(UITheme.FONT_HEADER);
        userLbl.setForeground(Color.WHITE);

        JLabel regBadge = new JLabel(currentStudent.getRegNo() != null ? currentStudent.getRegNo() : "Undergraduate");
        regBadge.setFont(UITheme.FONT_SMALL);
        regBadge.setForeground(new Color(148, 163, 184));

        sidebar.add(brandLbl);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(userLbl);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(regBadge);
        sidebar.add(Box.createVerticalStrut(25));

        String[] btnLabels = {
            "👤  My Profile",
            "📚  Enrolled Modules",
            "📊  Attendance (80% Rule)",
            "🏆  Marks & GPA",
            "🏥  Medical Submissions",
            "📅  Class Timetable",
            "📢  Faculty Notices"
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

        contentPanel.add(buildProfileTab(), "PROFILE");
        contentPanel.add(buildCoursesTab(), "COURSES");
        contentPanel.add(buildAttendanceTab(), "ATTENDANCE");
        contentPanel.add(buildMarksGpaTab(), "MARKS_GPA");
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
    // 1. MY PROFILE TAB
    // ==========================================
    private JPanel buildProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Student Profile & Contact Details",
            "Academic information is managed by administration. Contact details can be updated by the student."), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 20, 0));
        split.setOpaque(false);

        // Left Card: Read-Only Academic Info
        JPanel academicCard = UIHelper.createCardPanel(new BorderLayout(0, 15));
        JLabel acadTitle = new JLabel("Academic Records (Read-Only)");
        acadTitle.setFont(UITheme.FONT_HEADER);
        academicCard.add(acadTitle, BorderLayout.NORTH);

        JPanel acadGrid = new JPanel(new GridLayout(5, 2, 8, 14));
        acadGrid.setOpaque(false);

        acadGrid.add(new JLabel("Full Name:"));
        acadGrid.add(createReadonlyLabel(currentUser != null ? currentUser.getFullName() : ""));
        acadGrid.add(new JLabel("Registration Number:"));
        acadGrid.add(createReadonlyLabel(currentStudent.getRegNo()));
        acadGrid.add(new JLabel("Department:"));
        acadGrid.add(createReadonlyLabel(currentStudent.getDepartment()));
        acadGrid.add(new JLabel("Academic Batch:"));
        acadGrid.add(createReadonlyLabel(currentStudent.getBatch()));
        acadGrid.add(new JLabel("Student Status:"));
        acadGrid.add(createReadonlyLabel(currentStudent.getStudentStatus()));

        academicCard.add(acadGrid, BorderLayout.CENTER);
        split.add(academicCard);

        // Right Card: Editable Contact Details
        JPanel contactCard = UIHelper.createCardPanel(new BorderLayout(0, 15));
        JLabel contactTitle = new JLabel("Update Contact Information");
        contactTitle.setFont(UITheme.FONT_HEADER);
        contactCard.add(contactTitle, BorderLayout.NORTH);

        JPanel contactGrid = new JPanel(new GridLayout(3, 2, 8, 14));
        contactGrid.setOpaque(false);

        tfEmail = UIHelper.createTextField(15);
        tfEmail.setText(currentUser != null ? currentUser.getEmail() : "");
        tfPhone = UIHelper.createTextField(15);
        tfPhone.setText(currentUser != null ? currentUser.getPhone() : "");
        tfAddress = UIHelper.createTextField(15);
        tfAddress.setText(currentUser != null ? currentUser.getAddress() : "");

        contactGrid.add(new JLabel("Email Address:")); contactGrid.add(tfEmail);
        contactGrid.add(new JLabel("Contact Phone:")); contactGrid.add(tfPhone);
        contactGrid.add(new JLabel("Postal Address:")); contactGrid.add(tfAddress);

        contactCard.add(contactGrid, BorderLayout.CENTER);

        JButton saveBtn = UIHelper.createPrimaryButton("💾  Save Contact Updates");
        saveBtn.addActionListener(e -> updateProfile());
        contactCard.add(saveBtn, BorderLayout.SOUTH);

        split.add(contactCard);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createReadonlyLabel(String text) {
        JLabel l = new JLabel(text != null ? text : "-");
        l.setFont(UITheme.FONT_REGULAR_BOLD);
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }

    private void updateProfile() {
        if (currentUser == null) return;
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();
        String address = tfAddress.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.updateContactAndProfilePicture(currentUser.getUserId(), email, phone, address, currentUser.getProfileImage())) {
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            JOptionPane.showMessageDialog(this, "Contact details updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update contact details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // 2. ENROLLED COURSES TAB
    // ==========================================
    private JPanel buildCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Enrolled Curriculum Modules", "Academic course units for your degree programme"), BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(2, 1, 0, 15));
        split.setOpaque(false);

        // Top: Enrolled Courses Table
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout(0, 8));
        JLabel title = new JLabel("Enrolled Courses");
        title.setFont(UITheme.FONT_HEADER);
        tableCard.add(title, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Course Name", "Credits", "Department", "Semester", "Lecturer"};
        coursesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(coursesModel);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        split.add(tableCard);

        // Bottom: Course Materials Table
        JPanel matCard = UIHelper.createCardPanel(new BorderLayout(0, 8));
        JLabel matTitle = new JLabel("Learning Materials & Resources");
        matTitle.setFont(UITheme.FONT_HEADER);
        matCard.add(matTitle, BorderLayout.NORTH);

        String[] mCols = {"Module", "Resource Title", "File Type", "Description", "Uploaded Date"};
        materialsModel = new DefaultTableModel(mCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable matTable = new JTable(materialsModel);
        UIHelper.styleTable(matTable);
        matCard.add(new JScrollPane(matTable), BorderLayout.CENTER);
        split.add(matCard);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 3. ATTENDANCE & 80% RULE TAB
    // ==========================================
    private JPanel buildAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Attendance Compliance (80% Rule)",
            "University Academic Standard: Minimum 80.0% effective attendance across 15 Theory + 15 Practical sessions required for Final Exam Eligibility."), BorderLayout.NORTH);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setOpaque(false);
        topBar.add(new JLabel("Select Enrolled Module:"));
        attCourseCombo = new JComboBox<>();
        attCourseCombo.setFont(UITheme.FONT_REGULAR);
        attCourseCombo.setPreferredSize(new Dimension(340, 36));
        attCourseCombo.addActionListener(e -> updateAttendanceView());
        topBar.add(attCourseCombo);
        panel.add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);

        // 4 KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiGrid.setOpaque(false);

        JPanel c1 = UIHelper.createKpiCard("Theory Attended", "0 / 15", "Out of 15 lecture sessions", UITheme.PRIMARY);
        JPanel c2 = UIHelper.createKpiCard("Practical Attended", "0 / 15", "Out of 15 lab sessions", UITheme.INFO);
        JPanel c3 = UIHelper.createKpiCard("Approved Medicals", "0", "Credits for missed sessions", UITheme.WARNING);
        JPanel c4 = UIHelper.createKpiCard("Effective Attendance", "0.0 %", "Threshold is 80.0%", UITheme.SUCCESS);

        lblTheoryVal = findLabel(c1, UITheme.FONT_KPI_VAL);
        lblPracticalVal = findLabel(c2, UITheme.FONT_KPI_VAL);
        lblMedicalsVal = findLabel(c3, UITheme.FONT_KPI_VAL);
        lblAttPctVal = findLabel(c4, UITheme.FONT_KPI_VAL);

        kpiGrid.add(c1); kpiGrid.add(c2); kpiGrid.add(c3); kpiGrid.add(c4);
        center.add(kpiGrid, BorderLayout.NORTH);

        // Big Eligibility Banner
        attBannerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        attBannerPanel.setBorder(new CompoundBorder(
            new LineBorder(UITheme.SUCCESS, 2, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        attBannerPanel.setBackground(UITheme.SUCCESS_BG);

        attBannerLabel = new JLabel("ELIGIBLE FOR END-SEMESTER FINAL EXAMINATION (≥ 80%)");
        attBannerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        attBannerLabel.setForeground(new Color(6, 95, 70));
        attBannerPanel.add(attBannerLabel);

        center.add(attBannerPanel, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void updateAttendanceView() {
        CourseItem c = (CourseItem) attCourseCombo.getSelectedItem();
        if (c == null || currentUser == null) return;

        AttendanceSummary s = attendanceDAO.getStudentAttendanceSummary(currentUser.getUserId(), c.courseId);
        if (s != null) {
            if (lblTheoryVal != null) lblTheoryVal.setText(s.getTheoryCount() + " / 15");
            if (lblPracticalVal != null) lblPracticalVal.setText(s.getPracticalCount() + " / 15");
            if (lblMedicalsVal != null) lblMedicalsVal.setText(String.valueOf(s.getApprovedMedicals()));
            if (lblAttPctVal != null) lblAttPctVal.setText(String.format("%.1f %%", s.getEffectivePercentage()));

            boolean eligible = s.isEligible();
            if (eligible) {
                attBannerPanel.setBackground(UITheme.SUCCESS_BG);
                attBannerPanel.setBorder(new LineBorder(UITheme.SUCCESS, 2, true));
                attBannerLabel.setText("✅  ELIGIBLE FOR END-SEMESTER FINAL EXAMINATION (Attendance ≥ 80.0%)");
                attBannerLabel.setForeground(new Color(6, 95, 70));
            } else {
                attBannerPanel.setBackground(UITheme.DANGER_BG);
                attBannerPanel.setBorder(new LineBorder(UITheme.DANGER, 2, true));
                attBannerLabel.setText("⚠️  NOT ELIGIBLE FOR FINAL EXAMINATION (Attendance < 80.0%)");
                attBannerLabel.setForeground(new Color(153, 27, 27));
            }
        }
    }

    // ==========================================
    // 4. MARKS & GPA TAB
    // ==========================================
    private JPanel buildMarksGpaTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Academic Marks & GPA Breakdown",
            "Continuous Assessment (CA), Final Exam, and UGC Commission Circular No. 12-2024 Grade Point Values (GPV)"), BorderLayout.NORTH);

        // Top GPA Metric Banner
        JPanel topKpis = new JPanel(new GridLayout(1, 2, 20, 0));
        topKpis.setOpaque(false);

        JPanel gpaCard = UIHelper.createKpiCard("Cumulative GPA (CGPA)", "0.00", "Overall Academic Performance", UITheme.PRIMARY);
        JPanel standingCard = UIHelper.createKpiCard("Academic Status", "Good Standing", "UGC Circular 12-2024 Classification", UITheme.SUCCESS);

        lblCgpaVal = findLabel(gpaCard, UITheme.FONT_KPI_VAL);
        lblStandingVal = findLabel(standingCard, UITheme.FONT_KPI_VAL);

        topKpis.add(gpaCard);
        topKpis.add(standingCard);
        panel.add(topKpis, BorderLayout.NORTH);

        // Table
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {
            "Course Code", "Course Name", "Credits", "Quiz (25%)", "Mid (50%)", "Assign (25%)",
            "CA Mark", "CA Status", "End Exam", "Total Mark", "Grade", "GPV"
        };
        marksModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(marksModel);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 5. MEDICAL RECORDS TAB
    // ==========================================
    private JPanel buildMedicalsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Submitted Medical Certificates",
            "Medical certificates approved by Technical Officers are credited toward your 80% attendance requirement."), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);
        JButton submitBtn = UIHelper.createPrimaryButton("+ Submit New Medical");
        submitBtn.addActionListener(e -> showSubmitMedicalDialog());
        toolbar.add(submitBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Course Code", "Session Type", "Session #", "Medical Date", "Status", "Reason", "Officer Remarks"};
        medicalsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(medicalsModel);
        UIHelper.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showSubmitMedicalDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Submit Medical Certificate", true);
        dlg.setSize(460, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<CourseItem> cbCourse = new JComboBox<>();
        List<Course> courses = courseDAO.getCoursesByStudent(currentUser != null ? currentUser.getUserId() : 0);
        for (Course c : courses) cbCourse.addItem(new CourseItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));

        JComboBox<String> cbType = UIHelper.createComboBox(new String[]{"THEORY", "PRACTICAL"});
        JSpinner spSession = new JSpinner(new SpinnerNumberModel(1, 1, 15, 1));
        JTextField tfDate = UIHelper.createTextField(10); tfDate.setText(LocalDate.now().toString());
        JTextField tfReason = UIHelper.createTextField(15); tfReason.setText("Medical illness / Doctor note");
        JTextField tfDoc = UIHelper.createTextField(15); tfDoc.setText("medical_cert.pdf");

        form.add(new JLabel("Course Module:")); form.add(cbCourse);
        form.add(new JLabel("Session Type:")); form.add(cbType);
        form.add(new JLabel("Session Number (1-15):")); form.add(spSession);
        form.add(new JLabel("Medical Date (YYYY-MM-DD):")); form.add(tfDate);
        form.add(new JLabel("Reason for Absence:")); form.add(tfReason);
        form.add(new JLabel("Attached Document Info:")); form.add(tfDoc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Submit Request");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            try {
                CourseItem c = (CourseItem) cbCourse.getSelectedItem();
                if (c == null) return;
                LocalDate dt = LocalDate.parse(tfDate.getText().trim());

                MedicalRecord m = new MedicalRecord(
                    0, currentUser.getUserId(), c.courseId, (String) cbType.getSelectedItem(),
                    (int) spSession.getValue(), dt, tfReason.getText().trim(), tfDoc.getText().trim(),
                    "PENDING", ""
                );

                if (medicalDAO.addMedicalRecord(m)) {
                    JOptionPane.showMessageDialog(dlg, "Medical certificate submitted for verification!");
                    dlg.dispose();
                    loadMedicalsData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ==========================================
    // 6. TIMETABLE TAB
    // ==========================================
    private JPanel buildTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("My Academic Schedule", "Weekly lecture and lab timetable for your enrolled courses"), BorderLayout.NORTH);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"Day", "Start Time", "End Time", "Course Code", "Course Name", "Venue", "Lecturer"};
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
    // 7. NOTICES TAB
    // ==========================================
    private JPanel buildNoticesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Faculty Notices & Announcements", "Broadcasts for undergraduates and faculty students"), BorderLayout.NORTH);

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
        if (currentUser == null) return;
        int stuId = currentUser.getUserId();

        // 1. Enrolled Courses
        coursesModel.setRowCount(0);
        attCourseCombo.removeAllItems();
        List<Course> courses = courseDAO.getCoursesByStudent(stuId);
        for (Course c : courses) {
            coursesModel.addRow(new Object[]{
                c.getCourseCode(), c.getCourseName(), c.getCredit(), c.getDepartment(),
                c.getSemester(), c.getLecturerName() != null ? c.getLecturerName() : "TBD"
            });
            attCourseCombo.addItem(new CourseItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
        }

        // Materials
        materialsModel.setRowCount(0);
        for (Course c : courses) {
            List<CourseMaterial> mats = courseDAO.getMaterialsByCourse(c.getCourseId());
            for (CourseMaterial m : mats) {
                materialsModel.addRow(new Object[]{
                    c.getCourseCode(), m.getTitle(), m.getFileType(), m.getDescription(),
                    m.getUploadedDate() != null ? m.getUploadedDate().toLocalDate() : ""
                });
            }
        }

        // 2. Marks & GPA
        marksModel.setRowCount(0);
        List<Marks> marks = marksDAO.getMarksByStudent(stuId);
        for (Marks m : marks) {
            marksModel.addRow(new Object[]{
                m.getCourseCode(), m.getCourseName(), m.getCredit(),
                m.getQuizMark(), m.getMidMark(), m.getAssignmentMark(),
                String.format("%.1f", m.getCaMark()),
                m.getCaEligibilityStatus(),
                m.getFinalExamMark(),
                String.format("%.1f", m.getTotalMark()),
                m.getGrade(),
                String.format("%.2f", m.getGpv())
            });
        }

        double gpa = marksDAO.calculateStudentGpa(stuId);
        if (lblCgpaVal != null) lblCgpaVal.setText(String.format("%.2f", gpa));
        if (lblStandingVal != null) {
            if (gpa >= 3.70) lblStandingVal.setText("First Class");
            else if (gpa >= 3.30) lblStandingVal.setText("Second Upper");
            else if (gpa >= 3.00) lblStandingVal.setText("Second Lower");
            else if (gpa >= 2.00) lblStandingVal.setText("Pass");
            else lblStandingVal.setText("Conditional / Weak");
        }

        // 3. Medicals
        loadMedicalsData();

        // 4. Timetable
        timetableModel.setRowCount(0);
        List<Timetable> tt = timetableDAO.getTimetablesForStudent(stuId);
        for (Timetable t : tt) {
            timetableModel.addRow(new Object[]{
                t.getDayOfWeek(), t.getStartTime(), t.getEndTime(), t.getCourseCode(),
                t.getCourseName(), t.getVenue(), t.getLecturerName() != null ? t.getLecturerName() : "TBD"
            });
        }

        // 5. Notices
        noticesModel.setRowCount(0);
        List<Notice> notes = noticeDAO.getNoticesForAudience("STUDENT");
        for (Notice n : notes) {
            noticesModel.addRow(new Object[]{
                n.getNoticeId(), n.getTitle(), n.getTargetAudience(),
                n.getPostedDate() != null ? n.getPostedDate().toLocalDate() : "",
                n.isPinned() ? "YES" : "NO"
            });
        }
    }

    private void loadMedicalsData() {
        if (currentUser == null) return;
        medicalsModel.setRowCount(0);
        List<MedicalRecord> list = medicalDAO.getMedicalRecordsByStudent(currentUser.getUserId());
        for (MedicalRecord m : list) {
            medicalsModel.addRow(new Object[]{
                m.getMedicalId(), m.getCourseCode(), m.getSessionType(), m.getSessionNumber(),
                m.getMedicalDate(), m.getIsApproved(), m.getReason(), m.getComments()
            });
        }
    }

    private JLabel findLabel(Container container, Font font) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel l && font.equals(l.getFont())) {
                return l;
            } else if (c instanceof Container sub) {
                JLabel found = findLabel(sub, font);
                if (found != null) return found;
            }
        }
        return null;
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
}
