/*
 * EASY EVALUATION NOTE
 * This class creates the JDBC connection used to connect Java with MySQL.
 */
package com.faculty.management.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


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
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                if (prop.getProperty("db.url") != null) url = prop.getProperty("db.url");
                if (prop.getProperty("db.user") != null) user = prop.getProperty("db.user");
                if (prop.getProperty("db.password") != null) password = prop.getProperty("db.password");
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties, using default credentials: " + e.getMessage());
        }
    }

    /**
     * Obtains a new database connection.
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
