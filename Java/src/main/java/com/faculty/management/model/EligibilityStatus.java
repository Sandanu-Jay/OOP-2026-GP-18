/*
 * This class mainly stores EligibilityStatus data.
 */
package com.faculty.management.model;


public class EligibilityStatus {
    private int studentId;
    private String regNo;
    private String studentName;
    private int courseId;
    private String courseCode;
    private String courseName;
    
    private double caMark;
    private boolean caEligible;
    
    private double attendancePercentage;
    private boolean attendanceEligible;
    
    private boolean finalExamEligible;

    public EligibilityStatus() {
    }

    public EligibilityStatus(int studentId, String regNo, String studentName, 
                             int courseId, String courseCode, String courseName, 
                             double caMark, double attendancePercentage) {
        this.studentId = studentId;
        this.regNo = regNo;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.caMark = caMark;
        this.attendancePercentage = attendancePercentage;
        evaluate();
    }

    public void evaluate() {
        this.caEligible = this.caMark >= 40.0;
        this.attendanceEligible = this.attendancePercentage >= 80.0;
        this.finalExamEligible = this.caEligible && this.attendanceEligible;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
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

    public double getCaMark() {
        return caMark;
    }

    public void setCaMark(double caMark) {
        this.caMark = caMark;
        evaluate();
    }

    public boolean isCaEligible() {
        return caEligible;
    }

    public String getFormattedCaMark() {
        return String.format("%.1f", caMark);
    }

    public String getCaStatusText() {
        return caEligible ? "ELIGIBLE" : "NOT ELIGIBLE";
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
        evaluate();
    }

    public String getFormattedAttendance() {
        return String.format("%.1f%%", attendancePercentage);
    }

    public boolean isAttendanceEligible() {
        return attendanceEligible;
    }

    public String getAttendanceStatusText() {
        return attendanceEligible ? "ELIGIBLE" : "NOT ELIGIBLE";
    }

    public boolean isFinalExamEligible() {
        return finalExamEligible;
    }

    public String getFinalExamStatusText() {
        return finalExamEligible ? "ELIGIBLE" : "NOT ELIGIBLE";
    }
}
