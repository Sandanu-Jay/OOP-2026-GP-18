/*
 * EASY EVALUATION NOTE
 * This class mainly stores Course data.
 * Explain it using: fields -> constructor -> getters/setters -> important methods.
 */
package com.faculty.management.model;


public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credit;
    private String department;
    private int semester;
    private int level = 1;
    private Integer lecturerId;
    private String lecturerName; // For display convenience in TableViews

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName, int credit, 
                  String department, int semester, Integer lecturerId) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
        this.department = department;
        this.semester = semester;
        this.lecturerId = lecturerId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Integer getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Integer lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (Level " + level + ", Semester " + semester + ")";
    }
}
