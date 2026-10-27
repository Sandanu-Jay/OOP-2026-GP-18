package com.faculty.management.ui;

import com.faculty.management.model.User;

/**
 * OOP CONCEPT: INTERFACE FOR LOOSE COUPLING
 * Navigation interface to switch views across the application without
 * tight coupling between UI components and the main window frame.
 */
public interface ViewNavigator {

    void showLogin();

    void showDashboard(User user);

    void logout();
}
