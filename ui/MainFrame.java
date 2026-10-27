package com.faculty.management.ui;

import com.faculty.management.model.User;
import com.faculty.management.util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Top-Level Application Window Frame.
 * Implements ViewNavigator interface for loose coupling and polymorphic view switching.
 */
public class MainFrame extends JFrame implements ViewNavigator {

    private final CardLayout cardLayout;
    private final JPanel rootPanel;
    private final LoginView loginView;

    public MainFrame() {
        setTitle("University Faculty Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        // CardLayout container
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        // Initial Login View
        loginView = new LoginView(this);
        rootPanel.add(loginView, "LOGIN");

        add(rootPanel, BorderLayout.CENTER);
        cardLayout.show(rootPanel, "LOGIN");
    }

    @Override
    public void showLogin() {
        cardLayout.show(rootPanel, "LOGIN");
    }

    @Override
    public void showDashboard(User user) {
        if (user == null) {
            showLogin();
            return;
        }

        String role = user.getRole().toUpperCase();
        JPanel dashboardView;

        switch (role) {
            case "ADMIN" -> dashboardView = new AdminDashboardView(this, user);
            case "LECTURER" -> dashboardView = new LecturerDashboardView(this, user);
            case "TECHNICAL_OFFICER" -> dashboardView = new TechnicalOfficerDashboardView(this, user);
            case "STUDENT" -> dashboardView = new StudentDashboardView(this, user);
            default -> {
                JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String cardKey = "DASHBOARD_" + role;
        rootPanel.add(dashboardView, cardKey);
        cardLayout.show(rootPanel, cardKey);
    }

    @Override
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to sign out?",
            "Sign Out Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            showLogin();
        }
    }
}
