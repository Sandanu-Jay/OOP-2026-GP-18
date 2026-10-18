package com.faculty.management.dao;

import com.faculty.management.model.Notice;
import java.util.List;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Faculty Notices.
 */
public interface INoticeDAO {

    List<Notice> getAllNotices();

    List<Notice> getNoticesForAudience(String role);

    boolean addNotice(Notice notice);

    boolean updateNotice(Notice notice);

    boolean deleteNotice(int noticeId);
}
