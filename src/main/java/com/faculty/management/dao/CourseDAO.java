/*
 * EASY EVALUATION NOTE
 * This class handles Course database operations.
 * Explain it using: SQL -> PreparedStatement -> ResultSet -> Java object -> exception handling.
 */
package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.level, c.semester, " +
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
                c.setLevel(rs.getInt("level"));
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
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.level, c.semester, " +
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
                    c.setLevel(rs.getInt("level"));
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
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.level, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "JOIN enrollments e ON c.course_id = e.course_id " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "WHERE e.student_id = ? AND LOWER(TRIM(c.department)) = LOWER(TRIM((SELECT s.department FROM students s WHERE s.student_id = ?))) " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
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
                    c.setLevel(rs.getInt("level"));
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
        String sql = "SELECT c.course_id, c.course_code, c.course_name, c.credit, c.department, c.level, c.semester, " +
                     "c.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.lecturer_id = u.user_id " +
                     "WHERE c.course_id NOT IN (" +
                     "    SELECT course_id FROM enrollments WHERE student_id = ?" +
                     ") AND LOWER(TRIM(c.department)) = LOWER(TRIM((SELECT s.department FROM students s WHERE s.student_id = ?))) " +
                     "ORDER BY c.course_code ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
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
                    c.setLevel(rs.getInt("level"));
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
        String sql = "INSERT INTO enrollments (student_id, course_id, academic_year, status) " +
                     "SELECT s.student_id, c.course_id, ?, 'ENROLLED' FROM students s JOIN courses c " +
                     "ON c.course_id = ? AND LOWER(TRIM(c.department)) = LOWER(TRIM(s.department)) " +
                     "WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (academicYear != null && !academicYear.trim().isEmpty()) ? academicYear.trim() : "2023/2024");
            stmt.setInt(2, courseId);
            stmt.setInt(3, studentId);

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
        String sql = "INSERT INTO courses (course_code, course_name, credit, department, level, semester, lecturer_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredit());
            stmt.setString(4, course.getDepartment());
            stmt.setInt(5, course.getLevel());
            stmt.setInt(6, course.getSemester());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                stmt.setInt(7, course.getLecturerId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credit = ?, department = ?, level = ?, " +
                     "semester = ?, lecturer_id = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredit());
            stmt.setString(4, course.getDepartment());
            stmt.setInt(5, course.getLevel());
            stmt.setInt(6, course.getSemester());
            if (course.getLecturerId() != null && course.getLecturerId() > 0) {
                stmt.setInt(7, course.getLecturerId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setInt(8, course.getCourseId());

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

    // --- Course Materials (actual files stored in MySQL) ---

    public List<CourseMaterial> getMaterialsByCourse(int courseId) {
        List<CourseMaterial> list = new ArrayList<>();
        String sql = "SELECT material_id, course_id, title, description, file_type, file_name, uploaded_date " +
                     "FROM course_materials WHERE course_id = ? ORDER BY uploaded_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseMaterial cm = new CourseMaterial();
                    cm.setMaterialId(rs.getInt("material_id"));
                    cm.setCourseId(rs.getInt("course_id"));
                    cm.setTitle(rs.getString("title"));
                    cm.setDescription(rs.getString("description"));
                    cm.setFileType(rs.getString("file_type"));
                    cm.setFileName(rs.getString("file_name"));
                    Timestamp uploaded = rs.getTimestamp("uploaded_date");
                    if (uploaded != null) cm.setUploadedDate(uploaded.toLocalDateTime());
                    list.add(cm);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching course materials: " + e.getMessage());
        }
        return list;
    }

    public boolean addCourseMaterial(CourseMaterial material, int lecturerId) {
        if (!isLecturerAssignedToCourse(lecturerId, material.getCourseId())) return false;
        String sql = "INSERT INTO course_materials " +
                     "(course_id, title, description, file_type, file_name, file_data) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, material.getCourseId());
            stmt.setString(2, material.getTitle());
            stmt.setString(3, material.getDescription());
            stmt.setString(4, material.getFileType());
            stmt.setString(5, material.getFileName());
            stmt.setBytes(6, material.getFileData());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error uploading course material: " + e.getMessage());
            return false;
        }
    }

    public byte[] getCourseMaterialFile(int materialId, int studentId) {
        String sql = "SELECT cm.file_data FROM course_materials cm " +
                     "JOIN enrollments e ON e.course_id = cm.course_id " +
                     "WHERE cm.material_id = ? AND e.student_id = ? " +
                     "AND e.status IN ('ENROLLED', 'REPEAT')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBytes("file_data");
            }
        } catch (SQLException e) {
            System.err.println("Error reading course material: " + e.getMessage());
        }
        return null;
    }

    public String getCourseMaterialFileName(int materialId, int studentId) {
        String sql = "SELECT cm.file_name FROM course_materials cm " +
                     "JOIN enrollments e ON e.course_id = cm.course_id " +
                     "WHERE cm.material_id = ? AND e.student_id = ? " +
                     "AND e.status IN ('ENROLLED', 'REPEAT')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("file_name");
            }
        } catch (SQLException e) {
            System.err.println("Error reading material file name: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteCourseMaterial(int materialId, int lecturerId) {
        String sql = "DELETE cm FROM course_materials cm " +
                     "JOIN courses c ON c.course_id = cm.course_id " +
                     "WHERE cm.material_id = ? AND c.lecturer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialId);
            stmt.setInt(2, lecturerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting course material: " + e.getMessage());
            return false;
        }
    }

    private boolean isLecturerAssignedToCourse(int lecturerId, int courseId) {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_id = ? AND lecturer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking lecturer course access: " + e.getMessage());
            return false;
        }
    }
}
