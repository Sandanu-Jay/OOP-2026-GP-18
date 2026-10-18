package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Student;
import com.faculty.management.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Undergraduate / Student records.
 * Uses PreparedStatement and database transactions.
 */
public class StudentDAO implements IStudentDAO {

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "s.reg_no, s.department, s.batch, s.student_status " +
                     "FROM users u " +
                     "JOIN students s ON u.user_id = s.student_id " +
                     "ORDER BY s.reg_no ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    "", // omit hash in list view
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("profile_image"),
                    rs.getString("reg_no"),
                    rs.getString("department"),
                    rs.getString("batch"),
                    rs.getString("student_status")
                );
                students.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all students: " + e.getMessage());
        }
        return students;
    }

    public List<Student> searchStudents(String query) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.profile_image, " +
                     "s.reg_no, s.department, s.batch, s.student_status " +
                     "FROM users u " +
                     "JOIN students s ON u.user_id = s.student_id " +
                     "WHERE s.reg_no LIKE ? OR u.full_name LIKE ? OR s.department LIKE ? " +
                     "ORDER BY s.reg_no ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String term = "%" + query + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "",
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("profile_image"),
                        rs.getString("reg_no"),
                        rs.getString("department"),
                        rs.getString("batch"),
                        rs.getString("student_status")
                    );
                    students.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        return students;
    }

    public boolean addStudent(Student student, String plainPassword) {
        String insertUserSql = "INSERT INTO users (username, password_hash, full_name, email, phone, role, profile_image) " +
                               "VALUES (?, ?, ?, ?, ?, 'STUDENT', ?)";
        String insertStudentSql = "INSERT INTO students (student_id, reg_no, department, batch, student_status) " +
                                  "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            int userId = -1;
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, student.getUsername());
                userStmt.setString(2, PasswordUtil.hashPassword(plainPassword));
                userStmt.setString(3, student.getFullName());
                userStmt.setString(4, student.getEmail());
                userStmt.setString(5, student.getPhone());
                userStmt.setString(6, student.getProfileImage());
                userStmt.executeUpdate();

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }

            if (userId == -1) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement stuStmt = conn.prepareStatement(insertStudentSql)) {
                stuStmt.setInt(1, userId);
                stuStmt.setString(2, student.getRegNo());
                stuStmt.setString(3, student.getDepartment());
                stuStmt.setString(4, student.getBatch());
                stuStmt.setString(5, student.getStudentStatus());
                stuStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        String updateUserSql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        String updateStudentSql = "UPDATE students SET reg_no = ?, department = ?, batch = ?, student_status = ? WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement uStmt = conn.prepareStatement(updateUserSql)) {
                uStmt.setString(1, student.getFullName());
                uStmt.setString(2, student.getEmail());
                uStmt.setString(3, student.getPhone());
                uStmt.setInt(4, student.getUserId());
                uStmt.executeUpdate();
            }

            try (PreparedStatement sStmt = conn.prepareStatement(updateStudentSql)) {
                sStmt.setString(1, student.getRegNo());
                sStmt.setString(2, student.getDepartment());
                sStmt.setString(3, student.getBatch());
                sStmt.setString(4, student.getStudentStatus());
                sStmt.setInt(5, student.getUserId());
                sStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        // Cascade delete on foreign key will also delete from students table
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    public int countStudents() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
        }
        return 0;
    }
}
