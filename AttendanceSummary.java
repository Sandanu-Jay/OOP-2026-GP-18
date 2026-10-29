package com.faculty.management.model;

/**
 * OOP CONCEPT: ENCAPSULATION & BUSINESS LOGIC
 * Encapsulates the attendance calculations for 15 Theory & 15 Practical sessions (30 total),
 * including approved medical considerations and the 80% eligibility threshold.
 */
public class AttendanceSummary {
    private int studentId;
    private String regNo;
    private String studentName;
    private int courseId;
    private String courseCode;
    private String courseName;
    
    private int theoryAttended;      // out of 15
    private int practicalAttended;   // out of 15
    private int totalAttended;       // theory + practical
    private int totalSessions = 30;  // 15 theory + 15 practical
    private int approvedMedicals;    // number of approved medical sessions
    private int effectiveAttended;   // totalAttended + approvedMedicals
    private double rawPercentage;    // attendance % before medicals
    private double effectivePercentage; // attendance % after approved medicals
    private boolean eligible;        // effectivePercentage >= 80.0%

    public AttendanceSummary() {
    }

    public AttendanceSummary(int studentId, String regNo, String studentName, 
                             int courseId, String courseCode, String courseName, 
                             int theoryAttended, int practicalAttended, int approvedMedicals) {
        this.studentId = studentId;
        this.regNo = regNo;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.theoryAttended = theoryAttended;
        this.practicalAttended = practicalAttended;
        this.approvedMedicals = approvedMedicals;
        calculate();
    }

    // Encapsulated calculation logic
    public void calculate() {
        this.totalAttended = theoryAttended + practicalAttended;
        this.rawPercentage = (totalSessions > 0) ? ((double) totalAttended / totalSessions) * 100.0 : 0.0;
        
        // Effective attendance takes approved medicals into account
        this.effectiveAttended = Math.min(totalSessions, totalAttended + approvedMedicals);
        this.effectivePercentage = (totalSessions > 0) ? ((double) effectiveAttended / totalSessions) * 100.0 : 0.0;
        
        // 80% attendance rule
        this.eligible = this.effectivePercentage >= 80.0;
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

    public String getStudentRegNo() {
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

    public int getTheoryAttended() {
        return theoryAttended;
    }

    public int getTheoryCount() {
        return theoryAttended;
    }

    public void setTheoryAttended(int theoryAttended) {
        this.theoryAttended = theoryAttended;
        calculate();
    }

    public int getPracticalAttended() {
        return practicalAttended;
    }

    public int getPracticalCount() {
        return practicalAttended;
    }

    public void setPracticalAttended(int practicalAttended) {
        this.practicalAttended = practicalAttended;
        calculate();
    }

    public int getTotalAttended() {
        return totalAttended;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public int getApprovedMedicals() {
        return approvedMedicals;
    }

    public void setApprovedMedicals(int approvedMedicals) {
        this.approvedMedicals = approvedMedicals;
        calculate();
    }

    public int getEffectiveAttended() {
        return effectiveAttended;
    }

    public double getRawPercentage() {
        return rawPercentage;
    }

    public String getFormattedRawPercentage() {
        return String.format("%.1f%%", rawPercentage);
    }

    public double getEffectivePercentage() {
        return effectivePercentage;
    }

    public String getFormattedEffectivePercentage() {
        return String.format("%.1f%%", effectivePercentage);
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getEligibilityStatus() {
        return eligible ? "ELIGIBLE (>= 80%)" : "NOT ELIGIBLE (< 80%)";
    }

    public String getSummaryDescription() {
        if (effectivePercentage > 80.0 && approvedMedicals > 0) {
            return "> 80% with medicals";
        } else if (effectivePercentage > 80.0) {
            return "> 80% attendance";
        } else if (Math.abs(effectivePercentage - 80.0) < 0.001) {
            return "Exactly 80% attendance";
        } else if (approvedMedicals > 0) {
            return "< 80% with medicals";
        } else {
            return "< 80% without medicals";
        }
    }
}
