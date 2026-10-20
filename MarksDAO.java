package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.EligibilityStatus;
import com.faculty.management.model.Marks;
import com.faculty.management.util.GradeCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Student Marks, CA Eligibility,
 * UGC Circular 12-2024 Grade calculations, and SGPA/CGPA.
 */
public class MarksDAO implements IMarksDAO {

    /**
     * Inserts or updates marks with encapsulated calculation:
     * CA = quiz*0.25 + mid*0.50 + assignment*0.25
     * Total = CA*0.40 + finalExam*0.60
     * Grade & GPV derived via GradeCalculator.
     */
    public boolean saveOrUpdateMarks(Marks m) {
        // Automatic calculation
        double ca = (m.getQuizMark() * 0.25) + (m.getMidMark() * 0.50) + (m.getAssignmentMark() * 0.25);
        double total = (ca * 0.40) + (m.getFinalExamMark() * 0.60);
        String grade = GradeCalculator.getGrade(total);
        double gpv = GradeCalculator.getGpv(total);

        m.setCaMark(ca);
        m.setTotalMark(total);
        m.setGrade(grade);
        m.setGpv(gpv);

        String sql = "INSERT INTO marks (student_id, course_id, quiz_mark, mid_mark, assignment_mark, " +
                     "ca_mark, final_exam_mark, total_mark, grade, gpv) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE quiz_mark = VALUES(quiz_mark), mid_mark = VALUES(mid_mark), " +
                     "assignment_mark = VALUES(assignment_mark), ca_mark = VALUES(ca_mark), " +
                     "final_exam_mark = VALUES(final_exam_mark), total_mark = VALUES(total_mark), " +
                     "grade = VALUES(grade), gpv = VALUES(gpv)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, m.getStudentId());
            stmt.setInt(2, m.getCourseId());
            stmt.setDouble(3, m.getQuizMark());
            stmt.setDouble(4, m.getMidMark());
            stmt.setDouble(5, m.getAssignmentMark());
            stmt.setDouble(6, ca);
            stmt.setDouble(7, m.getFinalExamMark());
            stmt.setDouble(8, total);
            stmt.setString(9, grade);
            stmt.setDouble(10, gpv);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving marks: " + e.getMessage());
            return false;
        }
    }

    public List<Marks> getAllMarksByCourse(int courseId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.mark_id, s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, " +
                     "c.course_name, c.credit, m.quiz_mark, m.mid_mark, m.assignment_mark, m.ca_mark, " +
                     "m.final_exam_mark, m.total_mark, m.grade, m.gpv " +
                     "FROM students s " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN enrollments e ON s.student_id = e.student_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN marks m ON s.student_id = m.student_id AND c.course_id = m.course_id " +
                     "WHERE c.course_id = ? " +
                     "ORDER BY s.reg_no ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Marks m = new Marks(
                        rs.getInt("mark_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getDouble("quiz_mark"),
                        rs.getDouble("mid_mark"),
                        rs.getDouble("assignment_mark"),
                        rs.getDouble("ca_mark"),
                        rs.getDouble("final_exam_mark"),
                        rs.getDouble("total_mark"),
                        rs.getString("grade") != null ? rs.getString("grade") : "Pending",
                        rs.getDouble("gpv")
                    );
                    m.setStudentRegNo(rs.getString("reg_no"));
                    m.setStudentName(rs.getString("full_name"));
                    m.setCourseCode(rs.getString("course_code"));
                    m.setCourseName(rs.getString("course_name"));
                    m.setCredit(rs.getInt("credit"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching marks by course: " + e.getMessage());
        }
        return list;
    }

    public List<Marks> getMarksByStudent(int studentId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.mark_id, s.student_id, s.reg_no, u.full_name, c.course_id, c.course_code, " +
                     "c.course_name, c.credit, m.quiz_mark, m.mid_mark, m.assignment_mark, m.ca_mark, " +
                     "m.final_exam_mark, m.total_mark, m.grade, m.gpv " +
                     "FROM enrollments e " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "JOIN users u ON s.student_id = u.user_id " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "LEFT JOIN marks m ON s.student_id = m.student_id AND c.course_id = m.course_id " +
                     "WHERE s.student_id = ? " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Marks m = new Marks(
                        rs.getInt("mark_id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getDouble("quiz_mark"),
                        rs.getDouble("mid_mark"),
                        rs.getDouble("assignment_mark"),
                        rs.getDouble("ca_mark"),
                        rs.getDouble("final_exam_mark"),
                        rs.getDouble("total_mark"),
                        rs.getString("grade") != null ? rs.getString("grade") : "Pending",
                        rs.getDouble("gpv")
                    );
                    m.setStudentRegNo(rs.getString("reg_no"));
                    m.setStudentName(rs.getString("full_name"));
                    m.setCourseCode(rs.getString("course_code"));
                    m.setCourseName(rs.getString("course_name"));
                    m.setCredit(rs.getInt("credit"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student marks: " + e.getMessage());
        }
        return list;
    }

    /**
     * Calculates overall GPA for a student:
     * GPA = Sum(GPV * Credit) / Sum(Credit)
     */
    public double calculateStudentGpa(int studentId) {
        String sql = "SELECT SUM(m.gpv * c.credit) AS total_points, SUM(c.credit) AS total_credits " +
                     "FROM marks m " +
                     "JOIN courses c ON m.course_id = c.course_id " +
                     "WHERE m.student_id = ? AND m.grade IS NOT NULL AND m.grade != 'Pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalPoints = rs.getDouble("total_points");
                    int totalCredits = rs.getInt("total_credits");
                    if (totalCredits > 0) {
                        return totalPoints / totalCredits;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating GPA: " + e.getMessage());
        }
        return 0.00;
    }

    /**
     * Fetches comprehensive course final-exam eligibility combining:
     * - CA Marks >= 40%
     * - Attendance >= 80% (taking approved medicals into account)
     */
    public List<EligibilityStatus> getOverallEligibilityByCourse(int courseId) {
        List<EligibilityStatus> list = new ArrayList<>();
        AttendanceDAO attDao = new AttendanceDAO();
        List<Marks> marksList = getAllMarksByCourse(courseId);

        for (Marks m : marksList) {
            var attSummary = attDao.getStudentAttendanceSummary(m.getStudentId(), courseId);
            double attPct = (attSummary != null) ? attSummary.getEffectivePercentage() : 0.0;
            
            EligibilityStatus status = new EligibilityStatus(
                m.getStudentId(),
                m.getStudentRegNo(),
                m.getStudentName(),
                courseId,
                m.getCourseCode(),
                m.getCourseName(),
                m.getCaMark(),
                attPct
            );
            list.add(status);
        }
        return list;
    }
}
