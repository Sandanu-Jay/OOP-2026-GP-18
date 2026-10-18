package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Timetable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Timetables and Room Allocations.
 */
public class TimetableDAO implements ITimetableDAO {

    public List<Timetable> getAllTimetables() {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.department, t.semester, t.day_of_week, t.start_time, " +
                     "t.end_time, t.course_id, c.course_code, c.course_name, t.venue, " +
                     "t.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM timetables t " +
                     "JOIN courses c ON t.course_id = c.course_id " +
                     "LEFT JOIN users u ON t.lecturer_id = u.user_id " +
                     "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), t.start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timetable t = new Timetable(
                    rs.getInt("timetable_id"),
                    rs.getString("department"),
                    rs.getInt("semester"),
                    rs.getString("day_of_week"),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getInt("course_id"),
                    rs.getString("venue"),
                    (Integer) rs.getObject("lecturer_id")
                );
                t.setCourseCode(rs.getString("course_code"));
                t.setCourseName(rs.getString("course_name"));
                t.setLecturerName(rs.getString("lecturer_name"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all timetables: " + e.getMessage());
        }
        return list;
    }

    public List<Timetable> getTimetablesByDepartment(String dept) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.department, t.semester, t.day_of_week, t.start_time, " +
                     "t.end_time, t.course_id, c.course_code, c.course_name, t.venue, " +
                     "t.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM timetables t " +
                     "JOIN courses c ON t.course_id = c.course_id " +
                     "LEFT JOIN users u ON t.lecturer_id = u.user_id " +
                     "WHERE t.department = ? " +
                     "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), t.start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dept);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timetable t = new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getInt("course_id"),
                        rs.getString("venue"),
                        (Integer) rs.getObject("lecturer_id")
                    );
                    t.setCourseCode(rs.getString("course_code"));
                    t.setCourseName(rs.getString("course_name"));
                    t.setLecturerName(rs.getString("lecturer_name"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching department timetables: " + e.getMessage());
        }
        return list;
    }

    public List<Timetable> getTimetablesByLecturer(int lecturerId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.department, t.semester, t.day_of_week, t.start_time, " +
                     "t.end_time, t.course_id, c.course_code, c.course_name, t.venue, " +
                     "t.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM timetables t " +
                     "JOIN courses c ON t.course_id = c.course_id " +
                     "LEFT JOIN users u ON t.lecturer_id = u.user_id " +
                     "WHERE t.lecturer_id = ? " +
                     "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), t.start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lecturerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timetable t = new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getInt("course_id"),
                        rs.getString("venue"),
                        rs.getInt("lecturer_id")
                    );
                    t.setCourseCode(rs.getString("course_code"));
                    t.setCourseName(rs.getString("course_name"));
                    t.setLecturerName(rs.getString("lecturer_name"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching lecturer timetables: " + e.getMessage());
        }
        return list;
    }

    public List<Timetable> getTimetablesForStudent(int studentId) {
        List<Timetable> list = new ArrayList<>();
        String sql = "SELECT t.timetable_id, t.department, t.semester, t.day_of_week, t.start_time, " +
                     "t.end_time, t.course_id, c.course_code, c.course_name, t.venue, " +
                     "t.lecturer_id, u.full_name AS lecturer_name " +
                     "FROM timetables t " +
                     "JOIN courses c ON t.course_id = c.course_id " +
                     "JOIN enrollments e ON c.course_id = e.course_id " +
                     "LEFT JOIN users u ON t.lecturer_id = u.user_id " +
                     "WHERE e.student_id = ? " +
                     "ORDER BY FIELD(t.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), t.start_time ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timetable t = new Timetable(
                        rs.getInt("timetable_id"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        rs.getString("day_of_week"),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getInt("course_id"),
                        rs.getString("venue"),
                        (Integer) rs.getObject("lecturer_id")
                    );
                    t.setCourseCode(rs.getString("course_code"));
                    t.setCourseName(rs.getString("course_name"));
                    t.setLecturerName(rs.getString("lecturer_name"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching student timetables: " + e.getMessage());
        }
        return list;
    }

    public boolean addTimetableEntry(Timetable t) {
        String sql = "INSERT INTO timetables (department, semester, day_of_week, start_time, end_time, " +
                     "course_id, venue, lecturer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getDepartment());
            stmt.setInt(2, t.getSemester());
            stmt.setString(3, t.getDayOfWeek());
            stmt.setTime(4, Time.valueOf(t.getStartTime()));
            stmt.setTime(5, Time.valueOf(t.getEndTime()));
            stmt.setInt(6, t.getCourseId());
            stmt.setString(7, t.getVenue());
            if (t.getLecturerId() != null && t.getLecturerId() > 0) {
                stmt.setInt(8, t.getLecturerId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding timetable entry: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTimetableEntry(Timetable t) {
        String sql = "UPDATE timetables SET department = ?, semester = ?, day_of_week = ?, " +
                     "start_time = ?, end_time = ?, course_id = ?, venue = ?, lecturer_id = ? " +
                     "WHERE timetable_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getDepartment());
            stmt.setInt(2, t.getSemester());
            stmt.setString(3, t.getDayOfWeek());
            stmt.setTime(4, Time.valueOf(t.getStartTime()));
            stmt.setTime(5, Time.valueOf(t.getEndTime()));
            stmt.setInt(6, t.getCourseId());
            stmt.setString(7, t.getVenue());
            if (t.getLecturerId() != null && t.getLecturerId() > 0) {
                stmt.setInt(8, t.getLecturerId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setInt(9, t.getTimetableId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating timetable entry: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTimetableEntry(int timetableId) {
        String sql = "DELETE FROM timetables WHERE timetable_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, timetableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting timetable entry: " + e.getMessage());
            return false;
        }
    }
}
