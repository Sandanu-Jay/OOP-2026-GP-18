/*
 * EASY EVALUATION NOTE
 * This class handles TechnicalOfficer database operations.
 * Explain it using: SQL -> PreparedStatement -> ResultSet -> Java object -> exception handling.
 */
package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.TechnicalOfficer;
import com.faculty.management.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TechnicalOfficerDAO {

    public List<TechnicalOfficer> getAllOfficers() {
        List<TechnicalOfficer> officers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "t.department " +
                     "FROM users u " +
                     "JOIN technical_officers t ON u.user_id = t.officer_id " +
                     "ORDER BY u.full_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TechnicalOfficer to = new TechnicalOfficer(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    "",
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("profile_image"),
                    rs.getString("department")
                );
                officers.add(to);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching technical officers: " + e.getMessage());
        }
        return officers;
    }

    public List<TechnicalOfficer> searchOfficers(String query) {
        List<TechnicalOfficer> officers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "t.department " +
                     "FROM users u " +
                     "JOIN technical_officers t ON u.user_id = t.officer_id " +
                     "WHERE u.full_name LIKE ? OR t.department LIKE ? " +
                     "ORDER BY u.full_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String term = "%" + query + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TechnicalOfficer to = new TechnicalOfficer(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "",
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("profile_image"),
                        rs.getString("department")
                    );
                    officers.add(to);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching officers: " + e.getMessage());
        }
        return officers;
    }

    public boolean addOfficer(TechnicalOfficer officer, String plainPassword) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, phone, role, profile_image) " +
                               "VALUES (?, ?, ?, ?, ?, 'TECHNICAL_OFFICER', ?)";
        String insertOfficerSql = "INSERT INTO technical_officers (officer_id, department) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int userId = -1;
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, officer.getUsername());
                userStmt.setString(2, PasswordUtil.hashPassword(plainPassword));
                userStmt.setString(3, officer.getFullName());
                userStmt.setString(4, officer.getEmail());
                userStmt.setString(5, officer.getPhone());
                userStmt.setString(6, officer.getProfileImage());
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

            try (PreparedStatement offStmt = conn.prepareStatement(insertOfficerSql)) {
                offStmt.setInt(1, userId);
                offStmt.setString(2, officer.getDepartment());
                offStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding technical officer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOfficer(TechnicalOfficer officer) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        String updateOfficerSql = "UPDATE technical_officers SET department = ? WHERE officer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement uStmt = conn.prepareStatement(updateUserSql)) {
                uStmt.setString(1, officer.getFullName());
                uStmt.setString(2, officer.getEmail());
                uStmt.setString(3, officer.getPhone());
                uStmt.setInt(4, officer.getUserId());
                uStmt.executeUpdate();
            }

            try (PreparedStatement oStmt = conn.prepareStatement(updateOfficerSql)) {
                oStmt.setString(1, officer.getDepartment());
                oStmt.setInt(2, officer.getUserId());
                oStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating technical officer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOfficer(int officerId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, officerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting officer: " + e.getMessage());
            return false;
        }
    }

    public int countOfficers() {
        String sql = "SELECT COUNT(*) FROM technical_officers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting officers: " + e.getMessage());
        }
        return 0;
    }
}
