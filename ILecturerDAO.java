package com.faculty.management.dao;

import com.faculty.management.model.Lecturer;
import java.util.List;

/**
 * Data Access Object Interface for Lecturer operations.
 */
public interface ILecturerDAO {

    List<Lecturer> getAllLecturers();

    List<Lecturer> searchLecturers(String query);

    boolean addLecturer(Lecturer lecturer, String plainPassword);

    boolean updateLecturer(Lecturer lecturer);

    boolean deleteLecturer(int lecturerId);

    int countLecturers();
}
