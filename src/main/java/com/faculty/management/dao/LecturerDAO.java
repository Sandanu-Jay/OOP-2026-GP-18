/*
 * EASY EVALUATION NOTE
 * This class handles Lecturer database operations.
 * Explain it using: SQL -> PreparedStatement -> ResultSet -> Java object -> exception handling.
 */
package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Lecturer;
import com.faculty.management.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {

    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "l.department, l.designation " +
                     "FROM users u " +
                     "JOIN lecturers l ON u.user_id = l.lecturer_id " +
                     "ORDER BY u.full_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Lecturer lec = new Lecturer(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    "",
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("profile_image"),
                    rs.getString("department"),
                    rs.getString("designation")
                );
                lecturers.add(lec);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all lecturers: " + e.getMessage());
        }
        return lecturers;
    }

    public List<Lecturer> searchLecturers(String query) {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "l.department, l.designation " +
                     "FROM users u " +
                     "JOIN lecturers l ON u.user_id = l.lecturer_id " +
                     "WHERE u.full_name LIKE ? OR l.department LIKE ? OR l.designation LIKE ? " +
                     "ORDER BY u.full_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String term = "%" + query + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Lecturer lec = new Lecturer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "",
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("profile_image"),
                        rs.getString("department"),
                        rs.getString("designation")
                    );
                    lecturers.add(lec);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching lecturers: " + e.getMessage());
        }
        return lecturers;
    }

    public boolean addLecturer(Lecturer lecturer, String plainPassword) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, phone, role, profile_image) " +
                               "VALUES (?, ?, ?, ?, ?, 'LECTURER', ?)";
        String insertLecturerSql = "INSERT INTO lecturers (lecturer_id, department, designation) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int userId = -1;
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, lecturer.getUsername());
                userStmt.setString(2, PasswordUtil.hashPassword(plainPassword));
                userStmt.setString(3, lecturer.getFullName());
                userStmt.setString(4, lecturer.getEmail());
                userStmt.setString(5, lecturer.getPhone());
                userStmt.setString(6, lecturer.getProfileImage());
                userStmt.executeUpdate();

                try (ResultSet rs = userStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    }
                }
            }

            if (userId == -1) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement lecStmt = conn.prepareStatement(insertLecturerSql)) {
                lecStmt.setInt(1, userId);
                lecStmt.setString(2, lecturer.getDepartment());
                lecStmt.setString(3, lecturer.getDesignation());
                lecStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding lecturer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateLecturer(Lecturer lecturer) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        String updateLecturerSql = "UPDATE lecturers SET department = ?, designation = ? WHERE lecturer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement uStmt = conn.prepareStatement(updateUserSql)) {
                uStmt.setString(1, lecturer.getFullName());
                uStmt.setString(2, lecturer.getEmail());
                uStmt.setString(3, lecturer.getPhone());
                uStmt.setInt(4, lecturer.getUserId());
                uStmt.executeUpdate();
            }

            try (PreparedStatement lStmt = conn.prepareStatement(updateLecturerSql)) {
                lStmt.setString(1, lecturer.getDepartment());
                lStmt.setString(2, lecturer.getDesignation());
                lStmt.setInt(3, lecturer.getUserId());
                lStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating lecturer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteLecturer(int lecturerId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lecturerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting lecturer: " + e.getMessage());
            return false;
        }
    }

    public int countLecturers() {
        String sql = "SELECT COUNT(*) FROM lecturers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting lecturers: " + e.getMessage());
        }
        return 0;
    }
}
