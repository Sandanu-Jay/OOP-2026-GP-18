package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Attendance;
import com.faculty.management.model.AttendanceSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Attendance records and calculating
 * 80% attendance eligibility with approved medical integration.
 */
public class AttendanceDAO implements IAttendanceDAO {

    /**
     * Records or updates attendance for a specific session.
     */
    public boolean markAttendance(int studentId, int courseId, String sessionType, 
                                  int sessionNumber, Date sessionDate, String status) {
        String sql = "INSERT INTO attendance (student_id, course_id, session_type, session_number, session_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE session_date = VALUES(session_date), status = VALUES(status)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setString(3, sessionType);
            stmt.setInt(4, sessionNumber);
            stmt.setDate(5, sessionDate);
            stmt.setString(6, status);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets individual attendance summary for a student in a course.
     */
    public AttendanceSummary getStudentAttendanceSummary(int studentId, int courseId) {
        String sql = "SELECT " +
                     "s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name, " +
                     "COUNT(CASE WHEN a.session_type = 'THEORY' AND a.status = 'PRESENT' THEN 1 END) AS theory_count, " +
                     "COUNT(CASE WHEN a.session_type = 'PRACTICAL' AND a.status = 'PRESENT' THEN 1 END) AS practical_count, " +
                     "(SELECT COUNT(*) FROM medical_records m WHERE m.student_id = s.student_id AND m.course_id = c.course_id AND m.is_approved = 'APPROVED') AS approved_medicals " +
                     "FROM students s " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "CROSS JOIN courses c " +
                     "LEFT JOIN attendance a ON s.student_id = a.student_id AND c.course_id = a.course_id " +
                     "WHERE s.student_id = ? AND c.course_id = ? " +
                     "GROUP BY s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AttendanceSummary(
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("full_name"),
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("theory_count"),
                        rs.getInt("practical_count"),
                        rs.getInt("approved_medicals")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating attendance summary: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns batch attendance summary for all students enrolled in a course.
     */
    public List<AttendanceSummary> getBatchAttendanceSummary(int courseId) {
        List<AttendanceSummary> list = new ArrayList<>();
        String sql = "SELECT " +
                     "s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name, " +
                     "COUNT(CASE WHEN a.session_type = 'THEORY' AND a.status = 'PRESENT' THEN 1 END) AS theory_count, " +
                     "COUNT(CASE WHEN a.session_type = 'PRACTICAL' AND a.status = 'PRESENT' THEN 1 END) AS practical_count, " +
                     "(SELECT COUNT(*) FROM medical_records m WHERE m.student_id = s.student_id AND m.course_id = c.course_id AND m.is_approved = 'APPROVED') AS approved_medicals " +
                     "FROM students s " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN enrollments e ON s.student_id = e.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN attendance a ON s.student_id = a.student_id AND c.course_id = a.course_id " +
                     "WHERE c.course_id = ? " +
                     "GROUP BY s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name " +
                     "ORDER BY s.reg_no ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceSummary summary = new AttendanceSummary(
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("full_name"),
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("theory_count"),
                        rs.getInt("practical_count"),
                        rs.getInt("approved_medicals")
                    );
                    list.add(summary);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching batch attendance summary: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns attendance summaries for all courses enrolled by a student.
     */
    public List<AttendanceSummary> getStudentAllCourseSummaries(int studentId) {
        List<AttendanceSummary> list = new ArrayList<>();
        String sql = "SELECT " +
                     "s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name, " +
                     "COUNT(CASE WHEN a.session_type = 'THEORY' AND a.status = 'PRESENT' THEN 1 END) AS theory_count, " +
                     "COUNT(CASE WHEN a.session_type = 'PRACTICAL' AND a.status = 'PRESENT' THEN 1 END) AS practical_count, " +
                     "(SELECT COUNT(*) FROM medical_records m WHERE m.student_id = s.student_id AND m.course_id = c.course_id AND m.is_approved = 'APPROVED') AS approved_medicals " +
                     "FROM students s " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN enrollments e ON s.student_id = e.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN attendance a ON s.student_id = a.student_id AND c.course_id = a.course_id " +
                     "WHERE s.student_id = ? " +
                     "GROUP BY s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, c.course_name " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceSummary summary = new AttendanceSummary(
                        rs.getInt("student_id"),
                        rs.getString("reg_no"),
                        rs.getString("full_name"),
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("theory_count"),
                        rs.getInt("practical_count"),
                        rs.getInt("approved_medicals")
                    );
                    list.add(summary);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student attendance: " + e.getMessage());
        }
        return list;
    }

    /**
     * Gets detailed session records for a course.
     */
    public List<Attendance> getAttendanceRecords(int courseId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.attendance_id, a.student_id, s.reg_no, u.full_name, a.course_id, " +
                     "c.course_code, a.session_type, a.session_number, a.session_date, a.status " +
                     "FROM attendance a " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN courses c ON a.course_id = c.course_id " +
                     "WHERE a.course_id = ? " +
                     "ORDER BY a.session_type ASC, a.session_number ASC, s.reg_no ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("session_type"),
                        rs.getInt("session_number"),
                        rs.getDate("session_date").toLocalDate(),
                        rs.getString("status")
                    );
                    a.setStudentRegNo(rs.getString("reg_no"));
                    a.setStudentName(rs.getString("full_name"));
                    a.setCourseCode(rs.getString("course_code"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance records: " + e.getMessage());
        }
        return list;
    }
}
