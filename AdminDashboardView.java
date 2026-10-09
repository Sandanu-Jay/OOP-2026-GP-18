package com.faculty.management.ui;

import com.faculty.management.dao.*;
import com.faculty.management.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

/**
 * Modern Swing Admin Dashboard View.
 * Connects directly to DAO Interfaces (IStudentDAO, ILecturerDAO, etc.).
 */
public class AdminDashboardView extends JPanel {

    private final ViewNavigator navigator;
    private final User currentUser;

    // DAO Interfaces (Dependency Inversion / OOP Abstraction)
    private final IStudentDAO studentDAO = new StudentDAO();
    private final ILecturerDAO lecturerDAO = new LecturerDAO();
    private final ITechnicalOfficerDAO officerDAO = new TechnicalOfficerDAO();
    private final ICourseDAO courseDAO = new CourseDAO();
    private final ITimetableDAO timetableDAO = new TimetableDAO();
    private final INoticeDAO noticeDAO = new NoticeDAO();
    private final IDepartmentDAO departmentDAO = new DepartmentDAO();

    private CardLayout contentCardLayout;
    private JPanel contentPanel;

    // Sidebar navigation buttons
    private final JButton[] navButtons = new JButton[7];
    private static final String[] TAB_NAMES = {
        "OVERVIEW", "STUDENTS", "LECTURERS", "OFFICERS", "COURSES", "TIMETABLE", "NOTICES"
    };

    // Tables
    private JTable studentsTable;
    private DefaultTableModel studentsModel;
    private JTable lecturersTable;
    private DefaultTableModel lecturersModel;
    private JTable officersTable;
    private DefaultTableModel officersModel;
    private JTable coursesTable;
    private DefaultTableModel coursesModel;
    private JTable timetableTable;
    private DefaultTableModel timetableModel;
    private JTable noticesTable;
    private DefaultTableModel noticesModel;

    // KPI labels
    private JLabel kpiStudentsVal;
    private JLabel kpiLecturersVal;
    private JLabel kpiOfficersVal;
    private JLabel kpiCoursesVal;

    public AdminDashboardView(ViewNavigator navigator, User user) {
        this.navigator = navigator;
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initUI();
        refreshAllData();
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

        JLabel userLbl = new JLabel(currentUser != null ? currentUser.getFullName() : "Administrator");
        userLbl.setFont(UITheme.FONT_HEADER);
        userLbl.setForeground(Color.WHITE);

        JLabel roleBadge = new JLabel("System Administrator");
        roleBadge.setFont(UITheme.FONT_SMALL);
        roleBadge.setForeground(new Color(148, 163, 184));

        sidebar.add(brandLbl);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(userLbl);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(roleBadge);
        sidebar.add(Box.createVerticalStrut(25));

        // Navigation buttons
        String[] btnLabels = {
            "📊  Dashboard",
            "👨‍🎓  Undergraduates",
            "👨‍🏫  Lecturers",
            "🛠️  Tech Officers",
            "📚  Course Modules",
            "📅  Timetables",
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

        // --- Center Content Panel (CardLayout) ---
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(UITheme.BG_MAIN);
        contentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        contentPanel.add(buildOverviewTab(), "OVERVIEW");
        contentPanel.add(buildStudentsTab(), "STUDENTS");
        contentPanel.add(buildLecturersTab(), "LECTURERS");
        contentPanel.add(buildOfficersTab(), "OFFICERS");
        contentPanel.add(buildCoursesTab(), "COURSES");
        contentPanel.add(buildTimetableTab(), "TIMETABLE");
        contentPanel.add(buildNoticesTab(), "NOTICES");

        add(contentPanel, BorderLayout.CENTER);

        // Highlight first tab
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
    // 1. OVERVIEW TAB
    // ==========================================
    private JPanel buildOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        panel.add(UIHelper.createHeader("System Administration Overview", "Real-time metrics and system records"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);

        // 4 KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiGrid.setOpaque(false);

        JPanel card1 = UIHelper.createKpiCard("Students", "0", "Enrolled undergraduates", UITheme.PRIMARY);
        JPanel card2 = UIHelper.createKpiCard("Lecturers", "0", "Academic faculty staff", UITheme.SUCCESS);
        JPanel card3 = UIHelper.createKpiCard("Tech Officers", "0", "Technical staff", UITheme.WARNING);
        JPanel card4 = UIHelper.createKpiCard("Course Modules", "0", "Active curriculum modules", UITheme.INFO);

        kpiStudentsVal = findLabel(card1, UITheme.FONT_KPI_VAL);
        kpiLecturersVal = findLabel(card2, UITheme.FONT_KPI_VAL);
        kpiOfficersVal = findLabel(card3, UITheme.FONT_KPI_VAL);
        kpiCoursesVal = findLabel(card4, UITheme.FONT_KPI_VAL);

        kpiGrid.add(card1);
        kpiGrid.add(card2);
        kpiGrid.add(card3);
        kpiGrid.add(card4);

        center.add(kpiGrid, BorderLayout.NORTH);

        // Recent Notices Card
        JPanel noticesCard = UIHelper.createCardPanel(new BorderLayout(0, 10));
        JLabel recentTitle = new JLabel("Faculty Announcements");
        recentTitle.setFont(UITheme.FONT_HEADER);
        recentTitle.setForeground(UITheme.TEXT_DARK);
        noticesCard.add(recentTitle, BorderLayout.NORTH);

        String[] cols = {"Notice ID", "Title", "Audience", "Posted Date", "Pinned"};
        noticesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        noticesTable = new JTable(noticesModel);
        UIHelper.styleTable(noticesTable);
        noticesCard.add(new JScrollPane(noticesTable), BorderLayout.CENTER);

        center.add(noticesCard, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // 2. STUDENTS TAB
    // ==========================================
    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        panel.add(UIHelper.createHeader("Undergraduate Management", "Manage student profiles and academic statuses"), BorderLayout.NORTH);

        // Action Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        JTextField searchField = UIHelper.createTextField(18);
        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        JButton resetBtn = UIHelper.createSecondaryButton("Reset");
        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            loadStudentsData(q.isEmpty() ? null : q);
        });
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudentsData(null);
        });
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Register Student");
        JButton deleteBtn = UIHelper.createDangerButton("Delete Student");

        addBtn.addActionListener(e -> showAddStudentDialog());
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        actions.add(addBtn);
        actions.add(deleteBtn);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.CENTER);

        // Table Card
        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"User ID", "Reg No", "Full Name", "Email", "Phone", "Department", "Batch", "Status"};
        studentsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        studentsTable = new JTable(studentsModel);
        UIHelper.styleTable(studentsTable);
        tableCard.add(new JScrollPane(studentsTable), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddStudentDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register New Undergraduate", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(480, 560);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField tfUser = UIHelper.createTextField(15);
        JPasswordField tfPass = UIHelper.createPasswordField(15);
        JTextField tfName = UIHelper.createTextField(15);
        JTextField tfEmail = UIHelper.createTextField(15);
        JTextField tfPhone = UIHelper.createTextField(15);
        JTextField tfReg = UIHelper.createTextField(15);
        JComboBox<String> cbDept = UIHelper.createComboBox(new String[]{
            "Information and communication Technology",
            "Engineering Technology",
            "Bio System Technology",
            "Multidisciplinary Studies"
        });
        JTextField tfBatch = UIHelper.createTextField(15);
        tfBatch.setText("2022/2023");
        JComboBox<String> cbStatus = UIHelper.createComboBox(new String[]{"NORMAL", "REPEAT", "BATCH_MISSED"});

        form.add(new JLabel("Username:")); form.add(tfUser);
        form.add(new JLabel("Password:")); form.add(tfPass);
        form.add(new JLabel("Full Name:")); form.add(tfName);
        form.add(new JLabel("Email:")); form.add(tfEmail);
        form.add(new JLabel("Phone:")); form.add(tfPhone);
        form.add(new JLabel("Reg No (e.g. TG/2022/1001):")); form.add(tfReg);
        form.add(new JLabel("Department:")); form.add(cbDept);
        form.add(new JLabel("Batch:")); form.add(tfBatch);
        form.add(new JLabel("Status:")); form.add(cbStatus);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Register");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(tfPass.getPassword()).trim();
            String n = tfName.getText().trim();
            String em = tfEmail.getText().trim();
            String ph = tfPhone.getText().trim();
            String rg = tfReg.getText().trim();
            String dp = (String) cbDept.getSelectedItem();
            String bt = tfBatch.getText().trim();
            String st = (String) cbStatus.getSelectedItem();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty() || em.isEmpty() || rg.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please fill all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student stu = new Student(0, u, "", n, em, ph, "default_avatar.png", rg, dp, bt, st);
            boolean ok = studentDAO.addStudent(stu, p);
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Student registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadStudentsData(null);
                refreshOverviewKpis();
            } else {
                JOptionPane.showMessageDialog(dlg, "Registration failed! Username or Reg No may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteStudent() {
        int row = studentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int userId = (int) studentsModel.getValueAt(row, 0);
        String reg = (String) studentsModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to permanently delete student " + reg + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (studentDAO.deleteStudent(userId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                loadStudentsData(null);
                refreshOverviewKpis();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // 3. LECTURERS TAB
    // ==========================================
    private JPanel buildLecturersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        panel.add(UIHelper.createHeader("Lecturer Management", "Manage academic faculty staff and departmental designations"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Add Lecturer");
        JButton deleteBtn = UIHelper.createDangerButton("Delete Lecturer");

        addBtn.addActionListener(e -> showAddLecturerDialog());
        deleteBtn.addActionListener(e -> handleDeleteLecturer());

        actions.add(addBtn);
        actions.add(deleteBtn);
        toolbar.add(actions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"User ID", "Full Name", "Username", "Email", "Phone", "Department", "Designation"};
        lecturersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        lecturersTable = new JTable(lecturersModel);
        UIHelper.styleTable(lecturersTable);
        tableCard.add(new JScrollPane(lecturersTable), BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddLecturerDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Lecturer", true);
        dlg.setSize(460, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField tfUser = UIHelper.createTextField(15);
        JPasswordField tfPass = UIHelper.createPasswordField(15);
        JTextField tfName = UIHelper.createTextField(15);
        JTextField tfEmail = UIHelper.createTextField(15);
        JTextField tfPhone = UIHelper.createTextField(15);
        JComboBox<String> cbDept = UIHelper.createComboBox(new String[]{
            "Information and communication Technology",
            "Engineering Technology",
            "Bio System Technology",
            "Multidisciplinary Studies"
        });
        JTextField tfDesig = UIHelper.createTextField(15);
        tfDesig.setText("Senior Lecturer");

        form.add(new JLabel("Username:")); form.add(tfUser);
        form.add(new JLabel("Password:")); form.add(tfPass);
        form.add(new JLabel("Full Name:")); form.add(tfName);
        form.add(new JLabel("Email:")); form.add(tfEmail);
        form.add(new JLabel("Phone:")); form.add(tfPhone);
        form.add(new JLabel("Department:")); form.add(cbDept);
        form.add(new JLabel("Designation:")); form.add(tfDesig);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(tfPass.getPassword()).trim();
            String n = tfName.getText().trim();
            String em = tfEmail.getText().trim();
            String ph = tfPhone.getText().trim();
            String dp = (String) cbDept.getSelectedItem();
            String ds = tfDesig.getText().trim();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty() || em.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please fill required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Lecturer lec = new Lecturer(0, u, "", n, em, ph, "default_avatar.png", dp, ds);
            if (lecturerDAO.addLecturer(lec, p)) {
                JOptionPane.showMessageDialog(dlg, "Lecturer added successfully!");
                dlg.dispose();
                loadLecturersData();
                refreshOverviewKpis();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to add lecturer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteLecturer() {
        int row = lecturersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a lecturer to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) lecturersModel.getValueAt(row, 0);
        String name = (String) lecturersModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete lecturer " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (lecturerDAO.deleteLecturer(id)) {
                JOptionPane.showMessageDialog(this, "Lecturer deleted.");
                loadLecturersData();
                refreshOverviewKpis();
            }
        }
    }

    // ==========================================
    // 4. TECHNICAL OFFICERS TAB
    // ==========================================
    private JPanel buildOfficersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Technical Officers Management", "Manage technical laboratory officers"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Add Officer");
        JButton delBtn = UIHelper.createDangerButton("Delete Officer");
        addBtn.addActionListener(e -> showAddOfficerDialog());
        delBtn.addActionListener(e -> handleDeleteOfficer());
        toolbar.add(addBtn); toolbar.add(delBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"User ID", "Full Name", "Username", "Email", "Phone", "Department"};
        officersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        officersTable = new JTable(officersModel);
        UIHelper.styleTable(officersTable);
        tableCard.add(new JScrollPane(officersTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddOfficerDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Technical Officer", true);
        dlg.setSize(440, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField tfUser = UIHelper.createTextField(15);
        JPasswordField tfPass = UIHelper.createPasswordField(15);
        JTextField tfName = UIHelper.createTextField(15);
        JTextField tfEmail = UIHelper.createTextField(15);
        JTextField tfPhone = UIHelper.createTextField(15);
        JComboBox<String> cbDept = UIHelper.createComboBox(new String[]{
            "Information and communication Technology",
            "Engineering Technology",
            "Bio System Technology",
            "Multidisciplinary Studies"
        });

        form.add(new JLabel("Username:")); form.add(tfUser);
        form.add(new JLabel("Password:")); form.add(tfPass);
        form.add(new JLabel("Full Name:")); form.add(tfName);
        form.add(new JLabel("Email:")); form.add(tfEmail);
        form.add(new JLabel("Phone:")); form.add(tfPhone);
        form.add(new JLabel("Department:")); form.add(cbDept);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(tfPass.getPassword()).trim();
            String n = tfName.getText().trim();
            String em = tfEmail.getText().trim();
            String ph = tfPhone.getText().trim();
            String dp = (String) cbDept.getSelectedItem();

            if (u.isEmpty() || p.isEmpty() || n.isEmpty() || em.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please fill required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TechnicalOfficer to = new TechnicalOfficer(0, u, "", n, em, ph, "default_avatar.png", dp);
            if (officerDAO.addOfficer(to, p)) {
                JOptionPane.showMessageDialog(dlg, "Technical Officer added successfully!");
                dlg.dispose();
                loadOfficersData();
                refreshOverviewKpis();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to add officer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteOfficer() {
        int row = officersTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) officersModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete technical officer?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (officerDAO.deleteOfficer(id)) {
                loadOfficersData();
                refreshOverviewKpis();
            }
        }
    }

    // ==========================================
    // 5. COURSES TAB
    // ==========================================
    private JPanel buildCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Course Curriculum Management", "Define courses, credit weighting, and assigned lecturers"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Add Course");
        JButton delBtn = UIHelper.createDangerButton("Delete Course");
        addBtn.addActionListener(e -> showAddCourseDialog());
        delBtn.addActionListener(e -> handleDeleteCourse());
        toolbar.add(addBtn); toolbar.add(delBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Course Code", "Course Name", "Credits", "Department", "Semester", "Assigned Lecturer"};
        coursesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        coursesTable = new JTable(coursesModel);
        UIHelper.styleTable(coursesTable);
        tableCard.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddCourseDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Course Module", true);
        dlg.setSize(480, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField tfCode = UIHelper.createTextField(15);
        JTextField tfName = UIHelper.createTextField(15);
        JComboBox<Integer> cbCredits = UIHelper.createComboBox(new Integer[]{1, 2, 3, 4, 6});
        JComboBox<String> cbDept = UIHelper.createComboBox(new String[]{
            "Information and communication Technology",
            "Engineering Technology",
            "Bio System Technology",
            "Multidisciplinary Studies"
        });
        JComboBox<Integer> cbSem = UIHelper.createComboBox(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
        JComboBox<String> cbLec = new JComboBox<>();
        cbLec.addItem("-- Select Lecturer --");
        for (Lecturer l : lecturers) {
            cbLec.addItem(l.getUserId() + ": " + l.getFullName());
        }

        form.add(new JLabel("Course Code (e.g. ICT2101):")); form.add(tfCode);
        form.add(new JLabel("Course Name:")); form.add(tfName);
        form.add(new JLabel("Credit Value:")); form.add(cbCredits);
        form.add(new JLabel("Department:")); form.add(cbDept);
        form.add(new JLabel("Semester:")); form.add(cbSem);
        form.add(new JLabel("Assigned Lecturer:")); form.add(cbLec);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Save Course");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            String code = tfCode.getText().trim();
            String name = tfName.getText().trim();
            int cr = (int) cbCredits.getSelectedItem();
            String dp = (String) cbDept.getSelectedItem();
            int sem = (int) cbSem.getSelectedItem();

            Integer lecId = null;
            if (cbLec.getSelectedIndex() > 0) {
                String sel = (String) cbLec.getSelectedItem();
                lecId = Integer.parseInt(sel.split(":")[0]);
            }

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Course Code and Name are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course c = new Course(0, code, name, cr, dp, sem, lecId);
            if (courseDAO.addCourse(c)) {
                JOptionPane.showMessageDialog(dlg, "Course added successfully!");
                dlg.dispose();
                loadCoursesData();
                refreshOverviewKpis();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to add course. Code may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteCourse() {
        int row = coursesTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) coursesModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete course?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (courseDAO.deleteCourse(id)) {
                loadCoursesData();
                refreshOverviewKpis();
            }
        }
    }

    // ==========================================
    // 6. TIMETABLE TAB
    // ==========================================
    private JPanel buildTimetableTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Timetable Scheduling", "Classroom and laboratory lecture scheduling"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Add Session");
        JButton delBtn = UIHelper.createDangerButton("Delete Session");
        addBtn.addActionListener(e -> showAddTimetableDialog());
        delBtn.addActionListener(e -> handleDeleteTimetable());
        toolbar.add(addBtn); toolbar.add(delBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Department", "Semester", "Day", "Start Time", "End Time", "Course", "Venue", "Lecturer"};
        timetableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        timetableTable = new JTable(timetableModel);
        UIHelper.styleTable(timetableTable);
        tableCard.add(new JScrollPane(timetableTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddTimetableDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Schedule Timetable Session", true);
        dlg.setSize(480, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<String> cbDept = UIHelper.createComboBox(new String[]{
            "Information and communication Technology",
            "Engineering Technology",
            "Bio System Technology",
            "Multidisciplinary Studies"
        });
        JComboBox<Integer> cbSem = UIHelper.createComboBox(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JComboBox<String> cbDay = UIHelper.createComboBox(new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        });
        JTextField tfStart = UIHelper.createTextField(10); tfStart.setText("09:00:00");
        JTextField tfEnd = UIHelper.createTextField(10); tfEnd.setText("11:00:00");

        List<Course> courses = courseDAO.getAllCourses();
        JComboBox<String> cbCourse = new JComboBox<>();
        for (Course c : courses) cbCourse.addItem(c.getCourseId() + ": " + c.getCourseCode() + " - " + c.getCourseName());

        JTextField tfVenue = UIHelper.createTextField(15); tfVenue.setText("Lecture Hall 01");

        List<Lecturer> lecturers = lecturerDAO.getAllLecturers();
        JComboBox<String> cbLec = new JComboBox<>();
        cbLec.addItem("-- None --");
        for (Lecturer l : lecturers) cbLec.addItem(l.getUserId() + ": " + l.getFullName());

        form.add(new JLabel("Department:")); form.add(cbDept);
        form.add(new JLabel("Semester:")); form.add(cbSem);
        form.add(new JLabel("Day of Week:")); form.add(cbDay);
        form.add(new JLabel("Start Time (HH:mm:ss):")); form.add(tfStart);
        form.add(new JLabel("End Time (HH:mm:ss):")); form.add(tfEnd);
        form.add(new JLabel("Course Module:")); form.add(cbCourse);
        form.add(new JLabel("Venue / Room:")); form.add(tfVenue);
        form.add(new JLabel("Lecturer:")); form.add(cbLec);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Schedule");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            try {
                String dp = (String) cbDept.getSelectedItem();
                int sm = (int) cbSem.getSelectedItem();
                String dy = (String) cbDay.getSelectedItem();
                LocalTime st = LocalTime.parse(tfStart.getText().trim());
                LocalTime et = LocalTime.parse(tfEnd.getText().trim());
                int cid = Integer.parseInt(((String) cbCourse.getSelectedItem()).split(":")[0]);
                String vn = tfVenue.getText().trim();
                Integer lid = null;
                if (cbLec.getSelectedIndex() > 0) lid = Integer.parseInt(((String) cbLec.getSelectedItem()).split(":")[0]);

                Timetable t = new Timetable(0, dp, sm, dy, st, et, cid, vn, lid);
                if (timetableDAO.addTimetableEntry(t)) {
                    JOptionPane.showMessageDialog(dlg, "Timetable session scheduled!");
                    dlg.dispose();
                    loadTimetableData();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Failed to schedule session.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid time format. Use HH:mm:ss (e.g. 09:00:00).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteTimetable() {
        int row = timetableTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) timetableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete timetable session?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (timetableDAO.deleteTimetableEntry(id)) {
                loadTimetableData();
            }
        }
    }

    // ==========================================
    // 7. NOTICES TAB
    // ==========================================
    private JPanel buildNoticesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.add(UIHelper.createHeader("Notice Board Announcements", "Broadcast notices to undergraduates, lecturers, and staff"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton addBtn = UIHelper.createPrimaryButton("+ Post Announcement");
        JButton delBtn = UIHelper.createDangerButton("Delete Notice");
        addBtn.addActionListener(e -> showAddNoticeDialog());
        delBtn.addActionListener(e -> handleDeleteNotice());
        toolbar.add(addBtn); toolbar.add(delBtn);
        panel.add(toolbar, BorderLayout.CENTER);

        JPanel tableCard = UIHelper.createCardPanel(new BorderLayout());
        String[] cols = {"ID", "Title", "Target Audience", "Posted By", "Date", "Pinned"};
        noticesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        noticesTable = new JTable(noticesModel);
        UIHelper.styleTable(noticesTable);
        tableCard.add(new JScrollPane(noticesTable), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.SOUTH);
        tableCard.setPreferredSize(new Dimension(0, 480));
        return panel;
    }

    private void showAddNoticeDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Publish Faculty Notice", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField tfTitle = UIHelper.createTextField(15);
        JComboBox<String> cbAud = UIHelper.createComboBox(new String[]{"ALL", "STUDENTS", "LECTURERS", "OFFICERS"});
        JCheckBox chkPin = new JCheckBox("Pin to Top of Dashboard");

        top.add(new JLabel("Notice Title:")); top.add(tfTitle);
        top.add(new JLabel("Target Audience:")); top.add(cbAud);
        top.add(new JLabel("Pin Priority:")); top.add(chkPin);

        JTextArea taContent = new JTextArea(6, 20);
        taContent.setFont(UITheme.FONT_REGULAR);
        taContent.setLineWrap(true);
        taContent.setWrapStyleWord(true);

        form.add(top, BorderLayout.NORTH);
        form.add(new JScrollPane(taContent), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UIHelper.createPrimaryButton("Publish");
        JButton cancelBtn = UIHelper.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());

        saveBtn.addActionListener(e -> {
            String title = tfTitle.getText().trim();
            String content = taContent.getText().trim();
            String aud = (String) cbAud.getSelectedItem();
            boolean pin = chkPin.isSelected();

            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please provide notice title and content.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Notice n = new Notice(0, title, content, aud, currentUser != null ? currentUser.getUserId() : 1, null, pin);
            if (noticeDAO.addNotice(n)) {
                JOptionPane.showMessageDialog(dlg, "Notice published successfully!");
                dlg.dispose();
                loadNoticesData();
            }
        });

        btnPanel.add(cancelBtn); btnPanel.add(saveBtn);
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void handleDeleteNotice() {
        int row = noticesTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) noticesModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete notice?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (noticeDAO.deleteNotice(id)) {
                loadNoticesData();
            }
        }
    }

    // ==========================================
    // DATA LOADERS & HELPERS
    // ==========================================
    public void refreshAllData() {
        refreshOverviewKpis();
        loadStudentsData(null);
        loadLecturersData();
        loadOfficersData();
        loadCoursesData();
        loadTimetableData();
        loadNoticesData();
    }

    private void refreshOverviewKpis() {
        if (kpiStudentsVal != null) kpiStudentsVal.setText(String.valueOf(studentDAO.countStudents()));
        if (kpiLecturersVal != null) kpiLecturersVal.setText(String.valueOf(lecturerDAO.countLecturers()));
        if (kpiOfficersVal != null) kpiOfficersVal.setText(String.valueOf(officerDAO.countOfficers()));
        if (kpiCoursesVal != null) kpiCoursesVal.setText(String.valueOf(courseDAO.countCourses()));
    }

    private void loadStudentsData(String query) {
        studentsModel.setRowCount(0);
        List<Student> list = (query == null) ? studentDAO.getAllStudents() : studentDAO.searchStudents(query);
        for (Student s : list) {
            studentsModel.addRow(new Object[]{
                s.getUserId(), s.getRegNo(), s.getFullName(), s.getEmail(), s.getPhone(),
                s.getDepartment(), s.getBatch(), s.getStudentStatus()
            });
        }
    }

    private void loadLecturersData() {
        lecturersModel.setRowCount(0);
        List<Lecturer> list = lecturerDAO.getAllLecturers();
        for (Lecturer l : list) {
            lecturersModel.addRow(new Object[]{
                l.getUserId(), l.getFullName(), l.getUsername(), l.getEmail(), l.getPhone(),
                l.getDepartment(), l.getDesignation()
            });
        }
    }

    private void loadOfficersData() {
        officersModel.setRowCount(0);
        List<TechnicalOfficer> list = officerDAO.getAllOfficers();
        for (TechnicalOfficer o : list) {
            officersModel.addRow(new Object[]{
                o.getUserId(), o.getFullName(), o.getUsername(), o.getEmail(), o.getPhone(), o.getDepartment()
            });
        }
    }

    private void loadCoursesData() {
        coursesModel.setRowCount(0);
        List<Course> list = courseDAO.getAllCourses();
        for (Course c : list) {
            coursesModel.addRow(new Object[]{
                c.getCourseId(), c.getCourseCode(), c.getCourseName(), c.getCredit(),
                c.getDepartment(), c.getSemester(), c.getLecturerName() != null ? c.getLecturerName() : "Unassigned"
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
        List<Notice> list = noticeDAO.getAllNotices();
        for (Notice n : list) {
            noticesModel.addRow(new Object[]{
                n.getNoticeId(), n.getTitle(), n.getTargetAudience(),
                n.getPostedByName() != null ? n.getPostedByName() : "Admin",
                n.getPostedDate() != null ? n.getPostedDate().toLocalDate() : "",
                n.isPinned() ? "YES" : "NO"
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
}
