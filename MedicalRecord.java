package com.faculty.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * OOP CONCEPT: ENCAPSULATION
 * Represents an undergraduate medical certificate submission for missed sessions.
 */
public class MedicalRecord {
    private int medicalId;
    private int studentId;
    private String studentRegNo;
    private String studentName;
    private int courseId;
    private String courseCode;
    private String sessionType;     // 'THEORY' or 'PRACTICAL'
    private int sessionNumber;      // 1 to 15
    private LocalDate medicalDate;
    private String reason;          // Medical reason / justification per ER diagram
    private String documentInfo;    // e.g. Hospital Certificate #MC-8812
    private String isApproved;      // 'PENDING', 'APPROVED', 'REJECTED'
    private LocalDateTime submittedDate;
    private String comments;

    public MedicalRecord() {
        this.isApproved = "PENDING";
    }

    public MedicalRecord(int medicalId, int studentId, int courseId, String sessionType, 
                         int sessionNumber, LocalDate medicalDate, String documentInfo, 
                         String isApproved, String comments) {
        this(medicalId, studentId, courseId, sessionType, sessionNumber, medicalDate, "", documentInfo, isApproved, comments);
    }

    public MedicalRecord(int medicalId, int studentId, int courseId, String sessionType, 
                         int sessionNumber, LocalDate medicalDate, String reason, String documentInfo, 
                         String isApproved, String comments) {
        this.medicalId = medicalId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.sessionType = sessionType;
        this.sessionNumber = sessionNumber;
        this.medicalDate = medicalDate;
        this.reason = reason;
        this.documentInfo = documentInfo;
        this.isApproved = (isApproved != null) ? isApproved : "PENDING";
        this.comments = comments;
    }

    public int getMedicalId() {
        return medicalId;
    }

    public void setMedicalId(int medicalId) {
        this.medicalId = medicalId;
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

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public LocalDate getMedicalDate() {
        return medicalDate;
    }

    public void setMedicalDate(LocalDate medicalDate) {
        this.medicalDate = medicalDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDocumentInfo() {
        return documentInfo;
    }

    public void setDocumentInfo(String documentInfo) {
        this.documentInfo = documentInfo;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isApprovedStatus() {
        return "APPROVED".equalsIgnoreCase(isApproved);
    }
}
