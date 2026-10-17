package com.faculty.management.dao;

import com.faculty.management.model.Course;
import com.faculty.management.model.CourseMaterial;
import java.util.List;

/**
 * Data Access Object Interface for Academic Courses and Materials.
 */
public interface ICourseDAO {

    List<Course> getAllCourses();

    List<Course> getCoursesByLecturer(int lecturerId);

    List<Course> getCoursesByStudent(int studentId);

    List<Course> getAvailableCoursesForStudent(int studentId);

    boolean enrollStudent(int studentId, int courseId, String academicYear);

    boolean dropCourse(int studentId, int courseId);

    boolean isStudentEnrolled(int studentId, int courseId);

    boolean addCourse(Course course);

    boolean updateCourse(Course course);

    boolean deleteCourse(int courseId);

    int countCourses();

    List<CourseMaterial> getMaterialsByCourse(int courseId);

    boolean addCourseMaterial(CourseMaterial mat);

    boolean deleteCourseMaterial(int materialId);
}
