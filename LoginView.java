package com.faculty.management.ui;

import com.faculty.management.model.User;
import com.faculty.management.service.IAuthService;
import com.faculty.management.service.impl.AuthServiceImpl;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Split-Screen Login View in Pure Java Swing.
 * Interacts with IAuthService (Loose Coupling via Interface).
 */
public class LoginView extends JPanel {

    private final ViewNavigator navigator;
    private final IAuthService authService;

    private JComboBox<String> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JLabel statusLabel;
    private JButton loginBtn;

    public LoginView(ViewNavigator navigator) {
        this(navigator, new AuthServiceImpl());
    }

    public LoginView(ViewNavigator navigator, IAuthService authService) {
        this.navigator = navigator;
        this.authService = authService;

        setLayout(new GridBagLayout());
        setBackground(UITheme.SIDEBAR_BG);

        initUI();
    }

    private void initUI() {
        // Main container card (880x560)
        JPanel mainCard = new JPanel(new GridLayout(1, 2, 0, 0));
        mainCard.setPreferredSize(new Dimension(880, 560));
        mainCard.setBackground(Color.WHITE);
        mainCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(30, 41, 59), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // --- Left Panel: University Branding ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(15, 23, 42)); // Deep Slate Navy
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel facultyBadge = new JLabel("FACULTY OF TECHNOLOGY");
        facultyBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        facultyBadge.setForeground(UITheme.PRIMARY);

        JLabel appTitle = new JLabel("<html>Faculty<br>Management<br>System</html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        appTitle.setForeground(Color.WHITE);

        JLabel appDesc = new JLabel("<html>University Academic & Administration Desktop Portal.<br><br>"
            + "• UGC Circular No. 12-2024 Grading Scheme<br>"
            + "• 80% Attendance Threshold Tracking<br>"
            + "• Continuous Assessment (CA ≥ 40%)<br>"
            + "• Pure Java OOP Architecture with Interfaces</html>");
        appDesc.setFont(UITheme.FONT_REGULAR);
        appDesc.setForeground(new Color(148, 163, 184));

        leftPanel.add(facultyBadge);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(appTitle);
        leftPanel.add(Box.createVerticalStrut(25));
        leftPanel.add(appDesc);
        leftPanel.add(Box.createVerticalGlue());

        JLabel versionLbl = new JLabel("Version 1.0.0 • Pure Core Java");
        versionLbl.setFont(UITheme.FONT_SMALL);
        versionLbl.setForeground(new Color(100, 116, 139));
        leftPanel.add(versionLbl);

        // --- Right Panel: Login Form ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(35, 40, 35, 40));

        JLabel welcomeLbl = new JLabel("Welcome Back");
        welcomeLbl.setFont(UITheme.FONT_TITLE);
        welcomeLbl.setForeground(UITheme.TEXT_DARK);

        JLabel subLbl = new JLabel("Sign in to access your portal");
        subLbl.setFont(UITheme.FONT_SUBTITLE);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        // Role Select
        JLabel roleLabel = new JLabel("Select Role");
        roleLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        roleLabel.setForeground(UITheme.TEXT_DARK);
        roleCombo = UIHelper.createComboBox(new String[]{"ADMIN", "LECTURER", "TECHNICAL_OFFICER", "STUDENT"});

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        userLabel.setForeground(UITheme.TEXT_DARK);
        usernameField = UIHelper.createTextField(20);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UITheme.FONT_REGULAR_BOLD);
        passLabel.setForeground(UITheme.TEXT_DARK);
        passwordField = UIHelper.createPasswordField(20);

        showPasswordCheck = new JCheckBox("Show Password");
        showPasswordCheck.setFont(UITheme.FONT_SMALL);
        showPasswordCheck.setForeground(UITheme.TEXT_MUTED);
        showPasswordCheck.setBackground(Color.WHITE);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.DANGER);

        // Sign In Button
        loginBtn = UIHelper.createPrimaryButton("Sign In to Portal");
        loginBtn.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> handleLogin());

        // Enter key listeners
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Quick Demo Fill Buttons
        JPanel demoPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        demoPanel.setBackground(Color.WHITE);
        demoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnDemoAdmin = createQuickBtn("Admin", "admin", "admin123", "ADMIN");
        JButton btnDemoLec = createQuickBtn("Lecturer", "lec_kamal", "lec123", "LECTURER");
        JButton btnDemoTo = createQuickBtn("Tech Officer", "to_saman", "to123", "TECHNICAL_OFFICER");
        JButton btnDemoStu = createQuickBtn("Student", "stu_chamindu", "student123", "STUDENT");

        demoPanel.add(btnDemoAdmin);
        demoPanel.add(btnDemoLec);
        demoPanel.add(btnDemoTo);
        demoPanel.add(btnDemoStu);

        JLabel demoHeader = new JLabel("Quick Demo Logins:");
        demoHeader.setFont(UITheme.FONT_KPI_TITLE);
        demoHeader.setForeground(UITheme.TEXT_MUTED);

        // Assemble Right Panel
        rightPanel.add(welcomeLbl);
        rightPanel.add(Box.createVerticalStrut(2));
        rightPanel.add(subLbl);
        rightPanel.add(Box.createVerticalStrut(18));

        rightPanel.add(roleLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(roleCombo);
        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(userLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(usernameField);
        rightPanel.add(Box.createVerticalStrut(12));

        rightPanel.add(passLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(showPasswordCheck);

        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(statusLabel);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(loginBtn);

        rightPanel.add(Box.createVerticalStrut(14));
        rightPanel.add(demoHeader);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(demoPanel);

        mainCard.add(leftPanel);
        mainCard.add(rightPanel);

        add(mainCard);
    }

    private JButton createQuickBtn(String text, String user, String pass, String role) {
        JButton btn = UIHelper.createSecondaryButton(text);
        btn.setFont(UITheme.FONT_SMALL);
        btn.addActionListener(e -> {
            usernameField.setText(user);
            passwordField.setText(pass);
            roleCombo.setSelectedItem(role);
            statusLabel.setText(" ");
        });
        return btn;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        loginBtn.setEnabled(false);
        statusLabel.setText("Authenticating credentials...");
        statusLabel.setForeground(UITheme.PRIMARY);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                try {
                    User user = get();
                    if (user == null) {
                        statusLabel.setText("Invalid username or password.");
                        statusLabel.setForeground(UITheme.DANGER);
                    } else if (selectedRole != null && !selectedRole.equalsIgnoreCase(user.getRole())) {
                        statusLabel.setText("Role mismatch! Account is registered as " + user.getRole());
                        statusLabel.setForeground(UITheme.WARNING);
                    } else {
                        statusLabel.setText("Success! Loading dashboard...");
                        statusLabel.setForeground(UITheme.SUCCESS);
                        navigator.showDashboard(user);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Database Connection Error. Check MySQL server.");
                    statusLabel.setForeground(UITheme.DANGER);
                }
            }
        };
        worker.execute();
    }
}
