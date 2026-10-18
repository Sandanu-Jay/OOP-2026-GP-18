package com.faculty.management.dao;

import com.faculty.management.model.Attendance;
import com.faculty.management.model.AttendanceSummary;
import java.sql.Date;
import java.util.List;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Attendance and 80% Threshold Compliance.
 */
public interface IAttendanceDAO {

    boolean markAttendance(int studentId, int courseId, String sessionType, 
                           int sessionNumber, Date sessionDate, String status);

    AttendanceSummary getStudentAttendanceSummary(int studentId, int courseId);

    List<AttendanceSummary> getBatchAttendanceSummary(int courseId);

    List<AttendanceSummary> getStudentAllCourseSummaries(int studentId);

    List<Attendance> getAttendanceRecords(int courseId);
}
