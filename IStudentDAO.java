package com.faculty.management.dao;

import com.faculty.management.model.Student;
import java.util.List;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Undergraduate / Student records.
 */

public interface IStudentDAO {

    List<Student> getAllStudents();

    List<Student> searchStudents(String query);

    boolean addStudent(Student student, String plainPassword);

    boolean updateStudent(Student student);

    boolean deleteStudent(int studentId);

    int countStudents();
}
