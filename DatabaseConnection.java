package com.faculty.management.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralized Database Connection Manager.
 * Reads database connection configuration from 'db.properties'.
 * Prevents repeating connection logic inside multiple classes.
 */
public class DatabaseConnection {

    private static String url = "jdbc:mysql://localhost:3306/faculty_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";

    static {
        loadProperties();
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private static void loadProperties() {
        Properties prop = new Properties();
        boolean loaded = false;

        // 1. Try classpath resource
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                prop.load(input);
                loaded = true;
            }
        } catch (Exception ignored) {}

        // 2. Try file in working directory or src
        if (!loaded) {
            String[] candidatePaths = {"db.properties", "src/main/resources/db.properties", "resources/db.properties"};
            for (String path : candidatePaths) {
                java.io.File file = new java.io.File(path);
                if (file.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                        prop.load(fis);
                        loaded = true;
                        break;
                    } catch (Exception ignored) {}
                }
            }
        }

        if (loaded) {
            if (prop.getProperty("db.url") != null) url = prop.getProperty("db.url");
            if (prop.getProperty("db.user") != null) user = prop.getProperty("db.user");
            if (prop.getProperty("db.password") != null) password = prop.getProperty("db.password");
        }
    }

    /**
     * Obtains a new database connection.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Quick utility to test database connectivity.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }
}
