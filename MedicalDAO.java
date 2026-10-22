package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for Undergraduate Medical records.
 */

public class MedicalDAO implements IMedicalDAO {

    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT m.medical_id, m.student_id, s.reg_no, u.full_name, m.course_id, c.course_code, " +
                     "m.session_type, m.session_number, m.medical_date, m.reason, m.document_info, m.is_approved, " +
                     "m.submitted_date, m.comments " +
                     "FROM medical_records m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN courses c ON m.course_id = c.course_id " +
                     "ORDER BY m.submitted_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String reason = "";
                try {
                    reason = rs.getString("reason");
                    if (reason == null) reason = "";
                } catch (SQLException ignored) {}

                MedicalRecord m = new MedicalRecord(
                    rs.getInt("medical_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("session_type"),
                    rs.getInt("session_number"),
                    rs.getDate("medical_date").toLocalDate(),
                    reason,
                    rs.getString("document_info"),
                    rs.getString("is_approved"),
                    rs.getString("comments")
                );
                m.setStudentRegNo(rs.getString("reg_no"));
                m.setStudentName(rs.getString("full_name"));
                m.setCourseCode(rs.getString("course_code"));
                m.setSubmittedDate(rs.getTimestamp("submitted_date").toLocalDateTime());
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medical records: " + e.getMessage());
        }
        return list;
    }

    public List<MedicalRecord> getMedicalRecordsByStudent(int studentId) {
        List<MedicalRecord> list = new ArrayList<>();
        String sql = "SELECT m.medical_id, m.student_id, s.reg_no, u.full_name, m.course_id, c.course_code, " +
                     "m.session_type, m.session_number, m.medical_date, m.reason, m.document_info, m.is_approved, " +
                     "m.submitted_date, m.comments " +
                     "FROM medical_records m " +
                     "JOIN students s ON m.student_id = s.student_id " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN courses c ON m.course_id = c.course_id " +
                     "WHERE m.student_id = ? " +
                     "ORDER BY m.submitted_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String reason = "";
                    try {
                        reason = rs.getString("reason");
                        if (reason == null) reason = "";
                    } catch (SQLException ignored) {}

                    MedicalRecord m = new MedicalRecord(
                        rs.getInt("medical_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("session_type"),
                        rs.getInt("session_number"),
                        rs.getDate("medical_date").toLocalDate(),
                        reason,
                        rs.getString("document_info"),
                        rs.getString("is_approved"),
                        rs.getString("comments")
                    );
                    m.setStudentRegNo(rs.getString("reg_no"));
                    m.setStudentName(rs.getString("full_name"));
                    m.setCourseCode(rs.getString("course_code"));
                    m.setSubmittedDate(rs.getTimestamp("submitted_date").toLocalDateTime());
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medical records by student: " + e.getMessage());
        }
        return list;
    }

    public boolean addMedicalRecord(MedicalRecord record) {
        String sql = "INSERT INTO medical_records (student_id, course_id, session_type, session_number, " +
                     "medical_date, reason, document_info, is_approved, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getCourseId());
            stmt.setString(3, record.getSessionType());
            stmt.setInt(4, record.getSessionNumber());
            stmt.setDate(5, Date.valueOf(record.getMedicalDate()));
            stmt.setString(6, record.getReason() != null ? record.getReason() : "");
            stmt.setString(7, record.getDocumentInfo());
            stmt.setString(8, record.getIsApproved());
            stmt.setString(9, record.getComments());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding medical record: " + e.getMessage());
            return false;
        }
    }

    public boolean updateMedicalRecord(MedicalRecord record) {
        String sql = "UPDATE medical_records SET session_type = ?, session_number = ?, medical_date = ?, " +
                     "reason = ?, document_info = ?, is_approved = ?, comments = ? WHERE medical_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, record.getSessionType());
            stmt.setInt(2, record.getSessionNumber());
            stmt.setDate(3, Date.valueOf(record.getMedicalDate()));
            stmt.setString(4, record.getReason() != null ? record.getReason() : "");
            stmt.setString(5, record.getDocumentInfo());
            stmt.setString(6, record.getIsApproved());
            stmt.setString(7, record.getComments());
            stmt.setInt(8, record.getMedicalId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            return false;
        }
    }

    public boolean updateApprovalStatus(int medicalId, String status, String comments) {
        String sql = "UPDATE medical_records SET is_approved = ?, comments = ? WHERE medical_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, comments);
            stmt.setInt(3, medicalId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medical status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMedicalRecord(int medicalId) {
        String sql = "DELETE FROM medical_records WHERE medical_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting medical record: " + e.getMessage());
            return false;
        }
    }

    public int countPendingMedicals() {
        String sql = "SELECT COUNT(*) FROM medical_records WHERE is_approved = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting pending medicals: " + e.getMessage());
        }
        return 0;
    }
}
