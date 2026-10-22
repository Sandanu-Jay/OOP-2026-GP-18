package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Courses and Learning Materials.
 */
public class CourseDAO implements ICourseDAO {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course c = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credit"),
                    rs.getString("department"),
                    rs.getInt("semester"),
                    (Integer) rs.getObject("lecturer_id")
                );
                c.setLecturerName(rs.getString("lecturer_name"));
                courses.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses: " + e.getMessage());
        }
        return courses;
    }

    public List<Course> getCoursesByLecturer(int lecturerId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "WHERE c.lecturer_id = ? " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        rs.getInt("lecturer_id")
                    );
                    c.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses by lecturer: " + e.getMessage());
        }
        return courses;
    }

    public List<Course> getCoursesByStudent(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "JOIN enrollments e ON c.course_id = e.course_id " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        (Integer) rs.getObject("lecturer_id")
                    );
                    c.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses by student: " + e.getMessage());
        }
        return courses;
    }

    public List<Course> getAvailableCoursesForStudent(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "WHERE c.course_id NOT IN (" +
                     "    SELECT course_id FROM enrollments WHERE student_id = ?" +
                     ") " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        (Integer) rs.getObject("lecturer_id")
                    );
                    c.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available courses for student: " + e.getMessage());
        }
        return courses;
    }

    public boolean enrollStudent(int studentId, int courseId, String academicYear) {
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, status) VALUES (?, ?, ?, 'ENROLLED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setString(3, (academicYear != null && !academicYear.trim().isEmpty()) ? academicYear.trim() : "2023/2024");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error enrolling student in course: " + e.getMessage());
            return false;
        }
    }

    public boolean dropCourse(int studentId, int courseId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error dropping course for student: " + e.getMessage());
            return false;
        }
    }

    public boolean isStudentEnrolled(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking course enrollment: " + e.getMessage());
        }
        return false;
    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, credit, department, semester, lecturer_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredit());
            stmt.setString(4, course.getDepartment());
            stmt.setInt(5, course.getSemester());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                stmt.setInt(6, course.getLecturerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credit = ?, department = ?, " +
                     "semester = ?, lecturer_id = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredit());
            stmt.setString(4, course.getDepartment());
            stmt.setInt(5, course.getSemester());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                stmt.setInt(6, course.getLecturerId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, course.getCourseId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false;
        }
    }

    public int countCourses() {
        String sql = "SELECT COUNT(*) FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error counting courses: " + e.getMessage());
        }
        return 0;
    }

    // --- Course Materials ---

    public List<CourseMaterial> getMaterialsByCourse(int courseId) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT m.material_id, m.course_id, c.course_code, m.title, m.description, " +
                     "m.file_type, m.file_path_or_url, m.uploaded_date " +
                     "FROM course_materials m " +
                     "JOIN courses c ON m.course_id = c.course_id " +
                     "WHERE m.course_id = ? ORDER BY m.uploaded_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseMaterial cm = new CourseMaterial(
                        rs.getInt("material_id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("file_type"),
                        rs.getString("file_path_or_url"),
                        rs.getTimestamp("uploaded_date").toLocalDateTime()
                    );
                    cm.setCourseCode(rs.getString("course_code"));
                    list.add(cm);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching materials: " + e.getMessage());
        }
        return list;
    }

    public boolean addCourseMaterial(CourseMaterial mat) {
        String sql = "INSERT INTO course_materials (course_id, title, description, file_type, file_path_or_url) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mat.getCourseId());
            stmt.setString(2, mat.getTitle());
            stmt.setString(3, mat.getDescription());
            stmt.setString(4, mat.getFileType());
            stmt.setString(5, mat.getFilePathOrUrl());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding material: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourseMaterial(int materialId) {
        String sql = "DELETE FROM course_materials WHERE material_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, materialId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting material: " + e.getMessage());
            return false;
        }
    }
}
