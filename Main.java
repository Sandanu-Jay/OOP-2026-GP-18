package com.faculty.management;

import com.faculty.management.ui.MainFrame;

import javax.swing.*;

/**
 * Main application entry point for the University Faculty Management System.
 * Pure Core Java Desktop Application (Classes, Interfaces, OOP Architecture).
 * Runs natively on any JDK without external GUI frameworks or Maven build requirements.
 */
public class Main {

    public static void main(String[] args) {
        // Apply native system Look and Feel for crisp modern controls
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Launch on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
