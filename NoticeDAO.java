package com.faculty.management.dao;

import com.faculty.management.database.DatabaseConnection;
import com.faculty.management.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: DATABASE HANDLING & EXCEPTION HANDLING
 * Data Access Object (DAO) for managing Faculty Notices and Announcements.
 */
public class NoticeDAO implements INoticeDAO {

    public List<Notice> getAllNotices() {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.notice_id, n.title, n.content, n.target_audience, n.posted_by, " +
                     "u.full_name, n.posted_date, n.is_pinned " +
                     "FROM notices n " +
                     "JOIN users u ON n.posted_by = u.user_id " +
                     "ORDER BY n.is_pinned DESC, n.posted_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Notice notice = new Notice(
                    rs.getInt("notice_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("target_audience"),
                    rs.getInt("posted_by"),
                    rs.getTimestamp("posted_date").toLocalDateTime(),
                    rs.getBoolean("is_pinned")
                );
                notice.setPostedByName(rs.getString("full_name"));
                list.add(notice);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notices: " + e.getMessage());
        }
        return list;
    }

    public List<Notice> getNoticesForAudience(String role) {
        List<Notice> list = new ArrayList<>();
        String targetAudience = switch (role.toUpperCase()) {
            case "STUDENT" -> "STUDENTS";
            case "LECTURER" -> "LECTURERS";
            case "TECHNICAL_OFFICER" -> "OFFICERS";
            default -> "ALL";
        };

        String sql = "SELECT n.notice_id, n.title, n.content, n.target_audience, n.posted_by, " +
                     "u.full_name, n.posted_date, n.is_pinned " +
                     "FROM notices n " +
                     "JOIN users u ON n.posted_by = u.user_id " +
                     "WHERE n.target_audience = 'ALL' OR n.target_audience = ? " +
                     "ORDER BY n.is_pinned DESC, n.posted_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, targetAudience);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notice notice = new Notice(
                        rs.getInt("notice_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("target_audience"),
                        rs.getInt("posted_by"),
                        rs.getTimestamp("posted_date").toLocalDateTime(),
                        rs.getBoolean("is_pinned")
                    );
                    notice.setPostedByName(rs.getString("full_name"));
                    list.add(notice);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notices for audience: " + e.getMessage());
        }
        return list;
    }

    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notices (title, content, target_audience, posted_by, is_pinned) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notice.getTitle());
            stmt.setString(2, notice.getContent());
            stmt.setString(3, notice.getTargetAudience());
            stmt.setInt(4, notice.getPostedBy());
            stmt.setBoolean(5, notice.isPinned());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding notice: " + e.getMessage());
            return false;
        }
    }

    public boolean updateNotice(Notice notice) {
        String sql = "UPDATE notices SET title = ?, content = ?, target_audience = ?, is_pinned = ? " +
                     "WHERE notice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notice.getTitle());
            stmt.setString(2, notice.getContent());
            stmt.setString(3, notice.getTargetAudience());
            stmt.setBoolean(4, notice.isPinned());
            stmt.setInt(5, notice.getNoticeId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating notice: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteNotice(int noticeId) {
        String sql = "DELETE FROM notices WHERE notice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, noticeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting notice: " + e.getMessage());
            return false;
        }
    }
}
