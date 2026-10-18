package com.faculty.management.dao;

import com.faculty.management.model.Timetable;
import java.util.List;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Timetables and Room Allocations.
 */

public interface ITimetableDAO {

    List<Timetable> getAllTimetables();

    List<Timetable> getTimetablesByDepartment(String dept);

    List<Timetable> getTimetablesByLecturer(int lecturerId);

    List<Timetable> getTimetablesForStudent(int studentId);

    boolean addTimetableEntry(Timetable t);

    boolean updateTimetableEntry(Timetable t);

    boolean deleteTimetableEntry(int timetableId);
}
