package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.*;
import com.faculty.management.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & POLYMORPHISM
 * DAO responsible for User authentication and profile management.
 * Returns polymorphic User instances (Admin, Lecturer, TechnicalOfficer, Student).
 */
public class UserDAO implements IUserDAO {

    /**
     * Authenticates user credentials.
     * Demonstrates Polymorphism: Returns the specific subclass (Admin, Lecturer, Student, etc.)
     * referenced via the base class User.
     */
    public User authenticate(String username, String plainPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                        int userId = rs.getInt("user_id");
                        String role = rs.getString("role");
                        return loadSpecificUser(conn, userId, role, rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Helper to load role-specific details using Polymorphic instantiation.
     */
    private User loadSpecificUser(Connection conn, int userId, String role, ResultSet userRs) throws SQLException {
        String username = userRs.getString("username");
        String passwordHash = userRs.getString("password_hash");
        String fullName = userRs.getString("full_name");
        String email = userRs.getString("email");
        String phone = userRs.getString("phone");
        String profileImage = userRs.getString("profile_image");
        String address = "";
        try {
            address = userRs.getString("address");
            if (address == null) address = "";
        } catch (SQLException ignored) {}

        User user = null;
        switch (role) {
            case "ADMIN":
                user = new Admin(userId, username, passwordHash, fullName, email, phone, profileImage);
                break;

            case "LECTURER": {
                String sql = "SELECT department, designation FROM lecturers WHERE lecturer_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        String dept = rs.next() ? rs.getString("department") : "";
                        String desig = rs.getString("designation");
                        user = new Lecturer(userId, username, passwordHash, fullName, email, phone, profileImage, dept, desig);
                    }
                }
                break;
            }

            case "TECHNICAL_OFFICER": {
                String sql = "SELECT department FROM technical_officers WHERE officer_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        String dept = rs.next() ? rs.getString("department") : "";
                        user = new TechnicalOfficer(userId, username, passwordHash, fullName, email, phone, profileImage, dept);
                    }
                }
                break;
            }

            case "STUDENT": {
                String sql = "SELECT reg_no, department, batch, student_status FROM students WHERE student_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            user = new Student(userId, username, passwordHash, fullName, email, phone, profileImage,
                                    rs.getString("reg_no"), rs.getString("department"), rs.getString("batch"),
                                    rs.getString("student_status"));
                        }
                    }
                }
                if (user == null) {
                    user = new Student(userId, username, passwordHash, fullName, email, phone, profileImage, "", "", "", "NORMAL");
                }
                break;
            }

            default:
                return null;
        }

        if (user != null) {
            user.setAddress(address);
        }
        return user;
    }

    /**
     * Updates editable contact details and profile picture.
     * Prevents editing username or password in this flow.
     */
    public boolean updateContactAndProfilePicture(int userId, String email, String phone, String profileImage) {
        return updateContactAndProfilePicture(userId, email, phone, "", profileImage);
    }

    public boolean updateContactAndProfilePicture(int userId, String email, String phone, String address, String profileImage) {
        String sql = "UPDATE users SET email = ?, phone = ?, address = ?, profile_image = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, address != null ? address : "");
            stmt.setString(4, profileImage);
            stmt.setInt(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfileImage(int userId, String profileImage) {
        String sql = "UPDATE users SET profile_image = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileImage);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile image: " + e.getMessage());
            return false;
        }
    }

    /**
     * Admin full profile update.
     */
    public boolean updateUser(int userId, String fullName, String email, String phone, String role) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, role);
            stmt.setInt(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int countUsersByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting users by role: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getTotalUsersCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total users count: " + e.getMessage());
        }
        return 0;
    }
}
