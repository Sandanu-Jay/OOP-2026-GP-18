package com.faculty.management.model;

/**
 * OOP CONCEPT: ENCAPSULATION & BUSINESS LOGIC
 * Represents marks for course components (Quizzes, Mid-Exam, Assignments, CA, Final Exam),
 * along with the resulting UGC Circular No. 12-2024 Grade and GPV.
 */
public class Marks {
    private int markId;
    private int studentId;
    private String studentRegNo;
    private String studentName;
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credit;

    private double quizMark;
    private double midMark;
    private double assignmentMark;
    private double caMark;
    private double finalExamMark;
    private double totalMark;
    private String grade;
    private double gpv;

    public Marks() {
        this.grade = "E";
        this.gpv = 0.0;
    }

    public Marks(int markId, int studentId, int courseId, double quizMark, 
                 double midMark, double assignmentMark, double caMark, 
                 double finalExamMark, double totalMark, String grade, double gpv) {
        this.markId = markId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.quizMark = quizMark;
        this.midMark = midMark;
        this.assignmentMark = assignmentMark;
        this.caMark = caMark;
        this.finalExamMark = finalExamMark;
        this.totalMark = totalMark;
        this.grade = grade;
        this.gpv = gpv;
    }

    /**
     * CA Eligibility Rule: CA mark must be >= 40.0%
     */
    public boolean isCaEligible() {
        return caMark >= 40.0;
    }

    public String getCaEligibilityStatus() {
        return isCaEligible() ? "ELIGIBLE (>= 40%)" : "NOT ELIGIBLE (< 40%)";
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public double getQuizMark() {
        return quizMark;
    }

    public void setQuizMark(double quizMark) {
        this.quizMark = quizMark;
    }

    public double getMidMark() {
        return midMark;
    }

    public void setMidMark(double midMark) {
        this.midMark = midMark;
    }

    public double getAssignmentMark() {
        return assignmentMark;
    }

    public void setAssignmentMark(double assignmentMark) {
        this.assignmentMark = assignmentMark;
    }

    public double getCaMark() {
        return caMark;
    }

    public void setCaMark(double caMark) {
        this.caMark = caMark;
    }

    public double getFinalExamMark() {
        return finalExamMark;
    }

    public void setFinalExamMark(double finalExamMark) {
        this.finalExamMark = finalExamMark;
    }

    public double getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(double totalMark) {
        this.totalMark = totalMark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGpv() {
        return gpv;
    }

    public void setGpv(double gpv) {
        this.gpv = gpv;
    }
}
