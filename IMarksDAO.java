package com.faculty.management.dao;

import com.faculty.management.model.EligibilityStatus;
import com.faculty.management.model.Marks;
import java.util.List;

/**
 * OOP CONCEPT: ABSTRACTION & INTERFACE DESIGN
 * Data Access Object Interface for Student Marks, CA Eligibility, and GPA calculation.
 */
public interface IMarksDAO {

    boolean saveOrUpdateMarks(Marks m);

    List<Marks> getAllMarksByCourse(int courseId);

    List<Marks> getMarksByStudent(int studentId);

    double calculateStudentGpa(int studentId);

    List<EligibilityStatus> getOverallEligibilityByCourse(int courseId);
}
